package com.jsc.pojo.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Set;

/**
 * 载荷:UserInfo
 * 加密信息内容
 */
public class UserInfo implements Serializable {

    private Long id;

    private String username;

    private String message;

    private Set<SimpleGrantedAuthority> authorities;

    public UserInfo() {
    }

    public UserInfo(Long id, String username, String message, Set<SimpleGrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.message = message;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
