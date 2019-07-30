package com.jsc.security.provider;

import com.jsc.security.service.SelfUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SelfAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SelfUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString(); //获取表单的用户名
        String password = authentication.getCredentials().toString(); //获取表单的密码

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String passwordDB = userDetails.getPassword();
        //将密码加密，与数据库中的密码进行比较
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean f = bCryptPasswordEncoder.matches(password, passwordDB);
        //判断密码是否相同
        if (!f){
            throw new BadCredentialsException("用户名密码不正确，请重新登陆！");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
