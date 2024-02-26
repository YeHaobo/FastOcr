package com.yhb.fastocr.cv;

import android.graphics.Bitmap;
import com.yhb.fastocr.cv.image.CvImageCut;
import com.yhb.fastocr.cv.image.CvImageGray;
import com.yhb.fastocr.cv.image.CvImageRotate;
import com.yhb.fastocr.cv.image.CvImageThreshold;
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

    /**旋转*/
    public static CvImageRotate decodeRotate(String path, int rotate){
        Mat srcMat = Imgcodecs.imread(path);
        Mat rotateMat = rotate(srcMat, rotate);
        return new CvImageRotate(srcMat, rotateMat);
    }
    public static CvImageRotate decodeRotate(Bitmap bitmap, int rotate){
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmap, srcMat);
        Mat rotateMat = rotate(srcMat, rotate);
        return new CvImageRotate(srcMat, rotateMat);
    }
    private static Mat rotate(Mat srcMat, int rotate){
        Mat rotateMat = new Mat();
        switch (rotate % 360){
            case 90: Core.rotate(srcMat, rotateMat, Core.ROTATE_90_CLOCKWISE); break;//顺时针旋转90
            case 180: Core.rotate(srcMat, rotateMat, Core.ROTATE_180); break;//旋转180
            case 270: Core.rotate(srcMat, rotateMat, Core.ROTATE_90_COUNTERCLOCKWISE); break;//逆时针旋转90
            default: rotateMat = srcMat;break;//不变
        }
        return rotateMat;
    }

    /**灰度*/
    public static CvImageGray decodeGray(String path, int rotate){
        Mat srcMat = Imgcodecs.imread(path);
        Mat rotateMat = rotate(srcMat, rotate);
        Mat grayMat = gray(rotateMat);
        return new CvImageGray(srcMat, rotateMat, grayMat);
    }
    public static CvImageGray decodeGray(Bitmap bitmap, int rotate){
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmap, srcMat);
        Mat rotateMat = rotate(srcMat, rotate);
        Mat grayMat = gray(rotateMat);
        return new CvImageGray(srcMat, rotateMat, grayMat);
    }
    private static Mat gray(Mat srcMat){
        Mat grayMat = new Mat();
        Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        return grayMat;
    }

    /**二值化（正向/反向）*/
    public static CvImageThreshold decodeThreshold(String path, int rotate, int threshold){
        Mat srcMat = Imgcodecs.imread(path);
        Mat rotateMat = rotate(srcMat, rotate);
        Mat grayMat = gray(rotateMat);
        Mat thresholdMat = threshold(grayMat, threshold, true);
        Mat thresholdInvMat = threshold(grayMat, threshold, false);
        return new CvImageThreshold(srcMat, rotateMat, grayMat, thresholdMat, thresholdInvMat);
    }
    public static CvImageThreshold decodeThreshold(Bitmap bitmap, int rotate, int threshold){
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmap, srcMat);
        Mat rotateMat = rotate(srcMat, rotate);
        Mat grayMat = gray(rotateMat);
        Mat thresholdMat = threshold(grayMat, threshold, true);
        Mat thresholdInvMat = threshold(grayMat, threshold, false);
        return new CvImageThreshold(srcMat, rotateMat, grayMat, thresholdMat, thresholdInvMat);
    }
    private static Mat threshold(Mat srcMat, int threshold, boolean inversion){
        Mat thresholdMat = new Mat();
        Imgproc.threshold(srcMat, thresholdMat, threshold, 255, inversion ? Imgproc.THRESH_BINARY_INV : Imgproc.THRESH_BINARY);//正向/反向
        return thresholdMat;
    }

    /**裁切*/
    public static CvImageCut decodeCut(String path, int rotate, int threshold){
        Mat srcMat = Imgcodecs.imread(path);
        Mat rotateMat = rotate(srcMat, rotate);
        Mat grayMat = gray(rotateMat);
        Mat thresholdMat = threshold(grayMat, threshold, true);
        Mat thresholdInvMat = threshold(grayMat, threshold, false);
        List<Mat> cutMats = cut(thresholdMat);
        List<Mat> cutInvMats = cut(thresholdInvMat);
        return new CvImageCut(srcMat, rotateMat, grayMat, thresholdMat, thresholdInvMat, cutMats, cutInvMats);
    }
    public static CvImageCut decodeCut(Bitmap bitmap, int rotate, int threshold){
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmap, srcMat);
        Mat rotateMat = rotate(srcMat, rotate);
        Mat grayMat = gray(rotateMat);
        Mat thresholdMat = threshold(grayMat, threshold, true);
        Mat thresholdInvMat = threshold(grayMat, threshold, false);
        List<Mat> cutMats = cut(thresholdMat);
        List<Mat> cutInvMats = cut(thresholdInvMat);
        return new CvImageCut(srcMat, rotateMat, grayMat, thresholdMat, thresholdInvMat, cutMats, cutInvMats);
    }
    private static List<Mat> cut(Mat srcMat){
        //腐蚀
        Mat erodeMat = new Mat();
        Mat elementMat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 10));//腐蚀像素点例
        Imgproc.erode(srcMat, erodeMat, elementMat);//腐蚀

        //提取轮廓
        List<MatOfPoint> pointList = new ArrayList<>();
        Imgproc.findContours(erodeMat, pointList, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);//提取轮廓

        //裁剪轮廓
        List<Mat> subMatList = new ArrayList<>();
        for(MatOfPoint point : pointList){
            Rect rect = Imgproc.boundingRect(point);//转rect
//            Imgproc.rectangle(erodeMat, rect, new Scalar(0,0,255));//画轮廓
            subMatList.add(new Mat(srcMat, rect));
        }

        return subMatList;
    }

}