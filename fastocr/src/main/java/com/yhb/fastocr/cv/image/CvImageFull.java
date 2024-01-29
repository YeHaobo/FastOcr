package com.yhb.fastocr.cv.image;

import android.graphics.Bitmap;
import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;

/**Opencv加工位图实体*/
public class CvImageFull extends CvImageBase {

    /**正向二值化位图*/
    private Bitmap cvBitmap;
    /**反向二值化位图*/
    private Bitmap cvInvBitmap;

    /**获取所有位图*/
    @Override
    public List<Bitmap> getCvAllBitmaps() {
        List<Bitmap> bitmapList = new ArrayList<>();
        bitmapList.add(cvBitmap);
        bitmapList.add(cvInvBitmap);
        return bitmapList;
    }

    /**构造*/
    public CvImageFull(Mat mat, Mat invMat) {
        this.cvBitmap = mat2Bitmap(mat);
        this.cvInvBitmap = mat2Bitmap(invMat);
    }

    /**获取正向二值化位图*/
    public Bitmap getCvBitmap() {
        return cvBitmap;
    }

    /**获取逆向二值化位图*/
    public Bitmap getCvInvBitmap() {
        return cvInvBitmap;
    }

}