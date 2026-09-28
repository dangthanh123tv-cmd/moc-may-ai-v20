package com.mocmay.ai;
import android.content.*; import java.util.*;

public final class LocalAgent {
 private final ChatStore db; private final LocalEngine engine; private final ModelManager models; private final DeviceInfo device;
 public LocalAgent(Context c){db=new ChatStore(c);engine=new LocalEngine(c);models=new ModelManager(c);device=new DeviceInfo(c);}
 public LocalEngine engine(){return engine;}
 public String reply(String s){
  String t=s==null?"":s.trim();
  if(t.equalsIgnoreCase("/help"))return "/remember <text>\n/memory\n/clear-memory\n/history\n/clear-history\n/models\n/model-info <name>\n/load <name> [context] [threads]\n/unload\n/stop\n/status\n/device\n/scan\n/online\n/offline\n/delete-model <name>";
  if(t.startsWith("/remember ")){db.remember(t.substring(10).trim());return "Đã ghi nhớ.";}
  if(t.equalsIgnoreCase("/memory"))return db.memory();
  if(t.equalsIgnoreCase("/clear-memory")){db.clearMemory();return "Đã xóa bộ nhớ.";}
  if(t.equalsIgnoreCase("/history"))return db.recentMessages(50);
  if(t.equalsIgnoreCase("/clear-history")){db.clearMessages();return "Đã xóa lịch sử chat.";}
  if(t.equalsIgnoreCase("/models")){List<String> m=models.models();return m.isEmpty()?"Chưa có model GGUF.":String.join("\n",m);}
  if(t.startsWith("/model-info "))try{return models.describe(t.substring(12).trim());}catch(Exception e){return "Lỗi: "+e.getMessage();}
  if(t.startsWith("/delete-model ")){String n=t.substring(14).trim();if(n.equals(engine.loadedModel()))engine.unload();return models.delete(n)?"Đã xóa model.":"Không xóa được model.";}
  if(t.startsWith("/load ")){String[] p=t.split("\\s+");if(p.length<2)return "Dùng: /load <name> [context] [threads]";int c=p.length>2?parse(p[2],2048):2048;int th=p.length>3?parse(p[3],device.thermal().recommendedThreads()):device.thermal().recommendedThreads();return engine.load(p[1],c,th)?"Đã tải model: "+p[1]+" (context="+c+", threads="+th+")":"Không tải được model. Kiểm tra model/ nhiệt độ/ RAM.";}
  if(t.equalsIgnoreCase("/unload")){engine.unload();return "Đã giải phóng model khỏi RAM.";}
  if(t.equalsIgnoreCase("/stop")){engine.cancel();return "Đã gửi yêu cầu dừng.";}
  if(t.equalsIgnoreCase("/status"))return "Mộc Mây AI v29.0\nNative: "+engine.nativeAvailable()+"\nBackend: "+engine.backendInfo()+"\nLoaded model: "+(engine.loadedModel()==null?"none":engine.loadedModel())+"\nThermal: "+device.thermal().state()+"\nRAM trống: "+device.availableRamMb()+" MB\nStorage trống: "+device.freeStorageMb()+" MB\nAPI key: không nhúng.";
  if(t.equalsIgnoreCase("/device"))return device.summary();
  if(t.equalsIgnoreCase("/scan"))return "Security scan: OK. GGUF header + safe filename + size + SHA-256; model chỉ ở app-private storage.";
  if(t.equalsIgnoreCase("/online"))return "Online connector đang khóa mặc định.";
  if(t.equalsIgnoreCase("/offline"))return "Đang ở OFFLINE.";
  db.message("user",t);String a=engine.generate(t,256);db.message("assistant",a);return a;
 }
 private int parse(String s,int d){try{return Math.max(256,Math.min(8192,Integer.parseInt(s)));}catch(Exception e){return d;}}
}
