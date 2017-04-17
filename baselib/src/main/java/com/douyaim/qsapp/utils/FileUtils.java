package com.douyaim.qsapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import com.douyaim.qsapp.permissionhelp.PermissionHelp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by qiuyi on 15/4/15.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    private static int BUFFER_DEFAULT_SIZE = 1024;
    public final static int REQUEST_PERMISSION_CODE = 288;
    private static String PUB_ROOT_DIR = "";      //sd卡上的公共文件根路径
    private static String PRIVATE_ROOT_DIR = "";  //app内部文件根路径

    public static void copyDir(String sourcePath, String newPath) throws IOException {
        File file = new File(sourcePath);
        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }

        for (int i = 0; i < filePath.length; i++) {
            if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {
                copyDir(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
            }

            if (new File(sourcePath + file.separator + filePath[i]).isFile()) {
                copyFiles(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);
            }

        }
    }

    public static void copyFiles(String oldPath, String newPath) throws IOException {
        File oldFile = new File(oldPath);
        File file = new File(newPath);
        FileInputStream in = new FileInputStream(oldFile);
        FileOutputStream out = new FileOutputStream(file);
        ;

        byte[] buffer = new byte[2097152];

        while ((in.read(buffer)) != -1) {
            out.write(buffer);
        }
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {

        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public static List<File> getFileSort(String path) {

        List<File> list = getFiles(path, new ArrayList<File>());

        if (list != null && list.size() > 0) {

            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }

                }
            });
        }

        return list;
    }


    /**
     * 检查是否有读写存储权限
     *
     * @return
     */
    public static boolean checkStoragePermission() {
        return PermissionHelp.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 申请sd卡读写权限
     *
     * @param activity
     */
    public static void requestStoragePer(Activity activity) {
        PermissionHelp.with(activity)
                .setRequestCode(REQUEST_PERMISSION_CODE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
    }

    public static void requestStoragePer(Fragment frag) {
        PermissionHelp.with(frag)
                .setRequestCode(REQUEST_PERMISSION_CODE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
    }

    public static void initFilePath(Context context) {
        PUB_ROOT_DIR = context.getExternalFilesDir(null).getAbsolutePath();
        PRIVATE_ROOT_DIR = context.getFilesDir().getAbsolutePath();
        File pubRootDir = new File(PUB_ROOT_DIR);
        if (!pubRootDir.exists()) {
            pubRootDir.mkdir();
        }

        File privateRootDir = new File(PRIVATE_ROOT_DIR);
        if (!privateRootDir.exists()) {
            privateRootDir.mkdir();
        }
    }

    /**
     * 重命名文件
     *
     * @param inFile      源文件的绝对路径
     * @param newFileName 新文件的绝对路径
     * @return true 表示成功
     */
    public static boolean reNameFile(String inFile, String newFileName) {
        boolean ret = false;
        try {
            File inF = new File(inFile);
            File outF = new File(newFileName);
            if (inF.exists()) {

                if (outF.exists()) {
                    outF.delete();
                }
                ret = inF.renameTo(outF);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static String moveFile(String infile, String outfile) {
        L.i(TAG, "moveFile,in=" + infile, "out=" + outfile);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(infile);
            out = new FileOutputStream(outfile);

            byte[] buffer = new byte[BUFFER_DEFAULT_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            File file = new File(outfile);
            L.i(TAG, "move file to " + file.getAbsolutePath() + ",file exists ?" + file.exists());
            return outfile;
        } catch (FileNotFoundException e) {
            deleteFile(outfile);
            L.e(TAG, e);
        } catch (Exception e) {
            deleteFile(outfile);
            L.e(TAG, e);
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param filePath 本地文件路径
     */
    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        return deleteFile(new File(filePath));
    }

    /**
     * @param file
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            try {
                return file.delete();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static long getFolderUsedSize(File folder) {
        if (folder == null || !folder.exists()) {
            L.e(TAG, "FileUtils.getFolderUsedSize,folder is not exist");
            return 0;
        }
        try {
            if (folder.isDirectory()) {
                long size = 0;
                File[] flists = folder.listFiles();
                if (flists == null || flists.length == 0) {
                    return 0;
                }
                for (File file : flists) {
                    size += getFolderUsedSize(file);
                }
                return size;
            } else {
                return folder.length();
            }
        } catch (Exception e) {
            L.e(TAG, "FileUtils.getFolderUsedSize, e=" + e.getMessage());
        }
        return 0;
    }

    /**
     * 检查是否存在
     *
     * @param directory 目录 warn ,directory 结尾也允许是/
     * @param fileName  文件名
     * @return
     */
    public static boolean exists(String directory, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        File file = new File(directory, fileName);
        try {
            return file.exists();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 判断指定路径的文件是否存在
     * 最好不要使用拼接的路径,此时应当使用如下方法:
     */
    @Deprecated
    public static boolean exists(String filePath) {
        try {
            return new File(filePath).exists();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[BUFFER_DEFAULT_SIZE];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                return true;
            }
        } catch (Exception e) {
            L.e(TAG, "FileUtils.copyFile, ex=" + e.toString());
        }
        return false;
    }

    private static File getExternalDirectory(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null && !cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                L.e(TAG, "无法创建SDCard cache");
                return null;
            }
        }

        return cacheDir;
    }

    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;

        if (preferExternal && Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState())) {
            appCacheDir = getExternalDirectory(context);
        }

        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }

        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            L.d(TAG,
                    "Can't define system cache directory! use " + cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }

        return appCacheDir;
    }

    public static boolean isEmptyFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            return true;
        }

        File[] list = folder.listFiles();
        return list == null || list.length == 0;
    }

    /**
     * 按修改时间排序
     *
     * @param folderPath
     */
    public static List<File> sortFiles(String folderPath) {
        File folder = new File(folderPath);
        if (isEmptyFolder(folder)) {
            return null;
        }
        try {
            File[] list = folder.listFiles();
            List<File> sortedList = new ArrayList<File>();
            for (File file : list) {
                sortedList.add(file);
            }
            Collections.sort(sortedList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.lastModified() > o2.lastModified()) {
                        return 1;
                    } else if (o1.lastModified() == o2.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            return sortedList;
        } catch (Exception e) {
            L.e(TAG, "FileUtils.sortFiles,e=" + e.getMessage());
        }
        return null;
    }

    //目前采用只存储到应用内部的路径
    public static String getLogPath(Context context) {
        String path = context.getFilesDir()
                + "/if/logs" + "/";
//        }
        return path;
    }

    /**
     * 拷贝文件
     *
     * @param str1 源文件
     * @param str2 目标文件
     * @throws IOException
     */
    public static void copy(String str1, String str2) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)) {
            return;
        }
        try {
            File f1 = new File(str1);
            File f2 = new File(str2);
            if (!f2.exists()) {
                f2.createNewFile();
            }
            fis = new FileInputStream(f1);
            fos = new FileOutputStream(f2);
            if (f1.exists()) {
                // 判断是否是文件
                if (f1.isDirectory()) {
                    // 把该目录下的文件用一个File接受，然后再用递归方法重新判断
                    File[] arr = f1.listFiles();
                    if (arr != null) {
                        for (File file2 : arr) {
                            // 返回路径
                            copy(file2.getName(),
                                    str1 + File.separator + file2.getName());
                        }
                    }

                }
                // 判断是否文件
                else if (f1.isFile()) {
                    // 进行文件读写操作
                    byte[] buffer = new byte[1024 * 2];
                    int len = 0;
                    while ((len = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
            fis.close();
            fos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            L.e(TAG, "", e);
        } catch (IOException e) {
            L.e(TAG, "", e);
        }
    }

    public static String removeExtension(String s) {
        String separator = System.getProperty("file.separator");
        String filename;

        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }

        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;

        return filename.substring(0, extensionIndex);
    }

    public static boolean isSdcardValid() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    public static String readInputStream(InputStream is) throws IOException {
        int length = is.available();
        byte[] buffer = new byte[length];
        is.read(buffer);
        return new String(buffer, "UTF-8");
    }

    /**
     * 创建目录
     *
     * @return 如果目录已经存在，或者目录创建成功，返回true；如果目录创建失败，返回false
     */
    public static boolean createFolder(String folderPath) {
        boolean success = false;
        try {
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                success = true;
            } else {
                success = folder.mkdirs();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 创建指定文件的目录。例如filePath=/sdcard/aaa/bbb/ccc/1.txt，会创建/sdcard/aaa/bbb/ccc/目录。
     */
    public static void createFileFolder(String filePath) {
        try {
            new File(filePath).getParentFile().mkdirs();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹中的全部文件
     */
    public static boolean cleanDirectory(String folderPath) {
        if (TextUtils.isEmpty(folderPath)) {
            return false;
        }
        if (isEmptyFolder(new File(folderPath))) {
            return true;
        }
        try {
            for (File tempFile : new File(folderPath).listFiles()) {
                if (tempFile.isDirectory()) {
                    cleanDirectory(tempFile.getPath());
                }
                deleteFile(tempFile);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 这个是手机内存的总空间大小
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 这个是手机内存的可用空间大小
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 返回一个文件大小的字符串
     *
     * @param size 文件长度（单位Byte）
     * @return 文件大小的字符串（单位是MB、KB或者Byte）
     */

    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 将指定文本内容写入文件（指定目录）
     */
    public static boolean writeFile(String filePath, String content,
                                    boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将指定文本内容写入文件（指定文件）
     */
    public static boolean writeFile(File file, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                    fileWriter = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 从指定位置读取文本内容（指定目录）
     */
    public static String readFile(String filePath) {
        FileReader fileReader = null;
        BufferedReader br = null;
        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            // 建立对象fileReader
            fileReader = new FileReader(filePath);
            br = new BufferedReader(fileReader);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append('\n');
            }
            // 将字符列表转换成字符串
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                    fileReader = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 从指定位置读取文本内容（指定文件）
     */
    public static String readFile(File file) {
        FileReader fileReader = null;
        BufferedReader br = null;
        String content = null;
        try {
            StringBuilder sb = new StringBuilder();
            // 建立对象fileReader
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append('\n');
            }
            // 将字符列表转换成字符串
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                    fileReader = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 从assets中获取文件并读取数据（资源文件只能读不能写）
     */
    public static String readAssetsFile(Context context, String fileName) {
        String res = null;
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            res = FileUtils.readInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                is = null;
            }
        }
        return res;
    }

    /**
     * 从assets中获取文件并读取数据（资源文件只能读不能写）
     */
    public static String readRawFile(Context context, int fileResId) {
        String res = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(fileResId);
            res = FileUtils.readInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                is = null;
            }
        }
        return res;
    }

    public static String getExternalFilesAbsolutePath(Context context) {
        return FileUtils.isSdcardValid() ? context.getExternalFilesDir(null).getAbsolutePath() : "/sdcard/Android/data/com.sankuai.xmpp/files";
    }

    public static File getFile(Uri uri, ContentResolver resolver) {
        File file = null;
        try {
            String path = uri.toString();
            if (path.startsWith("content")) {
                Cursor cursor = resolver.query(uri, null, null, null, null);
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                cursor.close();
                file = new File(path);
            } else {
                file = new File(new URI(path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;

    }

    public static String getMimeType(File file) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String fileName = file.getName();
        if (fileName != null) {
            int index = file.getName().lastIndexOf('.');
            if (index != -1) {
                return mimeTypeMap.getMimeTypeFromExtension(fileName.toLowerCase().substring(index + 1));
            }
        }
        return null;
    }

    public static String getMimeType(String fileName) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if (!TextUtils.isEmpty(fileName)) {
            int index = fileName.lastIndexOf('.');
            if (index != -1) {
                return mimeTypeMap.getMimeTypeFromExtension(fileName.toLowerCase().substring(index + 1));
            }
        }
        return null;
    }

    public static String getExternalDir(Context context, String dir) {
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalFilesDir(dir);

        if (file != null) {
            sb.append(file.getAbsolutePath());
        }
        return sb.toString();
    }

    public static String getExternalCacheDir(Context context) {
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalCacheDir();
        if (file != null) {
            sb.append(file.getAbsolutePath());
        } else {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                        .append("/cache").toString();
            } else {
                sb.append(context.getCacheDir().getAbsolutePath());
            }

        }
        return sb.toString();
    }

    public static String getExternalFileDir(Context context) {
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalFilesDir("");
        if (file != null) {
            sb.append(file.getAbsolutePath());
        } else {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(context.getPackageName())
                        .append("/files").toString();
            } else {
                sb.append(context.getCacheDir().getAbsolutePath());
            }

        }
        return sb.toString();
    }

}
