# FastOcr
Android OCR 快速识别  
1、内部使用opencv处理图片，可获取旋转、灰度、二值化、裁剪等处理后的图片  
2、图片处理完成后使用tess-two（Tesseract）进行文字识别

![](/screenshot.jpg)

### 第三方开源
1、tess-two:9.1.0  
2、opencv:4.9.0

### 依赖
（1）在Project的build.gradle文件中添加
```java
  allprojects {
    repositories {
      ... ...
      maven { url 'https://jitpack.io' }
      ... ...
    }
  }
```
（2）在app的build.gradle文件中添加
```java
  dependencies {
    ... ...
    implementation 'com.github.YeHaobo.FastOcr:fastocr:1.5'//fastocr核心
    implementation 'com.github.YeHaobo.FastOcr:opencv:1.5'//opencv
    ... ...
  }
```

### 权限
（1）Android6.0+注意权限的动态获取
```java
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>6.0+必须
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>6.0+必须
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>11.0+版本按需添加授权
```

### 文字包  
**本地需要准备文字包供tess-two使用**  
[中文包](/app/src/main/assets/chi_sim.traineddata)  
[英文包](/app/src/main/assets/eng.traineddata)  
[tess-two文字包总汇](https://github.com/tesseract-ocr/tessdata/tree/3.04.00)  
[Tesseract4Android文字包总汇](https://github.com/tesseract-ocr/tessdata/tree/4.0.0)  
_项目中是将文字包从assets中拷贝至本地使用，但建议把包放在云端，通过远程下载的方式初始化，可有效减少安装包体积_  

### 初始化
```java
    HandlerThread handlerThread = new HandlerThread("YHB");//初始化耗时，需切换线程
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
    FastOcr.createAndInit(handler, true, dataFile, new FastOcrInitResult() {//dataFile:文字包。回调在handler线程
      @Override
      public void faseOcrInit(FastOcr fastOcr, String msg) {
        this.fastOcr = fastOcr;
        Log.e("FastOcrInit", (fastOcr != null ? "FastOcr初始化完成：" : "FastOcr初始化失败：") + msg);
      }
    });
```

### 识别
```java
        if(fastOcr == null) return;
        fastOcr.getText(bitmap, 0, 150, new FastOcrTextResult() {//0:旋转角度 150：二值化阈值
            @Override
            public void fastOcrText(boolean result, String language, String text) {
              Log.e("FastOcrText", (result ? ("使用语言：" + language + "\n识别文字：" + text) : ("识别失败：" + text)));
            }
        });
```

### 获取opencv处理图片
```java
    //CvImageFactory获取处理图片建议使用子线程
		
    CvImageRotate cvImageRotate = CvImageFactory.decodeRotate(bitmap, 90);//90：旋转角度
    Bitmap srcBitmap = cvImageRotate.getSrcBitmap();//原图
    Bitmap rotateBitmap = cvImageRotate.getRotateBitmap();//旋转图

    CvImageGray cvImageGray = CvImageFactory.decodeGray(bitmap, 90);
    Bitmap grayBitmap = cvImageGray.getGrayBitmap();//灰度图

    CvImageThreshold cvImageThreshold = CvImageFactory.decodeThreshold(bitmap, 90, 150);//150：二值化阈值
    Bitmap thresholdBitmap = cvImageThreshold.getThresholdBitmap();//正向二值化图
    Bitmap thresholdInvBitmap = cvImageThreshold.getThresholdInvBitmap();//逆向二值化图

    CvImageCut cvImageCut = CvImageFactory.decodeCut(bitmap, 90, 150);
    List<Bitmap> cutBitmaps = cvImageCut.getCutBitmaps();//正向二值化文字裁剪图
    List<Bitmap> cutInvBitmaps = cvImageCut.getCutInvBitmaps();//逆向二值化文字裁剪图
		
```

### 释放
```java
    @Override
    protected void onDestroy() {
        if(fastOcr != null) fastOcr.release();
        super.onDestroy();
    }
```
