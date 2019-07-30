package com.jsc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户没有登录访问无权限资源时，用调用此处理器解决异常，我们这里处理将数据返回给前端
 * AuthenticationEntryPoint 用来解决匿名用户访问无权限资源时的异常,匿名及没登录，匿名用户访问无权限资源时会调用此处理器
 * AccessDeineHandler 用来解决认证过的用户访问无权限资源时的异常
 * AuthenticationFailureHandler 用户登录失败时返回给前端的数据
 * AuthenticationSuccessHandler 用户登录成功时给前端返回的数据
 * AccessDeniedHandler 用户无权访问异常处理器
 */
@Component
public class AjaxAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        AjaxResponseBody responseBody = new AjaxResponseBody();

        responseBody.setStatus("400");
        responseBody.setMsg("Need Authorities!");

        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
    }
}
