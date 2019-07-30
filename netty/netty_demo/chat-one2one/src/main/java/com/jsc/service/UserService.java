package com.jsc.service;

import com.jsc.pojo.ChatMsg;

public interface UserService {

    void saveMsg(ChatMsg chatMsg);

    void updateMsgSigned(String[] msgIds);
}
