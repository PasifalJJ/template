package com.jsc.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;

public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> {
    //定义通道组
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 通道添加处理器
     * 每当从服务端收到新的客户端连接时，客户端的频道存入频道组列表中，
     * 并通知列表中的其他客户端频道
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //获取当前连接的通道
        Channel incoming = ctx.channel();
        //将该通道的信息发送到其他所有通道，将上线消息发给其他联系人
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + "加入\n");
        }
        //将该通道加入通道组
        channels.add(ctx.channel());
    }

    /**
     * 通道移除处理器
     * 每当从服务端收到客户端断开时，客户端的频道移除频道组列表中，
     * 并通知列表中的其他客户端频道
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //获取当前连接的通道
        Channel incoming = ctx.channel();
        //将该通道的信息发送到其他所有通道，将下线消息发给其他联系人
        for (Channel channel : channels) {
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + "离开\n");
        }
        //从通道组中移除该通道
        channels.remove(ctx.channel());
    }

    /**
     * 处理通道信息,处理用户发送的消息
     * 每当从服务端读到客户端写入信息时，将信息转发给其他客户端的频道。
     * 其中如果你使用的是Netty 5.x版本时，需要把channelRead0 （）重命名为messageReceived（）
     * @param ctx
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        //获取用户发送消息用户的通道
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if (incoming != channel) {
                //显示自己发送消息，别人看的结果
                channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + s + "\n");
            }else {
                //显示自己发送消息，自己看的结果
                channel.writeAndFlush("[you]" + s + "\n");
            }
        }
    }

    /**
     * 服务端监听到客户端活动
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"在线");
    }

    /**
     * 服务端监听到客户端不活动
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"掉线");
    }

    /**
     * 事件处理方法是当出现Throwable对象才会被调用，
     * 即当Netty由于IO错误或者处理器在处理事件时抛出的异常时。
     * 在大部分情况下，捕获的异常应该被记录下来并且把关联的channel to关闭掉。
     * 然而这个方法的处理方式会在遇到不同异常的情况下有不同的实现，
     * 比如你可能想在关闭连接之前发送一个错误码的响应消息。
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
