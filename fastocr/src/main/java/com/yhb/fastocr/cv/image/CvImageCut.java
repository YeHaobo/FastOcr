package com.yhb.fastocr.cv.image;

import android.graphics.Bitmap;
import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;

/**Opencv加工分割位图实体*/
public class CvImageCut extends CvImageBase {

    /**正向二值化位图*/
    private List<Bitmap> cvBitmaps;
    /**反向二值化位图*/
    private List<Bitmap> cvInvBitmaps;

    /**获取所有位图*/
    @Override
    public List<Bitmap> getCvAllBitmaps() {
        List<Bitmap> bitmapList = new ArrayList<>();
        bitmapList.addAll(cvBitmaps);
        bitmapList.addAll(cvInvBitmaps);
        return bitmapList;
    }

    /**构造*/
    public CvImageCut(List<Mat> mats, List<Mat> invMats) {
        this.cvBitmaps = new ArrayList<>();
        this.cvInvBitmaps = new ArrayList<>();
        for(Mat mat : mats){
            cvBitmaps.add(mat2Bitmap(mat));
        }
        for(Mat mat : invMats){
            cvInvBitmaps.add(mat2Bitmap(mat));
        }
    }

    /**获取正向二值化位图*/
    public List<Bitmap> getCvBitmaps() {
        return cvBitmaps;
    }

    /**获取逆向二值化位图*/
    public List<Bitmap> getCvInvBitmaps() {
        return cvInvBitmaps;
    }

}