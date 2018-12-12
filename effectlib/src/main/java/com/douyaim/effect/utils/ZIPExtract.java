package com.douyaim.effect.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.douyaim.qsapp.LibApp;
import com.douyaim.qsapp.utils.FileUtils;
import com.douyaim.qsapp.utils.MD5Utils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Hj
 */
public class ZIPExtract {

    private static int BUFFER_DEFAULT_SIZE = 1024 * 300;
    private static String RES_ROOT_PATH;

    public synchronized static String downRootPath(@NonNull String subPath) {
        if(RES_ROOT_PATH == null){
            RES_ROOT_PATH = FileUtils.getExternalCacheDir(LibApp.getAppContext());
        }
        File file = new File(RES_ROOT_PATH, subPath);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getUnzipPath(@NonNull String downUrl, @NonNull String subPath) {
        String strPath = downRootPath(subPath) + File.separator + obtSaveFileName(downUrl);
        return strPath;
    }

    public static String getUnzipPath(@NonNull String zipFilePath) {
        if(zipFilePath.endsWith(".zip")) {
            return zipFilePath.substring(0, zipFilePath.lastIndexOf(".zip"));
        }
        return null;
    }

    public static String obtSaveString(@NonNull String downUrl, @NonNull String subPath) {
        String extension = "";
        String fNameSuffix = downUrl.substring(downUrl.lastIndexOf("/") + 1);
        if(fNameSuffix.lastIndexOf(".") != -1) {
            extension = fNameSuffix.substring(fNameSuffix.lastIndexOf("."));
        }
        return getUnzipPath(downUrl, subPath) + extension;
    }

    public static File obtSaveFile(@NonNull String downUrl, @NonNull String subPath) {
        String fPath = obtSaveString(downUrl, subPath);
        return new File(fPath);
    }
    
    public static String obtFileName(String downUrl) {
        if (TextUtils.isEmpty(downUrl)) {
            return null;
        }
        String fNameSuffix = downUrl.substring(downUrl.lastIndexOf("/") + 1);
        if(fNameSuffix.lastIndexOf(".") != -1) {
            return fNameSuffix.substring(0, fNameSuffix.lastIndexOf("."));
        } else {
            return fNameSuffix;
        }
    }

    public static String obtSaveFileName(@NonNull String downUrl) {
        return MD5Utils.getMD5String(downUrl).substring(8, 24);
    }

    public static boolean unpackZip(@NonNull String file, @NonNull File dir) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            if(!dir.exists()) {
                dir.mkdir();
            }
            return unpackZip(inputStream, dir);
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static boolean unpackZip(@NonNull InputStream inputStream, @NonNull File dir) {
        ZipInputStream zipInputStream = null;
        boolean ze;
        try {
            if(!dir.exists()) {
                dir.mkdir();
            }

            zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            byte[] e1 = new byte[BUFFER_DEFAULT_SIZE];

            while (true) {
                ZipEntry ze1;
                while ((ze1 = zipInputStream.getNextEntry()) != null) {
                    String e = ze1.getName();
                    if (ze1.isDirectory()) {
                        (new File(dir, e)).mkdirs();
                    } else {
                        FileOutputStream outputStream = new FileOutputStream(new File(dir, e));

                        int count;
                        while ((count = zipInputStream.read(e1)) != -1) {
                            outputStream.write(e1, 0, count);
                        }

                        outputStream.close();
                        zipInputStream.closeEntry();
                    }
                }
                return true;
            }
        } catch (Exception var22) {
            var22.printStackTrace();
            ze = false;
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (Exception var21) {
                    var21.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception var20) {
                    var20.printStackTrace();
                }
            }
        }
        return ze;
    }

    public static void zipFiles(@NonNull List<String> resPaths, @NonNull String zipPath) throws Exception {
        ZipOutputStream zipOut = null;
        try{
            zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipPath), BUFFER_DEFAULT_SIZE));
            for (String resPath : resPaths) {
                zipFile(new File(resPath), zipOut, "");
            }
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            try {
                zipOut.closeEntry();
                zipOut.close();
            }catch(Exception e) {
            }
        }
    }

    private static void zipFile(File resFile, ZipOutputStream zipOut, String rootPath) throws Exception {
        BufferedInputStream in = null;
        try {
            rootPath = rootPath + (rootPath.trim().length() == 0 ? "" : File.separator)
                    + resFile.getName();
            rootPath = new String(rootPath.getBytes(), "utf-8");
            if (resFile.isDirectory()) {
                File[] fileList = resFile.listFiles();
                for (File file : fileList) {
                    zipFile(file, zipOut, rootPath);
                }
            } else {
                byte buffer[] = new byte[BUFFER_DEFAULT_SIZE];
                in = new BufferedInputStream(new FileInputStream(resFile), BUFFER_DEFAULT_SIZE);
                zipOut.putNextEntry(new ZipEntry(rootPath));
                int realLength;
                while ((realLength = in.read(buffer)) != -1) {
                    zipOut.write(buffer, 0, realLength);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
                zipOut.flush();
            } catch (Exception e) {
            }
        }
    }
}
