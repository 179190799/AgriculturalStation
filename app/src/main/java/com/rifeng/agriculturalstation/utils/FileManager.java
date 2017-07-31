package com.rifeng.agriculturalstation.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * <!--读写SD卡的权限-->
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 *
 * Created by chw on 2016/10/17.
 */
public class FileManager {

    private String SD_PATH;
    private String fileName;
    private String dirName;

    public String getSD_PATH() {
        return SD_PATH;
    }

    /**
     * @param permission 是否添加了权限
     */
    public FileManager(boolean permission){
        // 得到当前外部存储设备的目录
        SD_PATH = Environment.getExternalStorageDirectory() + "/";
    }

    public File createSDFile(String dirName, String fileName){
        this.fileName = fileName;
        this.dirName = dirName;
        File file = new File(createSDDir(dirName), fileName);
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    private File createSDDir(String dirName) {
        File dir = new File(SD_PATH + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断SD卡上是否存在该文件
     *
     * @param dirName 文件夹
     * @param fileName 文件名
     * @return
     */
    public boolean isFileExist(String dirName, String fileName){
        File file = new File(SD_PATH + dirName + "/" + fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param dirName 文件夹
     * @param fileName 文件名
     * @param input
     * @return
     */
    public File saveToSDcard(String dirName, String fileName, InputStream input){

        File file = null;
        OutputStream output = null;
        try {
            // 创建目录
            createSDDir(dirName);
            // 创建文件
            file = createSDFile(dirName, fileName);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
//            int len = 0;
            while ((input.read(buffer)) != -1){
                output.write(buffer);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (input != null){
                    input.close();
                }
                if(output != null){
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * @param dirName
     * @param fileName
     * @return 从sd卡中读取文件的字符，返回String
     * @throws FileNotFoundException
     */
    public String readFromSDcard(String dirName, String fileName) throws FileNotFoundException {
        try {
            File file = new File(SD_PATH + dirName + "/" + fileName);
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder("");
            String line = null;
            // 循环读取文件内容
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            fis.close();
            br.close();
            return sb.toString().trim();
        } catch (FileNotFoundException e){
            e.printStackTrace();
            throw new FileNotFoundException("no such file");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /////////////////////////////////直接读写包名文件夹下的文件，而不是SD卡中的//////////////////////////////////////////
    /**
     * 将string写入到data/data/包名/files中
     *
     * @param context
     * @param filename
     * @param str
     */
    public static void write(Context context, String filename, String str){
        try {
            /**
             * MODE_PRIVATE：该文件只能被当前程序读写
             * MODE_APPEND：用追加方式打开，新写入的内容追加到末尾
             * MODE_WORLD_READABLE：该文件内容可以被其他程序读
             * MODE_WORLD_WRITEABLE：该文件内容可以被其他程序写
             */
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            PrintStream ps = new PrintStream(fos);
            // 输出文件的内容
            ps.println(str);
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read(Context context, String fileName){
        try {
            // 打开文件输入流
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            int len = 0;
            StringBuilder sb = new StringBuilder("");
            // 读取文件内容
            while ((len = fis.read(buffer)) > 0){
                sb.append(new String(buffer, 0, len));
            }
            // 关闭文件输入流
            fis.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
















































