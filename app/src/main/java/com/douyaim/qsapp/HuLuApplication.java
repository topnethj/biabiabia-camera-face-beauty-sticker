package com.douyaim.qsapp;

import com.douyaim.qsapp.utils.FileUtils;

public class HuLuApplication extends LibApp {

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.initFilePath(this);
    }

}
