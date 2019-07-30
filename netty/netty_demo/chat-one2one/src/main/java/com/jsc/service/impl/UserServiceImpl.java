package com.jsc.service.impl;

import com.jsc.dao.ChatMsgRepository;
import com.jsc.pojo.ChatMsg;
import com.jsc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ChatMsgRepository chatMsgRepository;

    @Override
    public void saveMsg(ChatMsg chatMsg) {
       chatMsgRepository.save(chatMsg);
    }

    @Override
    public void updateMsgSigned(String[] ids) {
        for (String id : ids) {
            ChatMsg chatMsg = chatMsgRepository.findById(id).get();
            chatMsg.setStatue(1);
            chatMsgRepository.saveAndFlush(chatMsg);
        }
    }
}
