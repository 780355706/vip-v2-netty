package cn.enjoyedu.nettybasic.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 作者：Mark/Maoke
 * 创建日期：2018/08/25
 * 类说明：
 */
//@ChannelHandler.Sharable
public class EchoServer  {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 9999;
        EchoServer echoServer = new EchoServer(port);
        System.out.println("服务器即将启动");
        echoServer.start();
        System.out.println("服务器关闭");
    }

    /**
     * senyang：一个问题：下面的代码中，传入了一个serverHandler，表示对客户端的所有连接都使用这个处理器进行处理，那么在EchoClient启动两次的时候，Netty抛出了一个异常。
     * 原因是此处写法不是共享的。
     * 解决：
     *      1. 在该类上增加注解@ChannelHandler.Sharable（需要注意线程安全性，但这是一个无状态的类，所以一定是线程安全的）
     *      2. addLast方法每次都new一个EchoServerHandler出来（new耗空间耗时间）
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        /*线程组*/
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            /*服务端启动必须*/
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)/*将线程组传入*/
                    .channel(NioServerSocketChannel.class)/*指定使用NIO进行网络传输*/
//                    .channel(EpollServerSocketChannel.class)/*如果此处使用Epoll那么只能在Linux上面运行，因为Netty使用了JNI进行驱动的，对jdk中epoll进行了增强，epoll比nio性能高，因为是jdk提供的*/
                    .localAddress(new InetSocketAddress(port))/*指定服务器监听端口*/
                    /*服务端每接收到一个连接请求，就会新启一个socket通信，也就是channel，
                    所以下面这段代码的作用就是为这个子channel增加handle*/
                    /*senyang：使用childHandler而不使用handler的原因是ServerSocket只用来出来连接，和对端进行网络通讯的还是一个个具体的Socket，当接收到客户端
                    * 的请求时，ServerSocket会new出一个Socket来处理客户端的具体请求，所以要加到childHandler上面*/
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            /*添加到该子channel的pipeline的尾部*/
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync();/*异步绑定到服务器，sync()会阻塞直到完成*/
            f.channel().closeFuture().sync();/*阻塞直到服务器的channel关闭，防止应用程序本身退出，如果是运行在web服务器上面，web服务器程序是不会退出的，就不需要写这行代码*/

        } finally {
            group.shutdownGracefully().sync();/*优雅关闭线程组，其中.sync表示这行代码执行完成之后才能继续后面的工作*/
        }

    }


}
