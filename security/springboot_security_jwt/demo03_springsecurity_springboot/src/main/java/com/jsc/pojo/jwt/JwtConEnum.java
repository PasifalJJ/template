package com.jsc.pojo.jwt;


public enum  JwtConEnum {
    JWT_KEY_ID("id"),
    JWT_KEY_USERNAME("username"),
    JWT_KEY_AUTHORITY("authority")
    ;

    private String key;

    JwtConEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
