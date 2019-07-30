package jsc.webSocketServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * SimpleChatServerInitializer用来增加多个的处理类到ChannelPipeline上，
 * 包括编码，解码，SimpleChatServerHandler等。
 */
public class SimpleChatServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //webscoket 基于http协议，所以要添加http编解码器
        pipeline.addLast(new HttpServerCodec());
        //对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        //几乎在netty中的编程，都会使用此handel
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        //=======================上述用于支持http协议=============================

        /*websocket 服务器处理的协议，用于指定给客户端连接访问的路由 :/ws
         * 本handel会帮你处理一些繁重复杂的事
         * 会帮你处理握手动作，：handshaking(clos.ping.pong) ping+pong=心跳
         * 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
         * */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //添加消息处理器
        pipeline.addLast("handel", new SimpleChatServerHandler());
        System.out.println("simpleChatClient:"+ch.remoteAddress()+"连接上");
    }
}
