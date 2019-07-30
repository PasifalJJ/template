package com.jsc.security.filter;

import com.jsc.pojo.jwt.UserInfo;
import com.jsc.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class SelfAuthenticationProcessingFilter extends OncePerRequestFilter {

    @Value("${jwt.userToken}")
    private String USER_JWT_TOKEN;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Authentication authentication = new UsernamePasswordAuthenticationToken();
        //1 判断是否有token
        if (authentication == null) {
            String token = request.getHeader(USER_JWT_TOKEN);
            log.error("cookie的值为{}",token);
            if (StringUtils.isNotBlank(token)) {
                //1.1 有token,使用jwt进行解密，获取其加密信息
                UserInfo userInfo = null;
                try {
                    userInfo = JwtUtils.getInfoFromToken(token, secret);
                } catch (Exception e) {
                    log.error("[解析异常] token解析异常 token: " + token);
                }

                //令牌解析成功
                if (userInfo != null) {
                    authentication = new UsernamePasswordAuthenticationToken(userInfo.getUsername(), "", userInfo.getAuthorities());
                }
                //失败
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }
}
