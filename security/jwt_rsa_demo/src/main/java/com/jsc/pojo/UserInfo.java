package com.jsc.pojo;

/**
 * 载荷:UserInfo
 * 加密信息内容
 */
public class UserInfo {

    private Long id;

    private String username;

    private String message;

    public UserInfo() {
    }

    public UserInfo(Long id, String username, String message) {
        this.id = id;
        this.username = username;
        this.message = message;
    }

    public Long getId() {
        return this.id;
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

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
