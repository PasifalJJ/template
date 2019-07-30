package com.jsc.security.provider;

import com.jsc.pojo.jwt.UserInfo;
import com.jsc.security.service.SelfUserDetailsService;
import com.jsc.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Set;

@Component
@Slf4j
public class SelfAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SelfUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.userToken}")
    private String USER_JWT_TOKEN;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        if (authentication1 == null) {
            //获取httpServletRequest
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            HttpServletResponse response = servletRequestAttributes.getResponse();

            // 从数据库中查询
            String username = authentication.getPrincipal().toString(); //获取表单的用户名
            String password = authentication.getCredentials().toString(); //获取表单的密码

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String passwordDB = userDetails.getPassword();
            Set<SimpleGrantedAuthority> authorities = (Set<SimpleGrantedAuthority>) userDetails.getAuthorities();
            //将密码加密，与数据库中的密码进行比较
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean f = bCryptPasswordEncoder.matches(password, passwordDB);
            //判断密码是否相同
            if (!f) {
                throw new BadCredentialsException("用户名密码不正确，请重新登陆！");
            }
            authentication = new UsernamePasswordAuthenticationToken(username, "", userDetails.getAuthorities());
            //用户登录成功，生成token令牌，存入cookie
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(username);
            userInfo.setAuthorities(authorities);
            try {
                String jwtToken = JwtUtils.generateToken(userInfo, secret, 30);
                response.addCookie(new Cookie(USER_JWT_TOKEN,jwtToken));
            } catch (Exception e) {
                log.error("[令牌异常] 令牌生成异常 用户信息为"+userInfo);
            }
        }
        return authentication;
    }

    /**
     * 用来控制provider是否启用，可用来控制多个provider
     *
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
