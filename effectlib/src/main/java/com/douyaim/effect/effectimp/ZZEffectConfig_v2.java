package com.douyaim.effect.effectimp;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.douyaim.effect.utils.OpenGlUtils;
import com.douyaim.effect.utils.ZIPExtract;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileInputStream;
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

    public ZZEffectBlendItem_v2 blendItem;

    public static ZZEffectConfig_v2 getEffectConfig(@NonNull String filePath){
        String configPath = filePath + "config.js";
        String json = OpenGlUtils.readStringFromSD(configPath);
        if(json == null){
            return null;
        }
        try{
            Gson gson = new Gson();
            ZZEffectConfig_v2 effectConfig = (ZZEffectConfig_v2)gson.fromJson(json, ZZEffectConfig_v2.class);
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

    public static void effectConfigUnZip(Context context) {
        /*
        List<String> zipList = new ArrayList<String>();
        try {
            String[] assetsFlist = context.getAssets().list("effect");
            for(int i=0; i<assetsFlist.length; i++){
                if(assetsFlist[i].endsWith(".zip")){
                    zipList.add(assetsFlist[i]);
                }
            }
        } catch (IOException e) {
        }
        for(String zip : zipList){
            String srcPath = "file:///assets/effect/" + zip;
            effectConfigUnZip1(context, srcPath, null);
        }*/
    }

    public static synchronized String effectConfigUnZip1(Context context, @NonNull String srcPath, @NonNull String configName) {
        String unzipPath = ZIPExtract.getUnzipPath(srcPath);
        String zipName = ZIPExtract.obtFileName(srcPath);
        String config1 = unzipPath + File.separator + configName;
        String config2 = unzipPath + File.separator + zipName + File.separator + configName;

        File unzipFile = new File(unzipPath);
        if(unzipFile.exists()){
            if(unzipFile.list().length > 0){
                if(new File(config1).exists()){
                    return unzipPath + File.separator;
                }else if(new File(config2).exists()){
                    return unzipPath + File.separator + zipName + File.separator;
                }
            }
        }else{
            unzipFile.mkdir();
        }

        AssetManager am = context.getResources().getAssets();
        InputStream is = null;
        try {
            is = am.open(srcPath.substring(srcPath.indexOf("effect")));
        } catch (IOException e) {
        }
        if(is == null){
            return null;
        }

        boolean r = ZIPExtract.unpackZip(is, unzipFile);
        if(r){
            if(new File(config1).exists()){
                return unzipPath + File.separator;
            }else if(new File(config2).exists()){
                return unzipPath + File.separator + zipName + File.separator;
            }
        }
        return null;
    }

    public static synchronized String effectConfigUnZip(Context context, @NonNull String url) {
        try{
            String rootPath = ZIPExtract.downRootPath();
            String unzipPath = ZIPExtract.getUnzipPath(url);
            File zipFile = ZIPExtract.obtSaveFile(url);
            if(!zipFile.exists()){
                return null;
            }
            File unzipFile = new File(unzipPath);
            if(!unzipFile.exists()){
                unzipFile.mkdir();
            }
            FileInputStream inputStream = new FileInputStream(zipFile);
            boolean r = ZIPExtract.unpackZip(inputStream, unzipFile);
            if(r && unzipFile.length() > 0){
                return unzipPath + File.separator;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
