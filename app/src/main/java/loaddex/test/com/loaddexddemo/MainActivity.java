package loaddex.test.com.loaddexddemo;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            File outPath = getDir("dex",MODE_PRIVATE);
            DexClassLoader dexClassLoader = new DexClassLoader((String) msg.obj,outPath.getAbsolutePath(),null,getClassLoader());
            try {
                Class<?> clz = dexClassLoader.loadClass("loaddex.test.com.loaddexddemo.LoadUtils");
                Method method = clz.getDeclaredMethod("getLoadString");
                Toast.makeText(MainActivity.this, (CharSequence) method.invoke(clz.newInstance()),Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadDex(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dexPath = new File(Environment.getExternalStorageDirectory(),"load.jar");
                copyFile(dexPath.getAbsolutePath());
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = dexPath.getAbsolutePath();
                mHandler.sendMessage(msg);
            }
        }).start();


    }

    private  void copyFile(String destPath) {
        try {
            InputStream in = getAssets().open("load.jar");
            FileOutputStream fos = new FileOutputStream(new File(destPath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = in.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            in.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
