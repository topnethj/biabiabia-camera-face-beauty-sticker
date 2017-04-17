package com.douyaim.effect.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.douyaim.qsapp.LibApp;
import com.douyaim.qsapp.utils.FileUtils;
import com.douyaim.qsapp.utils.MD5Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Hj
 */
public class ZIPExtract {

    private static final String FNAME_EFFECT = "effect";
    private static int BUFFER_DEFAULT_SIZE = 1024;
    private static String EFFECT_ROOT_PATH;

    public static boolean isAlreadyEffect(@NonNull File downFile) {
        if (downFile.exists() && downFile.listFiles().length > 1) {
            return true;
        }
        return false;
    }

    public synchronized static String downRootPath() {
        if(EFFECT_ROOT_PATH == null){
            EFFECT_ROOT_PATH = FileUtils.getExternalCacheDir(LibApp.getAppContext()) + File.separator + FNAME_EFFECT;
        }
        File file = new File(EFFECT_ROOT_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        return EFFECT_ROOT_PATH;
    }

    public static String getUnzipPath(@NonNull String downUrl) {
        String strPath = downRootPath() + File.separator + obtSaveFileName(downUrl);
        //File file = new File(strPath);
        //if (!file.exists()) {
        //    file.mkdir();
        //}
        return strPath;
    }

    public static String obtSaveString(@NonNull String downUrl) {
        return getUnzipPath(downUrl) + ".zip";
    }

    public static File obtSaveFile(@NonNull String downUrl) {
        return new File(getUnzipPath(downUrl) + ".zip");
    }
    
    public static String obtFileName(String downUrl) {
        if (TextUtils.isEmpty(downUrl)) {
            return null;
        }
        return downUrl.substring(downUrl.lastIndexOf("/") + 1, downUrl.length() - 4);
    }

    public static String obtSaveFileName(@NonNull String downUrl) {
        return MD5Utils.getMD5String(downUrl).substring(8, 24);
    }

    public static boolean unpackZip(@NonNull String file, @NonNull File dir) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return unpackZip(inputStream, dir);
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static boolean unpackZip(@NonNull InputStream inputStream, @NonNull File dir) {
        ZipInputStream zipInputStream = null;
        boolean ze;
        try {
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

}
