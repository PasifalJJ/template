package com.jsc.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WSSerrver {

    public static void main(String[] args) throws Exception {

        //定义主从两个线程组
        EventLoopGroup mainGroup=new NioEventLoopGroup();
        EventLoopGroup subGroup=new NioEventLoopGroup();

        try {
            //创建NioServer的引导启动类
            ServerBootstrap server=new ServerBootstrap();
            //添加主从线程组
            server.group(mainGroup, subGroup)
                    .channel(NioServerSocketChannel.class)  // TODO: 2019/7/13 添加协议？？？
                    .childHandler(new WSServerInitialzer());    //添加初始化处理器

            ChannelFuture future = server.bind(8088).sync();    //同步绑定端口，返回future对象

            future.channel().closeFuture().sync();  //关闭端口
        } finally {
            mainGroup.shutdownGracefully(); //关闭主线程组
            subGroup.shutdownGracefully();
        }
    }
}
