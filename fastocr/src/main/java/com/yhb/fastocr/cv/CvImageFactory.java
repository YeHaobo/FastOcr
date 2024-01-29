package com.yhb.fastocr.cv;

import android.graphics.Bitmap;
import com.yhb.fastocr.cv.image.CvImageCut;
import com.yhb.fastocr.cv.image.CvImageFull;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenCv图片加工工厂
 * 内部可能比较耗时，建议放在子线程执行
 * 所有操作前需初始化Opencv
 */
public class CvImageFactory {

    /**加工*/
    public static CvImageFull decodeFull(String path, int rotate, int threshold){
        Mat mat = createFull(Imgcodecs.imread(path), rotate, threshold, true);
        Mat invMat = createFull(Imgcodecs.imread(path), rotate, threshold, false);
        return new CvImageFull(mat, invMat);
    }

    /**加工*/
    public static CvImageFull decodeFull(Bitmap bitmapIn, int rotate, int threshold){
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmapIn, srcMat);
        Mat mat = createFull(srcMat, rotate, threshold, true);
        Mat invMat = createFull(srcMat, rotate, threshold, false);
        return new CvImageFull(mat, invMat);
    }

    /**加工并裁剪*/
    public static CvImageCut decodeCuts(String path, int rotate, int threshold){
        List<Mat> matList = createCuts(Imgcodecs.imread(path), rotate, threshold, true);
        List<Mat> invMatList = createCuts(Imgcodecs.imread(path), rotate, threshold, false);
        return new CvImageCut(matList, invMatList);
    }

    /**加工并裁剪*/
    public static CvImageCut decodeCuts(Bitmap bitmapIn, int rotate, int threshold){
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmapIn, srcMat);
        List<Mat> matList = createCuts(srcMat, rotate, threshold, true);
        List<Mat> invMatList = createCuts(srcMat, rotate, threshold, false);
        return new CvImageCut(matList, invMatList);
    }

    /**加工原图*/
    private static Mat createFull(Mat srcMat, int rotate, int threshold, boolean inversion){
        //旋转
        Mat rotateMat = new Mat();
        switch (rotate % 360){
            case 90: Core.rotate(srcMat, rotateMat, Core.ROTATE_90_CLOCKWISE); break;//顺时针旋转90
            case 180: Core.rotate(srcMat, rotateMat, Core.ROTATE_180); break;//旋转180
            case 270: Core.rotate(srcMat, rotateMat, Core.ROTATE_90_COUNTERCLOCKWISE); break;//逆时针旋转90
            default: rotateMat = srcMat;
        }

        //灰度图
        Mat grayMat = new Mat();
        Imgproc.cvtColor(rotateMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        //二值化
        Mat binaryMat = new Mat();
        Imgproc.threshold(grayMat, binaryMat, threshold, 255, inversion ? Imgproc.THRESH_BINARY_INV : Imgproc.THRESH_BINARY);//二值化（正向/反向）

        return binaryMat;
    }

    /**加工并裁切原图*/
    private static List<Mat> createCuts(Mat srcMat, int rotate, int threshold, boolean inversion){
        //二值
        Mat binaryMat = createFull(srcMat, rotate, threshold, inversion);

        //腐蚀
        Mat erodeMat = new Mat();
        Mat elementMat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 10));//腐蚀像素点例
        Imgproc.erode(binaryMat, erodeMat, elementMat);//腐蚀

        //提取轮廓
        List<MatOfPoint> pointList = new ArrayList<>();
        Imgproc.findContours(erodeMat, pointList, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);//提取轮廓

        //裁剪轮廓
        List<Mat> subMatList = new ArrayList<>();
        for(MatOfPoint point : pointList){
            Rect rect = Imgproc.boundingRect(point);//转rect
//            Imgproc.rectangle(erodeMat, rect, new Scalar(0,0,255));//画轮廓
            subMatList.add(new Mat(binaryMat, rect));
        }

        return subMatList;
    }

}