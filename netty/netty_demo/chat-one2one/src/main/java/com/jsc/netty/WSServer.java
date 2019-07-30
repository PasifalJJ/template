package com.jsc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WSServer {
    //单例的两种写法
    //1、静态代码块

    //2、静态内部类，推荐使用静态内部类，可以完全避免高并发造成的问题
    private static class SingleWSServer {
        private static final WSServer instance = new WSServer();
    }

    public static WSServer getInstance() {
        return SingleWSServer.instance;
    }

    private EventLoopGroup mainGroup;   //处理IO的多线程时事件循环器
    private EventLoopGroup subGroup;   //处理IO的多线程时事件循环器
    private ServerBootstrap bootstrap;  //NIO服务的辅助启动类
    private ChannelFuture future;       //通过返回对象占位符，最终消息会存入其中

    // WSServer构造方法
    public WSServer() {
        mainGroup = new NioEventLoopGroup(); //指定为NIO事件循环器
        subGroup = new NioEventLoopGroup(); //指定为NIO事件循环器
        bootstrap = new ServerBootstrap();
        bootstrap.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class) //用来监听新进来的TCP/http连接的通道，就像标准IO中的ServerSocket一样
                .childHandler(new WSServerInitialzer()); //添加初始化处理器，用来对传入的通道进行初始化处理
    }

    //启动服务
    public void start(int port) {

        this.future = bootstrap.bind(port);
        System.err.println("netty websocket server 启动完毕");
    }


}
