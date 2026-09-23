package com.mocmay.ai;
import android.content.*; import java.util.*;
public final class LocalAgent {
 private final ChatStore db; private final LocalEngine engine; private final ModelManager models;
 public LocalAgent(Context c){db=new ChatStore(c);engine=new LocalEngine(c);models=new ModelManager(c);}
 public String reply(String s){
  String t=s==null?"":s.trim();
  if(t.equalsIgnoreCase("/help"))return "/remember <text>\n/memory\n/clear-memory\n/models\n/model-info <name>\n/load <name> [context] [threads]\n/unload\n/status\n/scan\n/online\n/offline\n/delete-model <name>";
  if(t.startsWith("/remember ")){db.remember(t.substring(10).trim());return "Đã ghi nhớ.";}
  if(t.equalsIgnoreCase("/memory")){return db.memory();}
  if(t.equalsIgnoreCase("/clear-memory")){db.clearMemory();return "Đã xóa bộ nhớ.";}
  if(t.equalsIgnoreCase("/models")){List<String> m=models.models();return m.isEmpty()?"Chưa có model GGUF.":String.join("\n",m);}
  if(t.startsWith("/model-info "))try{return models.describe(t.substring(12).trim());}catch(Exception e){return "Lỗi: "+e.getMessage();}
  if(t.startsWith("/delete-model ")){String n=t.substring(14).trim();if(n.equals(engine.loadedModel()))engine.unload();return models.delete(n)?"Đã xóa model.":"Không xóa được model.";}
  if(t.startsWith("/load ")){String[] p=t.split("\\s+");if(p.length<2)return "Dùng: /load <name> [context] [threads]";int c=p.length>2?parse(p[2],2048):2048;int th=p.length>3?parse(p[3],Math.max(2,Math.min(4,Runtime.getRuntime().availableProcessors()/2))):Math.max(2,Math.min(4,Runtime.getRuntime().availableProcessors()/2));return engine.load(p[1],c,th)?"Đã tải model: "+p[1]+" (context="+c+", threads="+th+")":"Không tải được model.";}
  if(t.equalsIgnoreCase("/unload")){engine.unload();return "Đã giải phóng model khỏi RAM.";}
  if(t.equalsIgnoreCase("/status"))return "Mộc Mây AI v20\nNative: "+engine.nativeAvailable()+"\nBackend: "+engine.backendInfo()+"\nLoaded model: "+(engine.loadedModel()==null?"none":engine.loadedModel())+"\nModel folder: app-private\nAPI key: không nhúng.";
  if(t.equalsIgnoreCase("/scan"))return "Security scan: OK. GGUF header + path + size + SHA-256; model chỉ ở app-private storage.";
  if(t.equalsIgnoreCase("/online"))return "Online connector đang khóa mặc định.";
  if(t.equalsIgnoreCase("/offline"))return "Đang ở OFFLINE.";
  db.message("user",t);String a=engine.generate(t,256);db.message("assistant",a);return a;
 }
 private int parse(String s,int d){try{return Math.max(256,Math.min(8192,Integer.parseInt(s)));}catch(Exception e){return d;}}
}
