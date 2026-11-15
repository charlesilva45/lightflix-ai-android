package com.lightflix.ai;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.os.Build;
import android.widget.*;
import java.io.InputStream;
import java.io.OutputStream;
public class MainActivity extends Activity {
 private static final int PICK=1;
 private ImageView preview;
 private Bitmap original, enhanced;
 public void onCreate(Bundle b) { super.onCreate(b);
 LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(24,24,24,24);
 TextView title=new TextView(this);title.setText("Lightflix AI • versão 0.1");title.setTextSize(23);root.addView(title);
 TextView note=new TextView(this);note.setText("Ajustes locais básicos. A galeria automática e IA avançada ainda não estão disponíveis.");root.addView(note);
 preview=new ImageView(this);root.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
 Button pick=new Button(this);pick.setText("Escolher foto");root.addView(pick);
 Button improve=new Button(this);improve.setText("Melhorar iluminação e contraste");root.addView(improve);
 Button save=new Button(this);save.setText("Salvar cópia");root.addView(save);
 setContentView(root);
 pick.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK);});
 improve.setOnClickListener(v->{if(original==null){toast("Selecione uma foto");return;}enhanced=adjust(original);preview.setImageBitmap(enhanced);});
 save.setOnClickListener(v->{if(enhanced==null){toast("Melhore uma foto primeiro");return;}try{ContentValues cv=new ContentValues();cv.put(MediaStore.Images.Media.DISPLAY_NAME,"Lightflix_"+System.currentTimeMillis()+".jpg");cv.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");cv.put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Lightflix");Uri uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);if(uri==null)throw new Exception("Não foi possível criar imagem");try(OutputStream os=getContentResolver().openOutputStream(uri)){if(os==null||!enhanced.compress(Bitmap.CompressFormat.JPEG,92,os))throw new Exception("Falha ao salvar");}toast("Cópia salva em Pictures/Lightflix");}catch(Exception e){toast("Erro: "+e.getMessage());}});
 }
 protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==PICK&&result==RESULT_OK&&data!=null){try(InputStream in=getContentResolver().openInputStream(data.getData())){BitmapFactory.Options o=new BitmapFactory.Options();o.inSampleSize=2;original=BitmapFactory.decodeStream(in,null,o);if(original==null)throw new Exception("Imagem inválida");enhanced=null;preview.setImageBitmap(original);}catch(Exception e){toast("Erro ao abrir foto");}}}
 private Bitmap adjust(Bitmap src){int w=src.getWidth(),h=src.getHeight();Bitmap out=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);int[] row=new int[w];for(int y=0;y<h;y++){src.getPixels(row,0,w,0,y,w,1);for(int x=0;x<w;x++){int c=row[x];int r=clamp((int)((Color.red(c)-128)*1.08+140));int g=clamp((int)((Color.green(c)-128)*1.08+140));int b=clamp((int)((Color.blue(c)-128)*1.08+140));row[x]=Color.argb(Color.alpha(c),r,g,b);}out.setPixels(row,0,w,0,y,w,1);}return out;}
 private int clamp(int v){return Math.max(0,Math.min(255,v));}
 private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
