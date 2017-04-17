package com.douyaim.effect.effectimp;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.NonNull;
import com.douyaim.qsapp.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by hj on 16/10/20.
 */

public class ZZEffectAudioEngine_v2 {

    private Vector<ZZEffectAudioPlayer_v2> players = new Vector<>();
    private List<String> sPaths = new ArrayList<>();
    private boolean running;
    private SoundPool soundPool;

    public void initWithItems (@NonNull List<ZZEffectAudioItem_v2 > items) {
        if(items.size() > 0){
            soundPool = new SoundPool(items.size(), AudioManager.STREAM_MUSIC, 0);
        }
        for (ZZEffectAudioItem_v2 item : items) {
            if(StringUtils.isEmpty(item.getFilename())){
                continue;
            }
            String path = item.getDirPath() + item.getFilename();
            if(sPaths.contains(path)){
                continue;
            }else{
                sPaths.add(path);
            }
            ZZEffectAudioPlayer_v2 player = new ZZEffectAudioPlayer_v2();
            player.initWithItem(item, soundPool);
            players.add(player);
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                for(ZZEffectAudioPlayer_v2 player : players){
                    player.isLoaded = true;
                }
            }
        });
        running = true;
    }

    public void updateWithFaceStatus(int faceStatus) {
        for (ZZEffectAudioPlayer_v2 player : players) {
            if (running) {
                player.updateWithFaceResult(faceStatus);
            }
        }
    }

    public void playSound(@NonNull String name) {
        for(ZZEffectAudioPlayer_v2 player : players){
            if(player._item.getName() != null && player._item.getName().contains(name)){
                player.play();
                break;
            }
        }
    }

    public void destroy() {
        running = false;
        for(ZZEffectAudioPlayer_v2 player : players){
            player.destroy();
        }
        players.clear();
        sPaths.clear();
        if(soundPool != null){
            soundPool.release();
        }
    }

}
