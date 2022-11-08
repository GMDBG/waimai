package com.example.rgwaimai.common;

/**
 * 基于ThreadLocal封装的工具类，用户保存和获取当前登陆用户id
 * @authro zl
 * @create 2022-11-06-10:44
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}