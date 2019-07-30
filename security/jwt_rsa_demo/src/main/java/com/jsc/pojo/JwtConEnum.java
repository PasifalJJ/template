package com.jsc.pojo;


public enum  JwtConEnum {
    JWT_KEY_ID("id"),
    JWT_KEY_USERNAME("username")
    ;

    private String key;

    JwtConEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
