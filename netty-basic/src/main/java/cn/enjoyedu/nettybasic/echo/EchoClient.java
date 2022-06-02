package cn.enjoyedu.nettybasic.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 作者：Mark/Maoke
 * 创建日期：2018/08/26
 * 类说明：Netty实现的客户端
 */
public class EchoClient {

    private final int port;
    private final String host;

    public EchoClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() throws InterruptedException {
        /*线程组，专门用来处理网络中发生的各种事情*/
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            /*客户端启动必备*/
            Bootstrap b = new Bootstrap();
            b.group(group)/*把线程组传入*/
                    /*指定使用NIO进行网络传输*/
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host,port))
                    .handler(new EchoClientHandle());
            /*连接到远程节点，阻塞直到连接完成*/
            /*senyang：sync表示同步，connect方法是异步的，在nio中，调用connect()方法之后，他会直接返回，不会等到连接完成，所以这里要调用sync()方法*/
            ChannelFuture f = b.connect().sync();
            /*阻塞程序，直到Channel发生了关闭*/
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new EchoClient(9999,"127.0.0.1").start();
    }
}
