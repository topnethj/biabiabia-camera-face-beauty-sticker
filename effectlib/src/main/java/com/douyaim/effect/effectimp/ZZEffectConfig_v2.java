package com.douyaim.effect.effectimp;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import com.douyaim.effect.utils.OpenGlUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by hj on 16/9/21.
 */
public class ZZEffectConfig_v2  implements Serializable {

    public String version;
    public String dirPath;

    @SerializedName("2d")
    public List<ZZEffect2DItem_v2> item2ds;
    @SerializedName("audio")
    public List<ZZEffectAudioItem_v2> audioItems;
    @SerializedName("face")
    public ZZEffectFaceItem_v2 faceItem;
    @SerializedName("screen")
    public ZZEffectTieZhiItem_v2 tieZhiItem;

    public static ZZEffectConfig_v2 getEffectConfig(@NonNull String filePath){
        String configPath = filePath + "config.js";
        String json = OpenGlUtils.readStringFromSD(configPath);
        if(json == null){
            return null;
        }
        try{
            Gson gson = new Gson();
            ZZEffectConfig_v2 effectConfig = gson.fromJson(json, ZZEffectConfig_v2.class);
            effectConfig.dirPath = filePath;

            if(effectConfig.faceItem != null){
                effectConfig.faceItem.setDirPath(filePath + "face/");
            }
            if(effectConfig.audioItems != null){
                for(ZZEffectAudioItem_v2 audioItem : effectConfig.audioItems){
                    audioItem.setDirPath(filePath + "audio/");
                }
            }
            if(effectConfig.item2ds != null){
                for(ZZEffect2DItem_v2 item : effectConfig.item2ds){
                    item.setDirPath(filePath + "2d/");
                }
            }
            if(effectConfig.tieZhiItem != null){
                effectConfig.tieZhiItem.setDirPath(filePath + "2d/");
            }

            return effectConfig;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized String effectConfigCopy(Context context, String path) {
        String rootPath = getExternalCacheDir(context);
        String configPath = rootPath + File.separator + path;
        if (new File(configPath).exists()) {
            return configPath + File.separator;
        }
        copyFiles(context, rootPath, path);
        return configPath + File.separator;
    }

    private static void copyFiles(Context context, String rootPath, String path) {
        try {
            String str[] = context.getAssets().list(path);
            if (str.length > 0) {
                File file = new File(rootPath + "/" + path);
                file.mkdirs();
                for (String string : str) {
                    path = path + "/" + string;
                    copyFiles(context, rootPath, path);
                    path = path.substring(0, path.lastIndexOf('/'));
                }
            } else {
                File file = new File(rootPath + "/" + path);
                if(!file.exists()) {
                    InputStream is = context.getAssets().open(path);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    //int count = 0;
                    while (true) {
                        //count++;
                        int len = is.read(buffer);
                        if (len == -1) {
                            break;
                        }
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
