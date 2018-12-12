package com.douyaim.effect.Filter;

import android.support.annotation.NonNull;
import com.douyaim.effect.effectimp.ZZEffect2DEngine_v2;
import com.douyaim.effect.effectimp.ZZEffectAudioEngine_v2;
import com.douyaim.effect.effectimp.ZZEffectBlendEngine_v2;
import com.douyaim.effect.effectimp.ZZEffectConfig_v2;
import com.douyaim.effect.effectimp.ZZEffectTexCoorConfig;
import com.douyaim.effect.effectimp.ZZEffectTexCoorItem;
import com.douyaim.effect.effectimp.ZZEffectTextureManager;
import com.douyaim.effect.face.ZZFaceManager_v2;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.effect.model.AndroidSize;
import com.douyaim.effect.utils.OpenGlUtils;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * Created by hj on 16/9/5.
 */
public class ZZEffectFilter_v2 extends GPUImageFilter {

    boolean isFace;
    AndroidSize bufferSize;
    AndroidSize frameSize;
    String dirPath;
    ZZEffectConfig_v2 effectConfig_v2;
    ZZEffectTexCoorConfig texCoorConfig;

    GPUImageFilter blendFilter;
    ZZEffectFaceFilter_v2 _faceFilter;
    ZZEffectAudioEngine_v2 _audioEngine;
    ZZEffect2DEngine_v2 _engine2d;
    ZZEffectTieZhiFilter_v2 _tieZhiFilter;

    public ZZEffectFilter_v2(@NonNull String dirPath, @NonNull AndroidSize bufferSize, AndroidSize frameSize, boolean isFace) {
        super();
        this.bufferSize = bufferSize;
        this.frameSize = frameSize;
        this.dirPath = dirPath;
        this.isFace = isFace;
        this.effectConfig_v2 = ZZEffectConfig_v2.getEffectConfig(dirPath);
        this.texCoorConfig = ZZEffectTexCoorConfig.getTexCoorConfig(dirPath);
    }

    private void loadResources() {
        //uv
        ZZEffectTextureManager.getZZEffectTextureManager().clearTexCoorCache();

        // 3d particle meshes
        // 3d normal meshes
        // 2d textures

        //for (ZZEffect2DItem_v2 *item in _config.item2ds)
        if (texCoorConfig != null && texCoorConfig.texCoorItems != null && texCoorConfig.texCoorItems.size() > 0) {
            // 2d textures
            for (ZZEffectTexCoorItem item : texCoorConfig.texCoorItems) {
                String imagePath = item.getDirPath() + item.getImageName();
                if(imagePath == null || !new File(imagePath).exists()){
                    continue;
                }
                ZZEffectTextureManager.getZZEffectTextureManager().loadTextureByPath(imagePath);
            }
            ZZEffectTextureManager.getZZEffectTextureManager().loadTexCoorWithItems(texCoorConfig.texCoorItems);
        }
    }

    private void unloadResources() {
        // 2d textures
        //for (ZZEffect2DItem_v2 *item in _config.item2ds)
        if (texCoorConfig != null && texCoorConfig.texCoorItems != null) {
            for (ZZEffectTexCoorItem item : texCoorConfig.texCoorItems) {
                String imagePath = item.getDirPath() + item.getImageName();
                ZZEffectTextureManager.getZZEffectTextureManager().removeByPath(imagePath);
            }
        }

        //uv
        ZZEffectTextureManager.getZZEffectTextureManager().clearTexCoorCache();
    }

