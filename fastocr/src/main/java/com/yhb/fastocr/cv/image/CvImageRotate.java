package com.yhb.fastocr.cv.image;

import android.graphics.Bitmap;
import org.opencv.core.Mat;

/**Opencv加工旋转位图实体*/
public class CvImageRotate extends CvImageBase {

    /**旋转图*/
    private Bitmap rotateBitmap;

    public CvImageRotate(Mat srcMat, Mat rotateMat) {
        super(srcMat);
        this.rotateBitmap = mat2Bitmap(rotateMat);
    }

    public Bitmap getRotateBitmap() {
        return rotateBitmap;
    }

}