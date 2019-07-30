package com.jsc.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class UserChannelRel {

    //静态变量，所有使用这个对象的共享这个变量
    private static Map<String, Channel> manager = new HashMap<>();

    public static void put(String senderId, Channel channel) {
        manager.put(senderId, channel);
    }

    public static Channel get(String senderId) {
        return manager.get(senderId);
    }

    public static void output(){
        for (Map.Entry<String, Channel> entry : manager.entrySet()) {
            System.out.println("UserId: " + entry.getKey()
                    + ", ChannelId: " + entry.getValue().id().asLongText());
        }
    }
}
