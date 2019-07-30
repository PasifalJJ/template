package com.jsc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class SimpleChatServer {

    private int port;

    public SimpleChatServer(int port){
        this.port = port;
    }

    public void run() throws Exception{
        /*.NioEventLoopGroup是用来处理I/O操作的多线程事件循环器，
           Netty 提供了许多不同的EventLoopGroup的实现用来处理不同的传输。
           在这个例子中我们实现了一个服务端的应用，因此会有2个 NioEventLoopGroup 会被使用。
           第一个经常被叫做‘boss’，用来接收进来的连接。第二个经常被叫做‘worker’，
           用来处理已经被接收的连接，一旦‘boss’接收到连接，就会把连接信息注册到‘worker’上。
           如何知道多少个线程已经被使用，如何映射到已经创建的 Channel上都需要依赖于
           EventLoopGroup 的实现，并且可以通过构造函数来配置他们的关系。*/
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        /**
         * ServerBootstrap是一个启动 NIO 服务的辅助启动类。
         * 你可以在这个服务中直接使用 Channel，但是这会是一个复杂的处理过程，
         * 在很多情况下你并不需要这样做
         */
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                /*这里我们指定使用NioServerSocketChannel类来举例说明一个新的 Channel
                如何接收进来的连接。*/
                .channel(NioServerSocketChannel.class)
                /*这里的事件处理类经常会被用来处理一个最近的已经接收的 Channel，
                SimpleChatServerInitializer类主要用来初始化我们的channel，
                在该类中可以给channel.pipeline添加拦截器，用来处理传递的信息。*/
                .childHandler(new SimpleChatServerInitializer())
                /*你可以设置这里指定的 Channel 实现的配置参数。
                我们正在写一个TCP/IP 的服务端，因此我们被允许设置 socket 的参数选项比如tcpNoDelay 和 keepAlive。
                请参考ChannelOption和详细的ChannelConfig实现的接口文档以此可以对ChannelOption 的有一个大概的认识。*/
                .option(ChannelOption.SO_BACKLOG, 128)
                /*option() 是提供给NioServerSocketChannel用来接收进来的连接。
                childOption() 是提供给由父管道ServerChannel接收到的连接，在这个例子中也是 NioServerSocketChannel。*/
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        System.out.println("simpleChatServer 启动了");

        //绑定端口，开始接收进来的连接
        ChannelFuture f = b.bind(port).sync();

        //获得ChannelFuture对象后关闭连接，后续返回的值会传递给ChannelFuture
        f.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length>0){
            port = Integer.parseInt(args[0]);
        }else {
            port = 8080;
        }
        //启动服务
        new SimpleChatServer(port).run();
    }
}
