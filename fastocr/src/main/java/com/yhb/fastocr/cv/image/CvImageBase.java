package com.yhb.fastocr.cv.image;

import android.graphics.Bitmap;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**Opencv位图base类*/
public class CvImageBase {

    /**原图*/
    private Bitmap srcBitmap;

    public CvImageBase(Mat srcMat) {
        this.srcBitmap = mat2Bitmap(srcMat);
    }

    public Bitmap getSrcBitmap() {
        return srcBitmap;
    }

    /**mat转换bitmap*/
    public Bitmap mat2Bitmap(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

}