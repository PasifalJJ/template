package com.jsc.netty;

import com.alibaba.fastjson.JSON;
import com.jsc.myEnum.MsgActionEnum;
import com.jsc.pojo.ChatMsg;
import com.jsc.pojo.DataContent;
import com.jsc.service.UserService;
import com.jsc.service.impl.UserServiceImpl;
import com.jsc.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TextWebSocketFrame：在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        //获取客户端传递过来的消息
        String content = textWebSocketFrame.text();
        //获取客户端的通道
        Channel currentChannel = ctx.channel();

        //1 将客户传递过来的信息转换为pojo对象
        log.info("[DataContent]: "+content);
        DataContent dataContent = JSON.parseObject(content, DataContent.class);

        //2 根据消息类型处理业务
        //2.1 获取消息类型
        Integer action = dataContent.getAction();
        //2.2 判断消息类型，根据不同的类型来处理不同的业务
        if (action == MsgActionEnum.CONNECT.type) {
            //当opensocket第一次open的时候，初始化channel，将channel和userid关联起来
            String senderId = dataContent.getChatMsg().getSenderId();
            UserChannelRel.put(senderId, currentChannel);

            // 测试
            for (Channel c : users) {
                System.out.println(c.id().asLongText());
            }
            UserChannelRel.output();
        } else if (action == MsgActionEnum.CHAT.type) {
            // 聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText = chatMsg.getMsg();
            String receiveId = chatMsg.getReceiveId();
            String senderId = chatMsg.getSenderId();
            String id = UUID.randomUUID().toString();
            chatMsg.setId(id);
            //设置消息未签收
            chatMsg.setStatue(0);
            // 保存消息到数据库，并且标记为 未签收
            UserService userService = SpringUtil.getBean(UserServiceImpl.class);
            userService.saveMsg(chatMsg);
            //使用mangodb/redis存储聊天数据，每天定时将没有读取的数据取出，存入数据库
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            // 发送消息
            // 从全局用户Channel关系中获取接受方的channel
            Channel receiveChannel = UserChannelRel.get(receiveId);
            if (receiveChannel == null) {
                // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
            }else {
                // 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
                Channel findChannel = users.find(receiveChannel.id());
                if (findChannel != null) {
                    //用户在线
                    receiveChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContentMsg)));
                }else {
                    // TODO: 2019/7/21 用户离线，推送消息
                }
            }
        } else if (action == MsgActionEnum.SIGNED.type) {
            //2.3 消息签收类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
            String msgIdsStr = dataContent.getExtend();
            String[] msgIds = msgIdsStr.split(",");

            List<String> mgsIdList = Arrays.stream(msgIds).filter(m -> StringUtils.isNotBlank(m)).collect(Collectors.toList());
            log.info("[mgsIdList] 签收消息的id集合:"+mgsIdList);
            if (!CollectionUtils.isEmpty(mgsIdList)){
                //批量签收
                UserService userService = SpringUtil.getBean(UserServiceImpl.class);
                userService.updateMsgSigned(msgIds);
            }
        } else if (action == MsgActionEnum.KEEPALIVE.type) {
            //  2.4  心跳类型的消息
            System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
        }
    }

    /**
     * 客户端连接服务端后
     * 获取客户端的channle，并且放到ChannelGroup中去进行管理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        System.out.println("客户端被移除，channelId为：" + channelId);
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }

}
