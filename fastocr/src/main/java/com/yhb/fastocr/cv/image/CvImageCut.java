package com.yhb.fastocr.cv.image;

import android.graphics.Bitmap;
import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;

/**Opencv加工分割位图实体*/
public class CvImageCut extends CvImageThreshold {

    /**正向二值化位图*/
    private List<Bitmap> cutBitmaps;
    /**反向二值化位图*/
    private List<Bitmap> cutInvBitmaps;

    public CvImageCut(Mat srcMat, Mat rotateMat, Mat grayMat, Mat thresholdMat, Mat thresholdInvMat, List<Mat> cutMats, List<Mat> cutInvMats) {
        super(srcMat, rotateMat, grayMat, thresholdMat, thresholdInvMat);
        this.cutBitmaps = new ArrayList<>();
        this.cutInvBitmaps = new ArrayList<>();
        for(Mat mat : cutMats){
            cutBitmaps.add(mat2Bitmap(mat));
        }
        for(Mat mat : cutInvMats){
            cutInvBitmaps.add(mat2Bitmap(mat));
        }
    }

    public List<Bitmap> getCutBitmaps() {
        return cutBitmaps;
    }

    public List<Bitmap> getCutInvBitmaps() {
        return cutInvBitmaps;
    }

}