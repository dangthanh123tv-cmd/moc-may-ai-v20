package com.mocmay.ai;

import android.content.*; import android.database.Cursor; import android.database.sqlite.*; import java.util.*;

public final class KnowledgeStore extends SQLiteOpenHelper {
    public KnowledgeStore(Context c){super(c,"mocmay_knowledge.db",null,1);}
    public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE knowledge(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT UNIQUE,content TEXT,created INTEGER)");d.execSQL("CREATE INDEX idx_knowledge_title ON knowledge(title)");}
    public void onUpgrade(SQLiteDatabase d,int a,int b){}
    public synchronized void put(String title,String content){if(title==null||title.trim().isEmpty())throw new IllegalArgumentException("Thiếu tiêu đề.");if(content==null)content="";if(content.length()>50000)throw new IllegalArgumentException("Nội dung quá dài.");getWritableDatabase().execSQL("INSERT OR REPLACE INTO knowledge(title,content,created) VALUES(?,?,?)",new Object[]{title.trim(),content.trim(),System.currentTimeMillis()});}
    public synchronized String list(){Cursor c=getReadableDatabase().rawQuery("SELECT title,created FROM knowledge ORDER BY created DESC LIMIT 100",null);StringBuilder b=new StringBuilder();try{while(c.moveToNext())b.append("• ").append(c.getString(0)).append("\n");}finally{c.close();}return b.length()==0?"Knowledge trống.":b.toString();}
    public synchronized String search(String q,int limit){String term=q==null?"":q.trim();if(term.isEmpty())return "";int n=Math.max(1,Math.min(10,limit));Cursor c=getReadableDatabase().rawQuery("SELECT title,content FROM knowledge WHERE title LIKE ? OR content LIKE ? ORDER BY created DESC LIMIT "+n,new String[]{"%"+term+"%","%"+term+"%"});StringBuilder b=new StringBuilder();try{while(c.moveToNext())b.append("[Knowledge: ").append(c.getString(0)).append("]\n").append(c.getString(1)).append("\n\n");}finally{c.close();}return b.toString();}
    public synchronized void clear(){getWritableDatabase().execSQL("DELETE FROM knowledge");}
}
