package com.douyaim.qsapp.permissionhelp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限申请成功执行方法
 * Created by wangw on 2017/1/23.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionSuccess {
    int value();
}
