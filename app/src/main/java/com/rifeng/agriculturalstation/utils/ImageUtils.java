package com.rifeng.agriculturalstation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chw on 2017/1/14.
 */
public class ImageUtils {

    public static Bitmap getBitmap(String imgPath){
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // 不压缩
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }

    /**
     * 按质量压缩
     *
     * @param bitmap
     * @param maxByteSize 允许最大值字节数
     * @return 质量压缩压缩过的图片
     */
    public static Bitmap compressByQuality(Bitmap bitmap, long maxByteSize){
        return compressByQuality(bitmap, maxByteSize, false);
    }

    /**
     * 按质量压缩
     *
     * @param bitmap
     * @param maxByteSize   允许最大值字节数
     * @param recycle   是否回收
     * @return  质量压缩压缩过的图片
     */
    public static Bitmap compressByQuality(Bitmap bitmap, long maxByteSize, boolean recycle){
        if(isEmptyBitmap(bitmap) || maxByteSize <= 0){
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxByteSize && quality > 0){ // 循环判断如果压缩后图片大于指定的大小则继续压缩
            baos.reset(); // 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality -= 5, baos);
        }
        if(quality < 0){
            return null;
        }
        byte[] bytes = baos.toByteArray();
        if(recycle && !bitmap.isRecycled()){
            bitmap.recycle();
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    public static Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if( os.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    public static File bitmap2File(String imgPath, float pixelW, float pixelH, Context context) throws IOException {
        Bitmap bitmap = ratio(getBitmap(imgPath), pixelW, pixelH);
        String path = context.getApplicationContext().getFilesDir() + "/image/"; // 上传图片缓存目录
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File imgFile = new File(path + new File(imgPath).getName());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        if(bitmap.isRecycled()){
            bitmap.recycle();
        }
        return imgFile;
    }

    /**
     * bitmap转file
     *
     * @param imgPath
     * @param context
     * @return
     * @throws IOException
     */
    public static File bitmap2File(String imgPath, long maxByteSize, Context context) throws IOException {
        Bitmap bitmap = compressByQuality(getBitmap(imgPath), maxByteSize, true);
        String path = context.getApplicationContext().getFilesDir() + "/image/"; // 上传图片缓存目录
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File imgFile = new File(path + new File(imgPath).getName());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        if(bitmap.isRecycled()){
            bitmap.recycle();
        }
        return imgFile;
    }

    /**
     * bitmap转file
     *
     * @param bitmap
     * @param context
     * @return
     * @throws IOException
     */
    public static File bitmap2File(Bitmap bitmap, Context context) throws IOException {
        String path = context.getApplicationContext().getFilesDir() + "/image/"; // 上传图片缓存目录
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File imgFile = new File(path + System.currentTimeMillis() + ".jpeg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        if(bitmap.isRecycled()){
            bitmap.recycle();
        }
        return imgFile;
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertBitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 图片转成string
     *
     * @param imgPath
     * @param maxByteSize
     * @param recycle
     * @return
     */
    public static String bitmap2String(String imgPath, long maxByteSize, boolean recycle){
        return convertBitmapToString(compressByQuality(getBitmap(imgPath), maxByteSize, recycle));
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param bitmap
     * @return true 空  false 非空
     */
    private static boolean isEmptyBitmap(Bitmap bitmap){
        return bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0;
    }
}




















































