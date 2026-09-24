package com.mocmay.ai;
import android.content.*;
import android.database.sqlite.*;
import android.database.Cursor;
import java.util.*;
public class ChatStore extends SQLiteOpenHelper {
  public ChatStore(Context c){super(c,"mocmay.db",null,2);}
  public void onCreate(SQLiteDatabase d){
    d.execSQL("CREATE TABLE memory(id INTEGER PRIMARY KEY AUTOINCREMENT,text TEXT UNIQUE,created INTEGER)");
    d.execSQL("CREATE TABLE messages(id INTEGER PRIMARY KEY AUTOINCREMENT,role TEXT,content TEXT,created INTEGER)");
  }
  public void onUpgrade(SQLiteDatabase d,int a,int b){ if(a<2) d.execSQL("CREATE INDEX IF NOT EXISTS idx_memory_created ON memory(created)");}
  public void remember(String s){getWritableDatabase().execSQL("INSERT OR IGNORE INTO memory(text,created) VALUES(?,?)",new Object[]{s,System.currentTimeMillis()});}
  public String memory(){
    Cursor c=getReadableDatabase().rawQuery("SELECT id,text FROM memory ORDER BY id DESC LIMIT 50",null);
    StringBuilder x=new StringBuilder();
    while(c.moveToNext()) x.append(c.getInt(0)).append(". ").append(c.getString(1)).append("\n");
    c.close(); return x.length()==0?"Bộ nhớ trống.":x.toString();
  }
  public void message(String role,String s){getWritableDatabase().execSQL("INSERT INTO messages(role,content,created) VALUES(?,?,?)",new Object[]{role,s,System.currentTimeMillis()});}
  public String history(){
    Cursor c=getReadableDatabase().rawQuery("SELECT role,content FROM messages ORDER BY id DESC LIMIT 12",null);
    ArrayList<String> rows=new ArrayList<>();
    while(c.moveToNext()) rows.add(c.getString(0)+": "+c.getString(1));
    c.close();
    if(rows.isEmpty()) return "Lịch sử trống.";
    Collections.reverse(rows);
    StringBuilder x=new StringBuilder();
    for(String r:rows) x.append(r).append("\\n");
    return x.toString();
  }
  public void clearMessages(){getWritableDatabase().execSQL("DELETE FROM messages");}

  public void clearMemory(){getWritableDatabase().execSQL("DELETE FROM memory");}
}
