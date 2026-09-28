package com.mocmay.ai;
import android.content.*; import android.database.sqlite.*; import android.database.Cursor; import java.util.*;

public class ChatStore extends SQLiteOpenHelper {
  public ChatStore(Context c){super(c,"mocmay.db",null,3);}
  public void onCreate(SQLiteDatabase d){
    d.execSQL("CREATE TABLE memory(id INTEGER PRIMARY KEY AUTOINCREMENT,text TEXT UNIQUE,created INTEGER)");
    d.execSQL("CREATE TABLE messages(id INTEGER PRIMARY KEY AUTOINCREMENT,role TEXT,content TEXT,created INTEGER)");
    d.execSQL("CREATE INDEX idx_messages_created ON messages(created)");
    d.execSQL("CREATE INDEX idx_memory_created ON memory(created)");
  }
  public void onUpgrade(SQLiteDatabase d,int a,int b){
    if(a<2) d.execSQL("CREATE INDEX IF NOT EXISTS idx_memory_created ON memory(created)");
    if(a<3) d.execSQL("CREATE INDEX IF NOT EXISTS idx_messages_created ON messages(created)");
  }
  public synchronized void remember(String s){getWritableDatabase().execSQL("INSERT OR IGNORE INTO memory(text,created) VALUES(?,?)",new Object[]{s,System.currentTimeMillis()});}
  public synchronized String memory(){
    Cursor c=getReadableDatabase().rawQuery("SELECT id,text FROM memory ORDER BY id DESC LIMIT 50",null); StringBuilder x=new StringBuilder();
    try{while(c.moveToNext()) x.append(c.getInt(0)).append(". ").append(c.getString(1)).append("\n");}finally{c.close();}
    return x.length()==0?"Bộ nhớ trống.":x.toString();
  }
  public synchronized void message(String role,String s){getWritableDatabase().execSQL("INSERT INTO messages(role,content,created) VALUES(?,?,?)",new Object[]{role,s,System.currentTimeMillis()});}
  public synchronized String recentMessages(int limit){
    int n=Math.max(1,Math.min(100,limit));
    Cursor c=getReadableDatabase().rawQuery("SELECT role,content FROM messages ORDER BY id DESC LIMIT "+n,null); ArrayList<String> rows=new ArrayList<>();
    try{while(c.moveToNext()) rows.add(c.getString(0)+": "+c.getString(1));}finally{c.close();}
    StringBuilder out=new StringBuilder(); for(int i=rows.size()-1;i>=0;i--) out.append(rows.get(i)).append("\n"); return out.toString();
  }
  public synchronized void clearMessages(){getWritableDatabase().execSQL("DELETE FROM messages");}
  public synchronized void clearMemory(){getWritableDatabase().execSQL("DELETE FROM memory");}
}
