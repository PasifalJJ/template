package com.jsc.netty.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

public class ChatHandel extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel，固定写法
    private static ChannelGroup clients=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 当客户端连接服务端之后,运行此方法(即客户端打开连接之后)
     * 获取客户端的channel，并且放到ChannelGroup中进行管理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    /**
     * 用户离开客户端，运行此方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当出发handelRemoved后，ChannelGroup会自动移除对应客户端的channel，所以下面的方法可以注释掉
        //clients.remove(ctx.channel());
        System.out.println("客户端断开，channle对应的长id为："+ctx.channel().id().asLongText());
        System.out.println("客户端断开，channle对应的短id为："+ctx.channel().id().asShortText());
    }

    /**
     * 处理消息的handel
     * @param channelHandlerContext
     * @param textWebSocketFrame    在netty中，是用于为webSocket专门处理文本的对象，frame是消息的载体
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        //获取客户传递的消息
        String content = textWebSocketFrame.text();
        System.out.println("接受到的数据："+content);

        for (Channel channel : clients){
            channel.writeAndFlush(new TextWebSocketFrame("[服务器在]"+ LocalDateTime.now()+
                    "接受到消息，消息为："+content));
        }

        /*下面的方法和上面的for循环方法一致
        clients.writeAndFlush(new TextWebSocketFrame("[服务器在]"+ LocalDateTime.now()+
                "接受到消息，消息为："+content));*/
    }
}
