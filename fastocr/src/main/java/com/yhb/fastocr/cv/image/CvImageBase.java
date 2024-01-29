package com.yhb.fastocr.cv.image;

import android.graphics.Bitmap;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import java.util.List;

/**Opencv位图base类*/
public abstract class CvImageBase {

    /**获取所有位图*/
    public abstract List<Bitmap> getCvAllBitmaps();

    /**mat转换bitmap*/
    public Bitmap mat2Bitmap(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

}