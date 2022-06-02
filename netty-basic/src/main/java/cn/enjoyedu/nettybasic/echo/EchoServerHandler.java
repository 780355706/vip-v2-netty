package cn.enjoyedu.nettybasic.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 类说明：自己的业务处理
 */
@ChannelHandler.Sharable
/*不加这个注解那么在增加到childHandler时就必须new出来*/
public class EchoServerHandler extends ChannelInboundHandlerAdapter {


    /*因为在TCP中存在一个滑动窗口的概念，所以服务端缓冲区的大小是随时可变的。所以有了下面的 a 和 b */
    /* a. senyang：可以完整的读取到一个对象时会执行一次，如果传输的数据大小是1000个字节，而缓冲区的大小只有500个字节，那么就需要传输两次，而传输完成一次的时候服务端无法完整地解析
    * 数据，所以要等到下一次传输完成，两次都传输完成之后可以正确地解析一个对象，那么此时该方法执行一次*/

    /* tips: senyang：如果缓冲区的大小是1024，而要传输的数据包含2个部分，第一个是1000个字节，第2个是24个字节，那么传输完第一个数据的时候，已经存在可以被正完整确解析的数据，这时channelRead就会执行，而
    * channelReadComplete方法要等到24个字节传输完成才会执行。也就是说，channelRead执行的条件是有完整的数据可以解析，channelReadComplete执行的条件是缓冲区被填满*/
    /*客户端读到数据以后，就会执行*/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        System.out.println("Server accept"+in.toString(CharsetUtil.UTF_8));
        ctx.write(in);

    }

    /* b. senyang：缓冲区中的数据读完了就会触发一次，所以如果数据需要分两次传输，那么就会执行两次，因为缓冲区会被刷空两次*/
    /*** 服务端读取完成网络数据后的处理*/
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)//要写的数据已经写完了，现在想把写完的数据强制刷出去，又不想写多余的字段。此处提供一个空缓冲区，目的是将写缓冲区中的数据刷到对端
                .addListener(ChannelFutureListener.CLOSE);//关闭当前连接，上一步返回一个ChannelFuture，表示writeAndFlush会在未来的某个时刻完成，返回一个结果占位符；增加侦听器，做完之后关闭连接。
    }

    /*** 发生异常后的处理*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();//关闭channel，如果执行了此处，那么在EchoServer中阻塞服务器关闭的代码就可以察觉到，并执行放行操作
    }
}
