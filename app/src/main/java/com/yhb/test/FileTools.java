package com.yhb.test;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

/**文件工具*/
public class FileTools {
    /**拷贝assets文件至指定目录*/
    public static File copyAssetsToDevice(Context context, String dirPath, String fileName){
        try{
            File dirFile = new File(dirPath);
            if(!dirFile.exists()){
                dirFile.mkdirs();
            }
            File dataFile = new File(dirFile, fileName);
            if(dataFile.exists()){
                dataFile.delete();
            }
            AssetManager assetManager = context.getAssets();
            FileOutputStream fileOutputStream = new FileOutputStream(dataFile);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(assetManager.open(fileName));
            int len;
            byte[] buf = new byte[1024];
            while ((len = bufferedInputStream.read(buf)) > 0) {
                fileOutputStream.write(buf, 0, len);
            }
            fileOutputStream.close();
            bufferedInputStream.close();
            return dataFile;
        }catch (Exception e){
            e.printStackTrace();
            Log.e("copyAssetsToDevice", e.getMessage());
        }
        return null;
    }

    /**获取assets图片*/
    public static Bitmap getAssetsToBitmap(Context context, String name){
        try{
            AssetManager assetManager = context.getAssets();
            return BitmapFactory.decodeStream(assetManager.open(name));
        }catch (Exception e){
            e.printStackTrace();
            Log.e("getAssetsBitmap", e.getMessage());
        }
        return null;
    }

}