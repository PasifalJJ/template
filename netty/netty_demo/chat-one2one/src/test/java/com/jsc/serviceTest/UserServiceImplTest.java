package com.jsc.serviceTest;

import com.jsc.ApplicationTest;
import com.jsc.dao.ChatMsgRepository;
import com.jsc.pojo.ChatMsg;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImplTest extends ApplicationTest {

    @Autowired
    private ChatMsgRepository chatMsgRepository;

    @Test
    public void saveMsgTest() {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setMsg("hello !~~~");
        chatMsg.setSenderId("2333");
        chatMsg.setReceiveId("7777");
        chatMsg.setStatue(0);
        chatMsgRepository.save(chatMsg);
    }

    @Test
    public void updateMsgSignedTest() {
        String[] ids = {"40289bd56c1279b7016c1279c22f0000"};
        for (String id : ids) {
            ChatMsg chatMsg = chatMsgRepository.findById(id).get();
            chatMsg.setStatue(1);
            chatMsgRepository.save(chatMsg);
        }
    }

}
