package com.yhb.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yhb.fastocr.FastOcr;
import com.yhb.fastocr.FastOcrInitResult;
import com.yhb.fastocr.FastOcrTextResult;
import com.yhb.fastocr.cv.CvImageFactory;
import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FastOcrInitResult, FastOcrTextResult {

    private String path = Environment.getExternalStorageDirectory().getPath() + "/intemol3/a.png";
    //    private String path = Environment.getExternalStorageDirectory().getPath() + "/intemol3/b.jpg";
    private File file = new File(Environment.getExternalStorageDirectory().getPath() + "/intemol3/tessdata/chi_sim.traineddata");
    private TextView tv;
    private ImageView img;
    private Handler handler;
    private FastOcr fastOcr;
    private List<Bitmap> list;
    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);
        img = findViewById(R.id.img);
        img.setOnClickListener(this);

        HandlerThread handlerThread = new HandlerThread("YHB");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        img.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    @Override
    public void onClick(View v) {
        Log.e("text", "start");
        if(fastOcr == null){
            FastOcr.createAndInit(handler, true, file, this);
        }else{
            if(list.size() > 0){
                num = num +1 < list.size() ? num + 1 : 0;
                img.setImageBitmap(list.get(num));
            }
        }
    }

    @Override
    public void faseOcrInit(FastOcr fastOcr, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(msg);
            }
        });
        Log.e("text", msg+"");
        if(fastOcr != null){
            this.fastOcr = fastOcr;
            fastOcr.getText(path, 90, this);
        }
        list = CvImageFactory.decodeCuts(path, 90, 150).getCvAllBitmaps();
        if(list.size() > 0){
            Log.e("text", list.size()+"");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    num = 0;
                    img.setImageBitmap(list.get(num));
                }
            });
        }
    }

    @Override
    public void fastOcrText(boolean result, String language, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(text);
                Log.e("text", text+"");
            }
        });
    }

}