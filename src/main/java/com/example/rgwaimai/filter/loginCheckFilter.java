package com.example.rgwaimai.filter;

import com.alibaba.fastjson.JSON;
import com.example.rgwaimai.common.BaseContext;
import com.example.rgwaimai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否正确登陆，过滤器
 * @authro zl
 * @create 2022-11-02-8:38
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter" ,urlPatterns = "/*")
public class loginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求:{}",requestURI);

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        //如果不需要处理，则放行
        boolean check = check(urls, requestURI);
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //后台判断登陆状态，如已经登陆，则放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户以登陆,用户ID为{}",request.getSession().getAttribute("employee"));
            Long empID = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empID);
            filterChain.doFilter(request,response);
            return;
        }

        //移动端判断登陆状态，如已经登陆，则放行
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户以登陆,用户ID为{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }


        //如未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登陆");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls,String requestURI){
        for(String url : urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}