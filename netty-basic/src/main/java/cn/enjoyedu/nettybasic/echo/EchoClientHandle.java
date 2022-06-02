package cn.enjoyedu.nettybasic.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 类说明：
 */
public class EchoClientHandle extends SimpleChannelInboundHandler<ByteBuf> {

    /*客户端读到数据以后，就会执行*/
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
            throws Exception {
        System.out.println("client acccept:"+msg.toString(CharsetUtil.UTF_8));
    }

    /*连接建立以后*/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer(
                "Hello Netty",CharsetUtil.UTF_8));//写往服务器
        //ctx.fireChannelActive();//如果有数据要在后面的Handler继续处理，那么此处必须手动传递，否则该数据在下一个Handler获取不到（设计模式中的“责任链”模式）
    }

    /*发生异常时执行*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();

        ctx.close();
    }

    /**/
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
