package com.example.rgwaimai.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @authro zl
 * @create 2022-11-04-22:05
 */
//如果有RestController和Controller注解则拦截
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ExceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error("错误信息{}",ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("网络错误");
    }


    @ExceptionHandler(CustomException.class)
    public R<String> ExceptionHandler(CustomException ex){
        log.error("错误信息{}",ex.getMessage());
        return R.error(ex.getMessage());
    }
}