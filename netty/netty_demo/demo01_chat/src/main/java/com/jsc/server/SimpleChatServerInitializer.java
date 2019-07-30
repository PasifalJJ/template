package com.jsc.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * SimpleChatServerInitializer用来增加多个的处理类到ChannelPipeline上，
 * 包括编码，解码，SimpleChatServerHandler等。
 */
public class SimpleChatServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("frame", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        //添加消息处理器
        pipeline.addLast("hander", new SimpleChatServerHandler());
        System.out.println("simpleChatClient:"+ch.remoteAddress()+"连接上");
    }
}
