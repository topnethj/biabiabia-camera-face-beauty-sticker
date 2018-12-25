package com.biabiabia.effect.effectimp;

import android.opengl.GLES20;
import android.support.annotation.NonNull;

import com.biabiabia.effect.utils.OpenGlUtils;
import com.biabiabia.effect.LibApp;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hj on 16/9/5.
 */
public class ZZEffectTextureManager {

    public static ZZEffectTextureManager TextureMgr;
    private Map<String, Integer> textureMap = new HashMap<>();
    private Map<String, ZZEffectTexCoorItem> texCoors = new HashMap<>();

    private ZZEffectTextureManager(){};

    public static ZZEffectTextureManager getZZEffectTextureManager(){
        if (TextureMgr == null) {
            synchronized (ZZEffectTextureManager.class) {
                if (TextureMgr == null) {
                    TextureMgr = new ZZEffectTextureManager();
                }
            }
        }
        return TextureMgr;
    }

    public synchronized void loadTextureByPath(@NonNull String path){
        if(!textureMap.containsKey(path)){
            int texture = OpenGlUtils.loadTexture(LibApp.getAppContext(), path);
            textureMap.put(path, texture);
        }
    }

    public synchronized void loadTextureByPath(@NonNull String[] paths){
        for(int i = 0; i < paths.length; i++){
            loadTextureByPath(paths[i]);
        }
    }

    public int getTextureByPath(@NonNull String path){
        loadTextureByPath(path);
        return textureMap.get(path);
    }

    public synchronized void removeByPath(@NonNull String path){
        if(textureMap.containsKey(path)){
            int texture = getTextureByPath(path);
            GLES20.glDeleteTextures(1, IntBuffer.wrap(new int[]{texture}));
            textureMap.remove(path);
        }
    }

    public synchronized void removeByPath(@NonNull String[] paths){
        for(int i = 0; i < paths.length; i++){
            removeByPath(paths[i]);
        }
    }

    //每个element对应的纹理信息
    public synchronized void loadTexCoorWithItems(@NonNull List<ZZEffectTexCoorItem> items) {
        for (ZZEffectTexCoorItem item : items) {
            texCoors.put(item.getElementName(), item);
        }
    }

    public synchronized ZZEffectTexCoorItem getTexCoorByName(String name) {
        return texCoors.get(name);
    }

    public synchronized void clearTexCoorCache() {
        texCoors.clear();
    }

    public synchronized void clear(){
        if(textureMap.size() > 0){
            Object[] ts = textureMap.values().toArray();
            int[] textures = new int[ts.length];
            for(int i = 0; i < ts.length; i++){
                textures[i] = ((Integer)ts[i]).intValue();
            }
            GLES20.glDeleteTextures(textures.length, IntBuffer.wrap((textures)));
            textureMap.clear();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        clear();
    }

}