    public void install() {
        if(effectConfig_v2 != null){
            // 加载资源
            loadResources();

            //face
            if(effectConfig_v2.faceItem != null && isFace){
                _faceFilter = new ZZEffectFaceFilter_v2(effectConfig_v2.faceItem);
                _faceFilter.onDisplaySizeChanged(bufferSize.width, bufferSize.height);
                _faceFilter.onInputSizeChanged(frameSize.width, frameSize.height);
                _faceFilter.init();
            }

            //2d
            _engine2d = new ZZEffect2DEngine_v2(bufferSize);
            if (effectConfig_v2.item2ds != null && effectConfig_v2.item2ds.size() > 0) {
                _engine2d.initWithItems(effectConfig_v2.item2ds);
            }

            //TODO:3D初始化调试

            //贴纸
            if(effectConfig_v2.tieZhiItem != null){
                _tieZhiFilter = new ZZEffectTieZhiFilter_v2(effectConfig_v2.tieZhiItem);
                _tieZhiFilter.onDisplaySizeChanged(bufferSize.width, bufferSize.height);
                _tieZhiFilter.onInputSizeChanged(frameSize.width, frameSize.height);
                _tieZhiFilter.init();
            }

            //声音
            if (effectConfig_v2.audioItems != null && effectConfig_v2.audioItems.size() > 0) {
                _audioEngine = new ZZEffectAudioEngine_v2();
                _audioEngine.initWithItems(effectConfig_v2.audioItems);
            }
        }

        blendFilter = ZZEffectBlendEngine_v2.genBlendFilterWithItem(null, 2);

        blendFilter.init();
        blendFilter.onDisplaySizeChanged(bufferSize.width, bufferSize.height);
        blendFilter.onInputSizeChanged(frameSize.width, frameSize.height);
    }

    public int[] onDrawFrame(final long timeMillis, List<ZZFaceResult> faceResult, int my_ttid) {
        int t1 = my_ttid;
        int t2 = OpenGlUtils.NO_TEXTURE;
        int t3 = OpenGlUtils.NO_TEXTURE;

        if (effectConfig_v2 == null) {
            return new int[]{t1,t2,t3};
        }

        if(_faceFilter != null){
            //face缩放和平移的因子
            _faceFilter.updateSizeWithScaleX(effectConfig_v2.scaleX, effectConfig_v2.scaleY, effectConfig_v2.posX, effectConfig_v2.posY);
            _faceFilter.updateWithFaceResults(faceResult);
            t1 = _faceFilter.onDrawFrame(my_ttid);
        }

        if(_tieZhiFilter != null){
            _tieZhiFilter.update(faceResult);
            t1 = _tieZhiFilter.onDrawFrame(my_ttid);
        }

        ZZFaceManager_v2.getZZFaceManager().turnFaceResultWithScaleX(faceResult, effectConfig_v2.scaleX,
                effectConfig_v2.scaleY, effectConfig_v2.posX, effectConfig_v2.posY);

        if(_engine2d != null){
            _engine2d.renderStart();
            if (effectConfig_v2.item2ds != null && effectConfig_v2.item2ds.size() > 0) {
                _engine2d.updateWithFaceResult(faceResult);
            }
            t2 = _engine2d.renderEnd();
        }

        if(_audioEngine != null){
            if (faceResult.size() > 0){
                //_audioEngine.updateWithFaceStatus(faceResult.get(0).getFaceStatus());
                for (int i = 0; i < faceResult.size(); i++) {
                    _audioEngine.updateWithFaceStatus(faceResult.get(i).getFaceStatus());
                }
            }else{
                _audioEngine.updateWithFaceStatus(ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
            }
        }

        return new int[]{t1,t2,t3};
    }

    public void blend(int[] textures, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        ((GPUImageTwoInputFilter)blendFilter).mFilterSourceTexture2 = textures[1];
        if(vertexBuffer != null && textureBuffer != null){
            blendFilter.onDrawFrame(textures[0], vertexBuffer, textureBuffer);
        }else{
            blendFilter.onDrawFrame(textures[0]);
        }
    }

    public void uninstall() {
        unloadResources();
        if(_engine2d != null){
            _engine2d.reset();
        }
        if(_faceFilter != null){
            _faceFilter.destroy();
        }
        if(_tieZhiFilter != null){
            _tieZhiFilter.destroy();
        }
        if(_audioEngine != null){
            _audioEngine.destroy();
        }
        if(blendFilter != null){
            blendFilter.destroy();
        }
    }

    public void run() {
        //[_engine3d run];
    }

    public void stop() {
        //[_engine3d stop];
    }

    public void dealloc() {
    }
}
