package com.yhb.fastocr.cv.image;

import android.graphics.Bitmap;
import org.opencv.core.Mat;

/**Opencv加工二值化位图实体*/
public class CvImageThreshold extends CvImageGray {

    /**正向二值化位图*/
    private Bitmap thresholdBitmap;
    /**反向二值化位图*/
    private Bitmap thresholdInvBitmap;

    public CvImageThreshold(Mat srcMat, Mat rotateMat, Mat grayMat, Mat thresholdMat, Mat thresholdInvMat) {
        super(srcMat, rotateMat, grayMat);
        this.thresholdBitmap = mat2Bitmap(thresholdMat);
        this.thresholdInvBitmap = mat2Bitmap(thresholdInvMat);
    }

    public Bitmap getThresholdBitmap() {
        return thresholdBitmap;
    }

    public Bitmap getThresholdInvBitmap() {
        return thresholdInvBitmap;
    }

}