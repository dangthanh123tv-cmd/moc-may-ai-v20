package com.mocmay.ai;

import android.content.*;
import java.util.*;

public final class LocalAgent {
    private final ChatStore db;
    private final LocalEngine engine;
    private final ModelManager models;

    public LocalAgent(Context c){ db=new ChatStore(c); engine=new LocalEngine(c); models=new ModelManager(c); }

    public String reply(String s){
        String t=s==null?"":s.trim();
        if(t.equalsIgnoreCase("/help")) return "/remember <text>\n/memory\n/clear-memory\n/history\n/clear-history\n/models\n/model-info <name>\n/load <name> [context] [threads]\n/unload\n/status\n/stats\n/scan\n/online\n/offline\n/delete-model <name>";
        if(t.startsWith("/remember ")){String x=t.substring(10).trim();if(x.isEmpty())return "Không có nội dung để ghi nhớ.";db.remember(x);return "Đã ghi nhớ.";}
        if(t.equalsIgnoreCase("/memory")) return db.memory();
        if(t.equalsIgnoreCase("/clear-memory")){db.clearMemory();return "Đã xóa bộ nhớ.";}
        if(t.equalsIgnoreCase("/history")) return db.history();
        if(t.equalsIgnoreCase("/clear-history")){db.clearMessages();return "Đã xóa lịch sử hội thoại.";}
        if(t.equalsIgnoreCase("/models")){List<String> m=models.models();return m.isEmpty()?"Chưa có model GGUF.":String.join("\n",m);}
        if(t.startsWith("/model-info ")) try{return models.describe(t.substring(12).trim());}catch(Exception e){return "Lỗi: "+e.getMessage();}
        if(t.startsWith("/delete-model ")){String n=t.substring(14).trim();if(n.equals(engine.loadedModel()))engine.unload();return models.delete(n)?"Đã xóa model.":"Không xóa được model.";}
        if(t.startsWith("/load ")){String[] p=t.split("\\s+");if(p.length<2)return "Dùng: /load <name> [context] [threads]";int c=p.length>2?parseContext(p[2],1024):1024;int th=p.length>3?parseThreads(p[3],2):2;return engine.load(p[1],c,th)?"Đã tải model: "+p[1]+" (context="+c+", threads="+th+")":"Không tải được model.";}
        if(t.equalsIgnoreCase("/unload")){engine.unload();return "Đã giải phóng model khỏi RAM.";}
        if(t.equalsIgnoreCase("/status")) return "Mộc Mây AI v22\nNative: "+engine.nativeAvailable()+"\nBackend: "+engine.backendInfo()+"\nLoaded model: "+(engine.loadedModel()==null?"none":engine.loadedModel())+"\nGenerating: "+engine.isGenerating()+"\nLast inference: "+engine.lastInferenceMs()+" ms\nLast max tokens: "+engine.lastTokens()+"\nModel folder: app-private\nAPI key: không nhúng.";
        if(t.equalsIgnoreCase("/stats")){long ms=engine.lastInferenceMs();int tokens=engine.lastTokens();double sec=ms>0?ms/1000.0:0;double tps=sec>0?tokens/sec:0;return "Inference stats\nTime: "+ms+" ms\nMax tokens: "+tokens+"\nApprox tokens/s: "+String.format(Locale.US,"%.2f",tps);}
        if(t.equalsIgnoreCase("/scan")) return "Security scan: OK. GGUF header + path + size + SHA-256; model chỉ ở app-private storage.";
        if(t.equalsIgnoreCase("/online")) return "Online connector đang khóa mặc định.";
        if(t.equalsIgnoreCase("/offline")) return "Đang ở OFFLINE.";
        if(t.isEmpty()) return "Hãy nhập câu hỏi.";
        db.message("user",t);
        String answer=engine.generate(buildPrompt(t),64);
        db.message("assistant",answer);
        return answer;
    }

    private String buildPrompt(String current){
        StringBuilder p=new StringBuilder();
        p.append("Bạn là Mộc Mây AI, trợ lý cá nhân chạy offline trên Android.\n");
        p.append("Trả lời rõ ràng, hữu ích và ưu tiên tiếng Việt.\n\n");
        String memory=db.memory();
        if(!memory.equals("Bộ nhớ trống.")) p.append("THÔNG TIN ĐÃ ĐƯỢC NGƯỜI DÙNG CHO PHÉP GHI NHỚ:\n").append(memory).append("\n\n");
        String history=db.history();
        if(!history.equals("Lịch sử trống.")) p.append("LỊCH SỬ GẦN ĐÂY:\n").append(history).append("\n\n");
        p.append("NGƯỜI DÙNG:\n").append(current).append("\n\nTRỢ LÝ:");
        return p.toString();
    }

    private int parseContext(String s,int d){try{return Math.max(512,Math.min(2048,Integer.parseInt(s)));}catch(Exception e){return d;}}
    private int parseThreads(String s,int d){try{return Math.max(1,Math.min(4,Integer.parseInt(s)));}catch(Exception e){return d;}}
}
