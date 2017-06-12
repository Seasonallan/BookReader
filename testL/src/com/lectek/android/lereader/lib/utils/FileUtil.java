package com.lectek.android.lereader.lib.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 文件操作工具
 *
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2011-7-15
 */
public final class FileUtil {

    /**
     * 判断是否有SDCARD
     *
     * @return
     */
    public static boolean isSDcardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }

    }

    /** SDCARD的根目录
     * @return
     */
    public static File getExternalStorageDirectory(){
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 判断文件是否存在
     * @param filePath
     * @return
     */
    public static boolean isFileExists(String filePath){
        return new File(filePath).exists();
    }
    
    /**
	 * 创建文件目录
	 * 
	 * @param path
	 */
	public static boolean createFileDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
			return true;
		}
		file = null;
		return false;
	}

    /**
     * 检查路径有效性。形如“/sdcard/abc/”认为是目录路径，形如“/sdcard/abc”认为是文件路径
     *
     * 如果autoMakePath为true则自动创建目录和文件
     *
     * @param path	路径
     * @param autoMakePath	是否自动创建路径目录
     * @return 文件或目录存或则autoMakePath为true并且创建成功 返回true，否则返回false
     */
    public static boolean checkPath(String path, boolean autoMakePath) {
        if(StringUtil.isEmpty(path)) {
            return false;
        }

        boolean result = new File(path).exists();

        if(!result && autoMakePath) {
            String filePath = null;
            //如果路径格式为“/sdcard/abc/name”，则认为最后一级是文件
            int lastIndex = path.lastIndexOf(File.separatorChar);
            if( lastIndex != path.length() - 1 && lastIndex > 0) {
                filePath = path;
                path = path.substring(0, lastIndex);
            }

            File file = new File(path);
            result = file.mkdirs();

            //如果最后一级是文件则创建
            if(filePath != null) {
                File newFile = new File(filePath);
                try {
                    result = newFile.createNewFile();
                }catch(IOException e) {}
            }
        }

        return result;
    }

    /**
     * 获取存储卡的可用空间
     *
     * @return
     */
    public static long getStorageSize() {
        if (isSDcardExist()) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            long blockSize = stat.getBlockSize();
            return stat.getAvailableBlocks() * blockSize;
        }
        return 0;
    }

    /**
     * 写文件
     *
     * @param str	写入的字符
     * @param filePath 文件路径
     * @param append 追加写入或覆盖写入
     * @return
     */
    public static boolean writeFile(String str, String filePath, boolean append) {

        if (!StringUtil.isEmpty(str) && !StringUtil.isEmpty(filePath)) {
            try {
                FileWriter fw = new FileWriter(filePath, append);
                fw.write(str);
                fw.flush();
                fw.close();
                return true;
            } catch (Exception e) {}
        }

        return false;
    }

    /**
     * 写文件，如果文件超过指定大小则清除内容
     * @param str
     * @param filePath
     * @param append 追加写入或覆盖写入
     * @param limitedSize	文件大小,字节
     * @return
     */
    public static boolean writeFileOfLimitedSize(String str, String filePath, boolean append, int limitedSize) {

        if (StringUtil.isEmpty(str) || StringUtil.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        if (file.exists() && file.length() > limitedSize) {
            file.delete();
        }

        return writeFile(str, filePath, append);
    }

    /**
     * 复制asset文件
     * @param context
     * @param assetFileName
     * @param filePath	文件路径，如:"/sdcard/abc/name.jpg"
     * @return 如果文件不存在并复制成功返回复制的大小，否则返回-1
     */
    public static int cloneAssetFile(Context context, String assetFileName, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return -1;
        }

        BufferedInputStream bis = null;
        RandomAccessFile raf = null;
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(assetFileName);
            bis = new BufferedInputStream(is);

            file.createNewFile();

            raf = new RandomAccessFile(file, "rw");
            raf.seek(0);

            byte[] buf = new byte[4 * 1024];
            int nRead;
            int currentBytes = 0;

            nRead = bis.read(buf, 0, buf.length);
            while(nRead >= 0) {
                raf.write(buf, 0, nRead);
                currentBytes += nRead;
                nRead = bis.read(buf, 0, buf.length);
            }

            return currentBytes;

        } catch (Exception e) {
        }finally {
            if(bis != null) {
                try {
                    bis.close();
                }catch(IOException e){}
            }

            if(raf != null) {
                try {
                    raf.close();
                }catch(IOException e){}
            }
        }

        return -1;
    }

    /**
     * 重命名文件，如果目标文件存在则覆盖
     * @param fromFilePath	源文件
     * @param toFilePath 目标文件
     * @param deleteSrc 重命名后是否删除源文件
     * @return
     */
    public static boolean renameFile(String fromFilePath, String toFilePath, boolean deleteSrc) {
        if (StringUtil.isEmpty(fromFilePath) || StringUtil.isEmpty(toFilePath)) {
            return false;
        }

        File file = new File(fromFilePath);
        if (!file.exists()) {
            return false;
        }

        File newFile = new File(toFilePath);
        if (newFile.exists()) {
            newFile.delete();
        }
        if (!file.renameTo(newFile)) {
            return false;
        }

        if(deleteSrc){
            file.delete();
        }
        return true;
    }

    /**
     * 删除文件
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        return StringUtil.isEmpty(filePath) || new File(filePath).delete();
    }

    /**
     * 删除目录
     * @param dirPath
     * @param force		是否强制删除子目录和文件
     * @return
     */
    public static boolean deleteDir(String dirPath, boolean force) {
        File rootFile = new File(dirPath);
        boolean result = false;

        if(rootFile.exists() ) {

            if(rootFile.isDirectory() && force) {
                File[] subFiles = rootFile.listFiles();
                final int count = subFiles == null ? 0 : subFiles.length;
                for(int i = 0; i < count; i++) {
                    deleteDir(subFiles[i].getAbsolutePath(), force);
                }
            }

            result = rootFile.delete();
        }

        return result;
    }

    /**
     * 根据扩展名获取打开该文件类型的Intent
     * @param filePath
     * @return
     */
    public static Intent openFileAs(String filePath) {

        File file = new File(filePath);
        if (!file.exists())
            return null;

		/* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();
		/* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.equals("ppt")) {
            return getPPTFileIntent(filePath);
        } else if (end.equals("xls")) {
            return getExcelFileIntent(filePath);
        } else if (end.equals("doc")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.equals("txt")) {
            return getTextFileIntent(filePath, false);
        } else {
            return getDefaultIntentOfOpenFile(filePath);
        }
    }

    /**
     * 复制acess文件到SD卡
     */
    public static void copyAssets(Context context, String filename,String fileSavePath) {
        File file = new File(fileSavePath+filename);
        if (file.exists()) {
            return;
        }
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            File outFile = new File(fileSavePath,filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (IOException e) {
            Log.e("tag", "Failed to copy asset file: " + filename, e);
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /** 保存图片 */
    public static boolean saveBitmap(String savePath,Bitmap bm) {
        boolean result = false;
        File file = new File(savePath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bm = compressImage(bm);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.i("dhz", "已经保存");
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private  static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>500) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    /**
     * 获取默认关联文件的打开Intent
     *
     */
    public static Intent getDefaultIntentOfOpenFile(String filePath) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    /**
     * 获取打开.apk文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getApkFileIntent(String filePath) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * 获取打开视频文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getVideoFileIntent(String filePath) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    /**
     * 获取打开音频文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getAudioFileIntent(String filePath) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    /**
     * 获取打开html文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getHtmlFileIntent(String filePath) {

        Uri uri = Uri.parse(filePath).buildUpon().encodedAuthority(
                "com.android.htmlfileprovider").scheme("content").encodedPath(
                filePath).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    /**
     * 获取打开图片文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getImageFileIntent(String filePath) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    /**
     * 获取打开ppt文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getPPTFileIntent(String filePath) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    /**
     * 获取打开Excel文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getExcelFileIntent(String filePath) {
    
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    /**
     * 获取打开Word文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getWordFileIntent(String filePath) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    /**
     * 获取打开Chm文件的Intent
     * @param filePath
     * @return
     */
    public static Intent getChmFileIntent(String filePath) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    /**
     * 获取打开txt文件的Intent
     * @param filePath
     * @param theme		文件路径是否是theme形式
     * @return
     */
    public static Intent getTextFileIntent(String filePath, boolean theme) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (theme) {
            Uri uri1 = Uri.parse(filePath);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(filePath));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    /**
     * 获取打开pdf文件的Intent
     * @return
     */
    public static Intent getPdfFileIntent(String filePath) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }


    /**
     * 是否是epub
     * @param filePath
     * @return
     */
    public static boolean isEpub(String filePath) {
        String path = filePath.toLowerCase();
        if(path != null) {
            return path.endsWith(".epub");
        }
        return false;
    }
    /**
     * 是否是txt
     * @param filePath
     * @return
     */
    public static boolean isTxt(String filePath) {
        String path = filePath.toLowerCase();
        if(path != null) {
            return path.endsWith(".txt");
        }
        return false;
    }
    /**
     * 是否是pdf
     * @param filePath
     * @return
     */
    public static boolean isPdf(String filePath) {
        String path = filePath.toLowerCase();
        if(path != null) {
            return path.endsWith(".pdf");
        }
        return false;
    }
    /**
     * 获取文件所在的目录路径名，如果是文件夹直接返回文件夹的路径名
     * 如果文件不存在返回null
     * @param file
     * @return
     */
    public static String getDirectoryName(File file) {
        if(file.exists()) {
            if(file.isDirectory()) {
                return file.getPath();
            } else {
                String path = file.getPath();
                return path.substring(0, path.lastIndexOf(File.separator) + 1);
            }
        }
        return null;
    }

    /**
     * 获取文件扩展名
     * @param filePath
     * @return
     */
    public static String getExtension(String filePath) {
        if(filePath != null && filePath.length() > 0) {
            int i = filePath.lastIndexOf(".");
            if(i > -1 && i < (filePath.length() - 1)) {
                return filePath.substring(i + 1);
            }
        }
        return "";
    }
}
