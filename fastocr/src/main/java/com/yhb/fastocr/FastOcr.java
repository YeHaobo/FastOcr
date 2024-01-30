package com.yhb.fastocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.yhb.fastocr.cv.CvImageFactory;
import org.opencv.android.OpenCVLoader;
import java.io.File;

/**
 * OCR识别
 * OCR依赖库Git地址 https://github.com/rmtheis/tess-two
 * OCR文字训练文件包下载地址 https://github.com/tesseract-ocr/tessdata/tree/3.04.00 （例：英文eng.traineddata、中文chi_sim.traineddata）
 * 本项目为兼容性这里使用support版本 ，androidX版本如下
 * OCR依赖库Git地址 https://github.com/adaptech-cz/Tesseract4Android
 * OCR文字训练文件包下载地址 https://github.com/tesseract-ocr/tessdata/tree/4.0.0 （例：英文eng.traineddata、中文chi_sim.traineddata）
 */
public class FastOcr {

    /**任务执行Handler*/
    private Handler handler;
    /**OCR识别引擎*/
    private TessBaseAPI ocrApi;

    /**初始化*/
    public static void createAndInit(Handler handler, boolean debug, File dataFile, FastOcrInitResult result){
        new FastOcr(handler, debug).init(dataFile, result);
    }

    /**私有构造*/
    private FastOcr(Handler handler, boolean debug) {
        this.handler = handler;
        this.ocrApi = new TessBaseAPI();
        this.ocrApi.setDebug(debug);
    }

    /**初始化*/
    public void init(final File dataFile, final FastOcrInitResult result){
        handler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    if(dataFile == null || !dataFile.exists()){//文件判空
                        if(result != null) result.faseOcrInit(null, "dataFile is null or not exists");
                        return;
                    }
                    if(!"traineddata".equals(dataFile.getName().substring(dataFile.getName().lastIndexOf(".") + 1))){//后缀名验证
                        if(result != null) result.faseOcrInit(null, "dataFile postfix is not '.traineddata'");
                        return;
                    }
                    if(!"tessdata".equals(dataFile.getParentFile().getName())){//文件夹验证
                        if(result != null) result.faseOcrInit(null, "dataFile parent folder name is not 'tessdata'");
                        return;
                    }
                    String path = dataFile.getParentFile().getParentFile().getAbsolutePath();
                    String language = dataFile.getName().substring(0, dataFile.getName().lastIndexOf("."));
                    boolean ocrInit = ocrApi.init(path, language);//ocr初始化
                    if(!ocrInit){
                        if(result != null) result.faseOcrInit(null, "ocr module init failed, permissions may be not open");
                        return;
                    }
                    boolean cvInit = OpenCVLoader.initLocal();//opencv初始化
                    if(!cvInit){
                        if(result != null) result.faseOcrInit(null, "opencv module init failed");
                        return;
                    }
                    if(result != null) result.faseOcrInit(FastOcr.this, "opencv module and ocr module init success");
                } catch (Exception e){
                    if(result != null) result.faseOcrInit(null, e.getMessage());
                }
            }
        });
    }

    /**识别*/
    public void getText(final String path, final int rotate, final FastOcrTextResult result){
        handler.post(new Runnable() {
            @Override
            public void run() {
                getText(BitmapFactory.decodeFile(path), rotate, result);
            }
        });
    }

    /**识别*/
    public void getText(final Bitmap bitmapSrc, final int rotate, final FastOcrTextResult result){
        handler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    Bitmap bitmap =  CvImageFactory.decodeFull(bitmapSrc, rotate, 150).getCvInvBitmap();
                    ocrApi.setImage(bitmap);//设置图片
                    String text = ocrApi.getUTF8Text();//识别
                    text = text == null ? "" : text.replace(" ","").replace("\n", "");
                    ocrApi.stop();//停止
                    ocrApi.clear();//清除
                    if(result != null) result.fastOcrText(true, ocrApi.getInitLanguagesAsString(), text);
                }catch (Exception e){
                    if(result != null) result.fastOcrText(false, ocrApi.getInitLanguagesAsString(), e.getMessage());
                }
            }
        });
    }

    /**释放*/
    public void release(){
        ocrApi.end();
        handler.removeCallbacksAndMessages(null);
    }

}