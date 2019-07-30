package com.jsc.dao;

import com.jsc.pojo.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMsgRepository extends JpaRepository<ChatMsg,String> {

}
