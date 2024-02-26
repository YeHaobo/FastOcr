package com.yhb.fastocr.cv.image;

import android.graphics.Bitmap;
import org.opencv.core.Mat;

/**Opencv加工灰度位图实体*/
public class CvImageGray extends CvImageRotate{

    /**灰度图*/
    private Bitmap grayBitmap;

    public CvImageGray(Mat srcMat, Mat rotateMat, Mat grayMat) {
        super(srcMat, rotateMat);
        this.grayBitmap = mat2Bitmap(grayMat);
    }

    public Bitmap getGrayBitmap() {
        return grayBitmap;
    }

}