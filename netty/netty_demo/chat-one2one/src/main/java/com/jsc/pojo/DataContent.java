package com.jsc.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataContent implements Serializable {

    private static final long serialVersionUID = -479982523375041272L;

    private Integer action; //动作类型
    private ChatMsg chatMsg; //用户聊天内容entity
    private String extend; //扩展字段

}
