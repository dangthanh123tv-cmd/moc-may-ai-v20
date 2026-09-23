package com.mocmay.ai;

import android.content.*; import android.net.Uri; import java.io.*; import java.security.*; import java.util.*;

public final class ModelManager {
    private final File dir;
    private static final long MAX_MODEL_BYTES = 6L*1024*1024*1024;
    private static final byte[] GGUF = {'G','G','U','F'};
    public ModelManager(Context c){ dir=new File(c.getFilesDir(),"models"); if(!dir.exists()) dir.mkdirs(); }
    public File directory(){return dir;}
    public List<String> models(){ ArrayList<String> o=new ArrayList<>(); File[] fs=dir.listFiles(); if(fs!=null) for(File f:fs) if(f.isFile()&&safeName(f.getName())&&f.getName().toLowerCase(Locale.ROOT).endsWith(".gguf")) o.add(f.getName()); Collections.sort(o); return o; }
    public boolean exists(String name){return safeName(name)&&new File(dir,name).isFile();}
    public File file(String name)throws IOException{if(!safeName(name))throw new IOException("Tên model không hợp lệ."); File f=new File(dir,name); if(!f.isFile())throw new IOException("Không tìm thấy model."); return f;}
    public boolean importFromUri(ContentResolver cr, Uri uri, String name)throws IOException{
        if(!safeName(name)||!name.toLowerCase(Locale.ROOT).endsWith(".gguf")) throw new IOException("Chỉ nhận model GGUF có tên file an toàn.");
        File dst=new File(dir,name), tmp=new File(dir,"."+name+".part"), bak=new File(dir,"."+name+".bak"); long total=0;
        try(InputStream in=cr.openInputStream(uri); OutputStream out=new BufferedOutputStream(new FileOutputStream(tmp))){
            if(in==null) throw new IOException("Không mở được file."); byte[] b=new byte[1024*1024]; int n;
            while((n=in.read(b))!=-1){ total+=n; if(total>MAX_MODEL_BYTES) throw new IOException("Model vượt giới hạn 6 GB."); out.write(b,0,n); }
        } catch(Exception e){tmp.delete(); if(e instanceof IOException) throw (IOException)e; throw new IOException(e);}
        if(!isGguf(tmp)){tmp.delete();throw new IOException("File không có GGUF magic header hợp lệ.");}
        if(dst.exists() && !dst.renameTo(bak)){tmp.delete();throw new IOException("Không tạo được bản sao lưu model cũ.");}
        if(!tmp.renameTo(dst)){ if(bak.exists()) bak.renameTo(dst); tmp.delete(); throw new IOException("Không hoàn tất import model."); }
        bak.delete(); return true;
    }
    public boolean isGguf(File f)throws IOException{try(InputStream in=new BufferedInputStream(new FileInputStream(f))){for(byte x:GGUF)if(in.read()!=x)return false;return true;}}
    public String sha256(File f)throws Exception{MessageDigest md=MessageDigest.getInstance("SHA-256");try(InputStream in=new BufferedInputStream(new FileInputStream(f))){byte[] b=new byte[1024*1024];int n;while((n=in.read(b))!=-1)md.update(b,0,n);}StringBuilder s=new StringBuilder();for(byte x:md.digest())s.append(String.format(Locale.ROOT,"%02x",x));return s.toString();}
    public String describe(String name)throws Exception{File f=file(name);return name+"\nSize: "+f.length()+" bytes\nGGUF: "+isGguf(f)+"\nSHA-256: "+sha256(f);}
    public boolean delete(String name){return safeName(name)&&new File(dir,name).delete();}
    private boolean safeName(String n){return n!=null&&!n.isEmpty()&&n.length()<180&&!n.contains("/")&&!n.contains("\\")&&!n.contains("..")&&!n.startsWith(".");}
}
