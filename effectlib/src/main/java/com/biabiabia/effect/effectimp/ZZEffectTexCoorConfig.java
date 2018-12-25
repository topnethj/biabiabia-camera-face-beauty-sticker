package com.biabiabia.effect.effectimp;

import android.support.annotation.NonNull;

import com.biabiabia.effect.utils.OpenGlUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hj on 17/3/17.
 */

public class ZZEffectTexCoorConfig implements Serializable {

    public String version;
    public String dirPath;
    @SerializedName("texCoor")
    public List<ZZEffectTexCoorItem> texCoorItems;

    public static ZZEffectTexCoorConfig getTexCoorConfig(@NonNull String filePath) {
        String configPath = filePath + "uv_config.js";
        String json = OpenGlUtils.readStringFromSD(configPath);
        if (json == null) {
            return null;
        }
        try {
            Gson gson = new Gson();
            ZZEffectTexCoorConfig texCoorConfig = (ZZEffectTexCoorConfig) gson.fromJson(json, ZZEffectTexCoorConfig.class);
            texCoorConfig.dirPath = filePath;
            if (texCoorConfig.texCoorItems != null) {
                for (ZZEffectTexCoorItem item : texCoorConfig.texCoorItems) {
                    item.setDirPath(filePath + "2d/");
                }
            }
            return texCoorConfig;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
