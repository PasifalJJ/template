package com.jsc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.EventExecutorGroup;

public class HeartHandler extends ChannelInboundHandlerAdapter {

    /**
     * 用于检测channel的心跳handler，继承ChannelInboundHandlerAdapter，从而不需要实现channelRead0方法
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //判断evt是否是IdleStatement（用于触发用户事件，包含 读空闲/写空闲/读写空闲 ）
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("进入读空闲");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                System.out.println("进入写空闲");
            } else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("channel关闭前，users的数量为：" + ChatHandler.users.size());
                // 获得当前的channel
                Channel channel = ctx.channel();
                //关闭当前的channel
                channel.close();
                System.out.println("channel关闭后，users的数量为：" + ChatHandler.users.size());
            }
        }
    }
}
