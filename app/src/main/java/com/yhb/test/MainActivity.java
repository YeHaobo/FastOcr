package com.yhb.test;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.yhb.fastocr.FastOcr;
import com.yhb.fastocr.FastOcrInitResult;
import com.yhb.fastocr.FastOcrTextResult;
import com.yhb.fastocr.cv.CvImageFactory;
import com.yhb.fastocr.cv.image.CvImageThreshold;
import java.io.File;

/**测试界面*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener, FastOcrInitResult, FastOcrTextResult {

    private ImageView img1, img2, img3, img4;
    private TextView tv;
    private Handler handler;
    private FastOcr fastOcr;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        tv = findViewById(R.id.tv);
        findViewById(R.id.btn).setOnClickListener(this);

        HandlerThread handlerThread = new HandlerThread("YHB");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        //从assets中拷贝语言包至本地，语言包文件必须在tessdata文件夹内
        copyLanguageFile("chi_sim.traineddata", getCacheDir().getAbsolutePath() + "/tessdata");

    }

    /**语言包文件拷贝*/
    private void copyLanguageFile(String dataFileName, String dataDirPath){
        Toast.makeText(this, "语言包拷贝中", Toast.LENGTH_SHORT).show();
        handler.post(new Runnable() {
            @Override
            public void run() {
                File dataFile = FileTools.copyAssetsToDevice(MainActivity.this, dataDirPath, dataFileName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, dataFile != null ? "语言包拷贝完成" : "语言包拷贝失败", Toast.LENGTH_SHORT).show();
                    }
                });
                FastOcr.createAndInit(handler, true, dataFile, MainActivity.this);//初始化FastOcr
            }
        });
    }

    /**FastOcr初始化回调*/
    @Override
    public void faseOcrInit(FastOcr fastOcr, final String msg) {//回调在handler线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, (fastOcr != null ? "FastOcr初始化完成：" : "FastOcr初始化失败：") + msg, Toast.LENGTH_SHORT).show();
            }
        });
        this.fastOcr = fastOcr;
        bitmap = FileTools.getAssetsToBitmap(MainActivity.this, "screen.png");
    }

    /**点击识别*/
    @Override
    public void onClick(View v) {
        if(fastOcr == null) return;
        fastOcr.getText(bitmap, 0, this);
        Toast.makeText(this, "文字识别中...", Toast.LENGTH_SHORT).show();
    }

    /**文字识别回调*/
    @Override
    public void fastOcrText(boolean result, String language, final String text) {
        if(bitmap == null) return;
        CvImageThreshold cvImageThreshold = CvImageFactory.decodeThreshold(bitmap, 0, 150);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(result){
                    img1.setImageBitmap(cvImageThreshold.getSrcBitmap());
                    img2.setImageBitmap(cvImageThreshold.getGrayBitmap());
                    img3.setImageBitmap(cvImageThreshold.getThresholdBitmap());
                    img4.setImageBitmap(cvImageThreshold.getThresholdInvBitmap());
                    tv.setText("使用语言：" + language + "\n识别文字：" + text);
                }else{
                    tv.setText("识别失败：" + text);
                }
            }
        });
    }

    /**释放*/
    @Override
    protected void onDestroy() {
        if(fastOcr != null) fastOcr.release();
        super.onDestroy();
    }

}