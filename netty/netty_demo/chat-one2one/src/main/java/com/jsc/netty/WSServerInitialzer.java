package com.jsc.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WSServerInitialzer extends ChannelInitializer<SocketChannel> {


    /**
     * 初始化通道，添加通道处理器
     * @param sc
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        //1 获取通道
        ChannelPipeline pipeline = sc.pipeline();
        //2 增加处理器，用于对http协议进行支持
        //2.1 增加http编解码器，因为websocket基于http
        pipeline.addLast(new HttpServerCodec());
        //2.2 增加写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //2.3 对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse，几乎在netty中的编程，都会使用到此handel
        pipeline.addLast(new HttpObjectAggregator(1024*64));

        //3 增加心跳支持，如果客户端在1分钟内没有向服务器发送读写心跳(All),主动断开通道
        //3.1 如果是读空闲or写空闲，不处理
        pipeline.addLast(new IdleStateHandler(1000, 1000, 1000));
        //3.2 自定义的空闲状态检测
        pipeline.addLast(new HeartHandler());
        //4 服务器处理websocket协议，指定客户端连接访问的路由(客户端访问路径)：/ws
        //4.1 WebSocketServerProtocolHandler用来处理websocket协议需要做的事情
        //包括：握手动作handshaking（close, ping, pong） ping + pong = 心跳
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //4.2自定义处理handel
        pipeline.addLast(new ChatHandler());
        //注：对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同，直接使用字符串传输将不能收到数据

    }
}
