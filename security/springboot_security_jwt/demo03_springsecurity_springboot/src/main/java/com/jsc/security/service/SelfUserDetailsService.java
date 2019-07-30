package com.jsc.security.service;

import com.jsc.security.entity.SelfUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 根据username从数据库获取信息
 * 最后返回的 userDetails 会在 AuthenticationProvider,比较password是否正确
 */
@Component
public class SelfUserDetailsService implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        SelfUserDetails userDetails = new SelfUserDetails();
        //数据库中的用户名
        userDetails.setUsername(s);
        //数据库中经过加密的密码,此处避免从数据库查询，直接进行模拟
        userDetails.setPassword(new BCryptPasswordEncoder().encode("123"));
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        GrantedAuthority authority2 = new SimpleGrantedAuthority("ROLE_ADMIN");
        Set<GrantedAuthority> set = new HashSet<>();
        set.add(authority);
        set.add(authority2);
        userDetails.setAuthorities(set);
        //此处返回的结构最后会在AuthenticationProvider进行处理，比较password是否正确
        return userDetails;
    }
}
