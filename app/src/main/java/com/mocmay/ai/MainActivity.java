package com.mocmay.ai;

import android.app.*; import android.os.*; import android.content.*; import android.net.Uri; import android.view.*; import android.view.inputmethod.InputMethodManager; import android.widget.*; import java.util.concurrent.*;

public class MainActivity extends Activity {
 private TextView chat,mode; private EditText input; private LocalAgent agent; private ModelManager models; private ExecutorService executor;
 static final int PICK_FILE=42,PICK_MODEL=43;
 @Override public void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_main);executor=Executors.newSingleThreadExecutor();agent=new LocalAgent(this);models=new ModelManager(this);chat=findViewById(R.id.chat);mode=findViewById(R.id.mode);input=findViewById(R.id.input);
  findViewById(R.id.send).setOnClickListener(v->send()); findViewById(R.id.stop).setOnClickListener(v->{agent.engine().cancel();append("Hệ thống","Đã gửi yêu cầu dừng.");});
  findViewById(R.id.scan).setOnClickListener(v->append("AI",agent.reply("/scan"))); findViewById(R.id.settings).setOnClickListener(v->showMenu()); findViewById(R.id.attach).setOnClickListener(v->pickFile());
  input.setOnEditorActionListener((v,a,e)->{send();return true;}); handleShare(getIntent());
  if(b==null){append("AI","Xin chào! Mộc Mây AI v29.0 • Offline-first • GGUF/llama.cpp • Thermal Guard.");}
  refreshMode();
 }
 @Override protected void onDestroy(){if(executor!=null)executor.shutdownNow();super.onDestroy();}
 private void refreshMode(){mode.setText("OFFLINE • "+(agent.engine().nativeAvailable()?"Native ready":"Fallback")+" • "+new DeviceInfo(this).thermal().state());}
 private void send(){String s=input.getText().toString().trim();if(s.isEmpty())return;append("Bạn",s);input.setText("");findViewById(R.id.send).setEnabled(false);findViewById(R.id.stop).setEnabled(true);hideKeyboard();
  executor.execute(()->{String a=agent.reply(s);runOnUiThread(()->{append("AI",a);refreshMode();findViewById(R.id.send).setEnabled(true);findViewById(R.id.stop).setEnabled(false);});});
 }
 private void hideKeyboard(){((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(input.getWindowToken(),0);}
 private void append(String w,String s){chat.append("\n"+w+": "+s+"\n");}
 private void pickFile(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");startActivityForResult(i,PICK_FILE);}
 private void pickModel(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");startActivityForResult(i,PICK_MODEL);}
 @Override protected void onActivityResult(int r,int code,Intent d){super.onActivityResult(r,code,d);if(code!=RESULT_OK||d==null||d.getData()==null)return;Uri u=d.getData();if(r==PICK_FILE)append("Hệ thống","Đã chọn: "+u);else if(r==PICK_MODEL){String name="model.gguf";try{android.database.Cursor c=getContentResolver().query(u,null,null,null,null);if(c!=null&&c.moveToFirst()){int x=c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);if(x>=0)name=c.getString(x);}if(c!=null)c.close();String finalName=name;append("Model","Đang import: "+finalName);executor.execute(()->{try{models.importFromUri(getContentResolver(),u,finalName);runOnUiThread(()->append("Model","Đã import: "+finalName+"\nDùng /load "+finalName+" để nạp vào RAM."));}catch(Exception e){runOnUiThread(()->append("Model","Import thất bại: "+e.getMessage()));}});}catch(Exception e){append("Model","Không đọc được file: "+e.getMessage());}}}
 private void handleShare(Intent i){if(i==null)return;if(Intent.ACTION_SEND.equals(i.getAction())){Uri u=i.getParcelableExtra(Intent.EXTRA_STREAM);if(u!=null)append("Share","Đã nhận file: "+u);String t=i.getStringExtra(Intent.EXTRA_TEXT);if(t!=null)input.setText(t);}else if(Intent.ACTION_SEND_MULTIPLE.equals(i.getAction()))append("Share","Đã nhận nhiều file.");}
 private void showMenu(){new AlertDialog.Builder(this).setTitle("Mộc Mây AI v29.0").setItems(new String[]{"Import model GGUF","Danh sách model","Trạng thái","Thiết bị & nhiệt","Lịch sử chat","Xóa lịch sử","Hướng dẫn /help","Online connector"},(d,w)->{if(w==0)pickModel();else if(w==1)append("Model",agent.reply("/models"));else if(w==2)showText("Status",agent.reply("/status"));else if(w==3)showText("Thiết bị",agent.reply("/device"));else if(w==4)showText("Lịch sử",agent.reply("/history"));else if(w==5)new AlertDialog.Builder(this).setTitle("Xóa lịch sử?").setMessage("Chỉ xóa lịch sử chat, không xóa model hay bộ nhớ đã ghi nhớ.").setNegativeButton("Hủy",null).setPositiveButton("Xóa",(x,y)->append("Hệ thống",agent.reply("/clear-history"))).show();else if(w==6)append("AI",agent.reply("/help"));else showText("Online","Tắt mặc định. Không nhúng API key vào APK.");}).show();}
 private void showText(String title,String msg){new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton("OK",null).show();}
}
