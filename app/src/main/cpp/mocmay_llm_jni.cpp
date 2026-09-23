#include <jni.h>
#include <string>
#include <fstream>
#include <mutex>
#include <vector>
#include <algorithm>
#include <cstdint>

#if MOCMAY_HAS_LLAMA
#include "llama.h"
#endif

static bool isGguf(const std::string &p) {
    std::ifstream f(p, std::ios::binary);
    char m[4]{};
    return f.good() && f.read(m, 4) && m[0]=='G' && m[1]=='G' && m[2]=='U' && m[3]=='F';
}

#if MOCMAY_HAS_LLAMA
struct Engine {
    llama_model * model = nullptr;
    llama_context * ctx = nullptr;
    llama_sampler * sampler = nullptr;
    const llama_vocab * vocab = nullptr;
    std::mutex mu;
};
static Engine g;
#endif

static std::string jstr(JNIEnv *env, jstring s) {
    if (!s) return {};
    const char *p = env->GetStringUTFChars(s, nullptr);
    std::string out = p ? p : "";
    if (p) env->ReleaseStringUTFChars(s, p);
    return out;
}
static jstring out(JNIEnv *env, const std::string &s) { return env->NewStringUTF(s.c_str()); }

extern "C" JNIEXPORT jboolean JNICALL
Java_com_mocmay_ai_NativeLlmBridge_nativeModelReadable(JNIEnv *env, jobject, jstring jp) {
    std::string p = jstr(env, jp);
    std::ifstream f(p, std::ios::binary);
    return f.good() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_mocmay_ai_NativeLlmBridge_nativeIsGguf(JNIEnv *env, jobject, jstring jp) {
    return isGguf(jstr(env, jp)) ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mocmay_ai_NativeLlmBridge_nativeBackendInfo(JNIEnv *env, jobject) {
#if MOCMAY_HAS_LLAMA
    return out(env, "llama.cpp 0.4.1 integration; arm64 CPU; GGUF; runtime enabled");
#else
    return out(env, "Native bridge only; llama.cpp not bundled in this build");
#endif
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_mocmay_ai_NativeLlmBridge_nativeLoadModel(JNIEnv *env, jobject, jstring jp, jint context, jint threads) {
#if !MOCMAY_HAS_LLAMA
    (void)env; (void)jp; (void)context; (void)threads; return JNI_FALSE;
#else
    std::lock_guard<std::mutex> lock(g.mu);
    std::string path = jstr(env, jp);
    if (!isGguf(path)) return JNI_FALSE;
    if (g.sampler) { llama_sampler_free(g.sampler); g.sampler=nullptr; }
    if (g.ctx) { llama_free(g.ctx); g.ctx=nullptr; }
    if (g.model) { llama_model_free(g.model); g.model=nullptr; }
    ggml_backend_load_all();
    auto mp = llama_model_default_params();
    mp.n_gpu_layers = 0;
    g.model = llama_model_load_from_file(path.c_str(), mp);
    if (!g.model) return JNI_FALSE;
    g.vocab = llama_model_get_vocab(g.model);
    auto cp = llama_context_default_params();
    cp.n_ctx = (uint32_t)std::max(256, std::min(8192, (int)context));
    cp.n_batch = std::min<uint32_t>(cp.n_ctx, 512);
    cp.n_threads = std::max(1, std::min(8, (int)threads));
    cp.n_threads_batch = cp.n_threads;
    g.ctx = llama_init_from_model(g.model, cp);
    if (!g.ctx) { llama_model_free(g.model); g.model=nullptr; return JNI_FALSE; }
    auto sp = llama_sampler_chain_default_params();
    g.sampler = llama_sampler_chain_init(sp);
    if (!g.sampler) { llama_free(g.ctx); llama_model_free(g.model); g.ctx=nullptr; g.model=nullptr; return JNI_FALSE; }
    llama_sampler_chain_add(g.sampler, llama_sampler_init_greedy());
    return JNI_TRUE;
#endif
}

extern "C" JNIEXPORT void JNICALL
Java_com_mocmay_ai_NativeLlmBridge_nativeUnloadModel(JNIEnv *, jobject) {
#if MOCMAY_HAS_LLAMA
    std::lock_guard<std::mutex> lock(g.mu);
    if (g.sampler) { llama_sampler_free(g.sampler); g.sampler=nullptr; }
    if (g.ctx) { llama_free(g.ctx); g.ctx=nullptr; }
    if (g.model) { llama_model_free(g.model); g.model=nullptr; }
    g.vocab=nullptr;
#endif
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_mocmay_ai_NativeLlmBridge_nativeGenerate(JNIEnv *env, jobject, jstring jp, jint maxTokens) {
#if !MOCMAY_HAS_LLAMA
    (void)env; (void)jp; (void)maxTokens;
    return out(env, "__NATIVE_RUNTIME_MISSING__");
#else
    std::lock_guard<std::mutex> lock(g.mu);

    if (!g.model || !g.vocab) {
        return out(env, "__MODEL_NOT_LOADED__");
    }

    std::string user_prompt = jstr(env, jp);
    if (user_prompt.empty()) {
        return out(env, "__EMPTY_PROMPT__");
    }

    // Recreate the context for every request.
    // This prevents previous KV-cache state from leaking into a new chat turn.
    if (g.sampler) {
        llama_sampler_free(g.sampler);
        g.sampler = nullptr;
    }
    if (g.ctx) {
        llama_free(g.ctx);
        g.ctx = nullptr;
    }

    auto cp = llama_context_default_params();
    cp.n_ctx = 2048;
    cp.n_batch = 512;
    cp.n_threads = 4;
    cp.n_threads_batch = 4;

    g.ctx = llama_init_from_model(g.model, cp);
    if (!g.ctx) {
        return out(env, "__CONTEXT_CREATE_FAILED__");
    }

    auto sp = llama_sampler_chain_default_params();
    g.sampler = llama_sampler_chain_init(sp);
    if (!g.sampler) {
        llama_free(g.ctx);
        g.ctx = nullptr;
        return out(env, "__SAMPLER_CREATE_FAILED__");
    }

    llama_sampler_chain_add(g.sampler, llama_sampler_init_greedy());

    // Use the chat template stored in the GGUF metadata.
    // Qwen2.5-Instruct uses a ChatML-style template.
    llama_chat_message messages[2];
    messages[0].role = "system";
    messages[0].content =
        "Bạn là Mộc Mây AI, một trợ lý AI chạy offline trên Android. "
        "Hãy trả lời rõ ràng, hữu ích và bằng tiếng Việt khi người dùng hỏi bằng tiếng Việt.";

    messages[1].role = "user";
    messages[1].content = user_prompt.c_str();

    int32_t needed = llama_chat_apply_template(
        nullptr,
        messages,
        2,
        true,
        nullptr,
        0
    );

    if (needed < 0) {
        return out(env, "__CHAT_TEMPLATE_FAILED__");
    }

    std::vector<char> formatted((size_t)needed + 1);

    int32_t written = llama_chat_apply_template(
        nullptr,
        messages,
        2,
        true,
        formatted.data(),
        (int32_t)formatted.size()
    );

    if (written < 0) {
        return out(env, "__CHAT_TEMPLATE_FAILED__");
    }

    std::string prompt(formatted.data(), (size_t)written);

    // Parse special tokens generated by the chat template.
    int n = -llama_tokenize(
        g.vocab,
        prompt.c_str(),
        prompt.size(),
        nullptr,
        0,
        false,
        true
    );

    if (n <= 0) {
        return out(env, "__TOKENIZE_FAILED__");
    }

    std::vector<llama_token> toks((size_t)n);

    int token_count = llama_tokenize(
        g.vocab,
        prompt.c_str(),
        prompt.size(),
        toks.data(),
        (int)toks.size(),
        false,
        true
    );

    if (token_count < 0) {
        return out(env, "__TOKENIZE_FAILED__");
    }

    toks.resize((size_t)token_count);

    llama_batch batch = llama_batch_get_one(
        toks.data(),
        (int)toks.size()
    );

    if (llama_decode(g.ctx, batch) != 0) {
        return out(env, "__DECODE_FAILED__");
    }

    std::string result;

    int limit = std::max(
        1,
        std::min(512, (int)maxTokens)
    );

    for (int i = 0; i < limit; ++i) {
        llama_token tok = llama_sampler_sample(
            g.sampler,
            g.ctx,
            -1
        );

        if (llama_vocab_is_eog(g.vocab, tok)) {
            break;
        }

        char buf[1024];

        int len = llama_token_to_piece(
            g.vocab,
            tok,
            buf,
            sizeof(buf),
            0,
            true
        );

        if (len < 0) {
            return out(env, "__TOKEN_PIECE_FAILED__");
        }

        result.append(buf, (size_t)len);

        llama_sampler_accept(g.sampler, tok);

        batch = llama_batch_get_one(&tok, 1);

        if (llama_decode(g.ctx, batch) != 0) {
            break;
        }
    }

    if (result.empty()) {
        return out(env, "__EMPTY_GENERATION__");
    }

    return out(env, result);
#endif
}
