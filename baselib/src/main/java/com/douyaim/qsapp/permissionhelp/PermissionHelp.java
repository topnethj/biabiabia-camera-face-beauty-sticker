package com.douyaim.qsapp.permissionhelp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import com.douyaim.qsapp.LibApp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限申请助手类
 * Created by wangw on 2017/1/23.
 */

public class PermissionHelp {

    private String[] mPermissions;
    private int mRequestCode;
    private Object mProxy;

    private PermissionHelp(Object proxy){
        mProxy = proxy;
    }

    public static PermissionHelp with(Activity activity){
        return new PermissionHelp(activity);
    }

    public static PermissionHelp with(Fragment fragment){
        return new PermissionHelp(fragment);
    }

    public PermissionHelp setRequestCode(int requestCode){
        this.mRequestCode = requestCode;
        return this;
    }

    public PermissionHelp permission(String... permissions){
        this.mPermissions = permissions;
        return this;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    public void request(){
        onRequestPermissions(mProxy,mRequestCode,mPermissions);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void onRequestPermissions(Object proxy, int requestCode, String[] permissions) {
        if (!Utils.isOverMarshmallow()){
            onSuccess(proxy,requestCode);
            return;
        }

        if (proxy == null) {
            return;
        }
        List<String> denPers = Utils.findDeniedPermissions(getContext(),permissions);
        if (denPers != null && !denPers.isEmpty()){
            String[] pers = denPers.toArray(new String[denPers.size()]);
            if (proxy instanceof Activity){
                ((Activity) proxy).requestPermissions(pers,requestCode);
            }else if(proxy instanceof Fragment){
                ((Fragment) proxy).requestPermissions(pers,requestCode);
            }
        }else {
            onSuccess(proxy,requestCode);
        }
    }

    private static Activity getActivity(Object proxy) {
        if (proxy instanceof Fragment){
            return ((Fragment)proxy).getActivity();
        }else if (proxy instanceof Activity){
            return (Activity) proxy;
        }
        return null;
    }

    private static void onSuccess(Object proxy, int requestCode) {
        Method method = Utils.findMethodWithRequestCode(proxy.getClass(),PermissionSuccess.class,requestCode);
        executeMethod(proxy,method);
    }



    private static void executeMethod(Object proxy, Method method) {
        if(method == null || proxy == null)
            return;
        try {
            if(!method.isAccessible())
                method.setAccessible(true);
            method.invoke(proxy);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void onRequestPermissionsResult(Activity activity,int requestCode,String[] permissions,int[] grantResults){
        onRequestResult(activity,requestCode,permissions,grantResults);
    }

    public static void onRequestPermissionsResult(Fragment fragment,int requestCode,String[] permissions,int[] grantResults){
        onRequestResult(fragment,requestCode,permissions,grantResults);
    }

    private static void onRequestResult(Object proxy, int requestCode, String[] permissions, int[] grantResults) {
        List<String> denPers = new ArrayList<>();
        int lenth = grantResults.length;
        for (int i = 0; i < lenth; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                denPers.add(permissions[i]);
        }

        if(denPers.isEmpty()){
            onSuccess(proxy,requestCode);
        }else {
            onFail(proxy,requestCode);
        }

    }

    private static void onFail(Object proxy, int requestCode) {
        Method method = Utils.findMethodWithRequestCode(proxy.getClass(),PermissionFail.class,requestCode);
        executeMethod(proxy,method);
    }

    private static Context getContext(){
        return LibApp.getAppContext();
    }


    public static boolean hasPermission(String... permissions){
        if(!Utils.isOverMarshmallow())
            return true;

        return Utils.findDeniedPermissions(getContext(),permissions).isEmpty();
    }

}
