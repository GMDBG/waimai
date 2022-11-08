package com.example.rgwaimai.common;

/**
 * 自定义业务异常
 * @authro zl
 * @create 2022-11-06-19:00
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}