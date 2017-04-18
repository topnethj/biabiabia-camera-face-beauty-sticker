package com.douyaim.qsapp.camera.filter;

import android.content.Context;

public class QSFilterManager {

    private QSFilterManager() {
    }

    public static IFilter getCameraFilter(FilterType filterType, Context context) {
        switch (filterType) {
            case Normal:
                return new CameraFilter(context);
            case Meiyan://神还原 filter_icon_origin.jpg null
                return new MeiyanFilter(context);
            default:
                return new CameraFilter(context);
        }
    }

    public static String getFilterTag(FilterType filterType) {
        switch (filterType) {
            case Normal:
                return "Normal";
            case Meiyan:
                return "Meiyan";
            default:
                return "Normal";
        }
    }

    public static FilterType getFilterType(String filterTypeTag) {
        if("Normal".equals(filterTypeTag)){
            return FilterType.Normal;
        }else if("Meiyan".equals(filterTypeTag)){
            return FilterType.Meiyan;
        }else{
            return FilterType.Normal;
        }
    }

    // 滤镜类型
    public enum FilterType {
        Normal, Meiyan, TX
    }

}
