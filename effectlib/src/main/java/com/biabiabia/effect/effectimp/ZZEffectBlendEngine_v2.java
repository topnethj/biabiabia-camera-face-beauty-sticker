package com.biabiabia.effect.effectimp;

import android.support.annotation.NonNull;

import com.biabiabia.effect.Filter.GPUImageFilter;
import com.biabiabia.effect.Filter.GPUImageTwoInputFilter;
import com.biabiabia.effect.utils.OpenGlUtils;
import com.biabiabia.effect.LibApp;

/**
 * Created by hj on 16/9/21.
 */
public class ZZEffectBlendEngine_v2 {

    public static GPUImageFilter genBlendFilterWithItem(@NonNull ZZEffectBlendItem_v2 item, int count) {
        if(!(count > 0 && count <= 3)){
            return null;
        }
        String vshPath = null;
        String fshPath = null;

        if(item != null){
            vshPath = item.getDirPath() + item.getVertexName();
            fshPath = item.getDirPath() + item.getFragmentName();
        }

        if(vshPath == null || fshPath == null) {
            if(count == 1){
                vshPath = "resource/blend1.vsh";
                fshPath = "resource/blend1.fsh";
            }else if(count == 2){
                vshPath = "resource/blend2.vsh";
                fshPath = "resource/blend2.fsh";
            }else if(count == 3){
                vshPath = "resource/blend3.vsh";
                fshPath = "resource/blend3.fsh";
            }
        }

        String vshString = OpenGlUtils.readShaderFromAssetsFile(LibApp.getAppContext(), vshPath);
        String fshString = OpenGlUtils.readShaderFromAssetsFile(LibApp.getAppContext(), fshPath);

        GPUImageFilter blendFilter = new GPUImageTwoInputFilter(vshString, fshString, false);

        return blendFilter;
    }

}
