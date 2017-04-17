package com.douyaim.qsapp.permissionhelp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.PermissionChecker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangw on 2017/1/23.
 */

 class Utils {

    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Context context, String... permissions){
        List<String> pers = new ArrayList<>();
        for (String permission : permissions) {
            if(PermissionChecker.checkSelfPermission(context,permission) != PermissionChecker.PERMISSION_GRANTED){
                pers.add(permission);
            }
        }
        return pers;
    }

//    public static List<Method> findAnnotaionMethods(Class clazz, Class<? extends Annotation> annClazz){
//        List<Method> methods = new ArrayList<>();
//        for (Method method:clazz.getDeclaredMethods()){
//            if(method.isAnnotationPresent(annClazz))
//                methods.add(method);
//        }
//        return methods;
//    }

    public static <T extends Annotation> Method findMethodWithRequestCode(Class clazz,Class<T> annClaz,int requestCode){
        for (Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(annClaz)) {
                if (isEqualRequestCode(method,annClaz,requestCode))
                    return method;
            }
        }
        return null;
    }

    public static boolean isEqualRequestCode(Method m,Class clazz,int requestCode){
        if (clazz.equals(PermissionFail.class)){
            return requestCode == m.getAnnotation(PermissionFail.class).value();
        }else if(clazz.equals(PermissionSuccess.class)){
            return requestCode == m.getAnnotation(PermissionSuccess.class).value();
        }else {
            return false;
        }
    }


}
