package cn.enjoyedu.nio.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 类说明：nio通信客户端处理器
 */
public class NioClientHandle implements Runnable{
    private String host;
    private int port;
    private volatile boolean started;
    private Selector selector;
    private SocketChannel socketChannel;


    public NioClientHandle(String ip, int port) {
        this.host = ip;
        this.port = port;
        try {
            /*创建选择器*/
            this.selector = Selector.open();
            /*打开监听通道*/
            socketChannel = SocketChannel.open();
            /*如果为 true，则此通道将被置于阻塞模式；
            * 如果为 false，则此通道将被置于非阻塞模式
            * 缺省为true*/
            socketChannel.configureBlocking(false);
            started = true;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public void stop(){
        started = false;
    }


    @Override
    public void run() {
        //连接服务器
        try {
            /*在bio里面，连接服务器的方法是阻塞式方法，*/
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        /*创建连接成功后，循环遍历selector，监听事件的发生*/
        while(started){
            try {
                /*阻塞方法,当至少一个注册的事件发生的时候就会继续*/
                selector.select();
                /*获取当前有哪些事件可以使用，selectionKey表示每一个Channel在选择器上注册的一个标识，每注册一个创建一个SelectionKey*/
                Set<SelectionKey> keys = selector.selectedKeys();
                /*转换为迭代器，依次处理这些事件*/
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key = null;
                while(it.hasNext()){
                    key = it.next();
                    /*我们必须首先将处理过的 SelectionKey 从选定的键集合中删除。
                    如果我们没有删除处理过的键，那么它仍然会在事件集合中以一个激活
                    的键出现，这会导致我们尝试再次处理它。*/
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if(key!=null){
                            key.cancel();//在select上面注册的事件取消，取消之后调用key.isValid()返回false
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        if(selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /*具体的事件处理方法：处理相关的网络读写，需要关注的只有三件事：读、写、连接*/
    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){//因为上一步有可能调用cancel取消注册该SelectionKey，所以需要校验有效性
            /*获得关心当前事件的channel*/
            SocketChannel sc =(SocketChannel)key.channel();
            /*处理连接就绪事件
            * 但是三次握手未必就成功了，所以需要等待握手完成和判断握手是否成功*/
            if(key.isConnectable()){//判断是否是连接就绪事件
                /*finishConnect的主要作用就是确认通道连接已建立，
                方便后续IO操作（读写）不会因连接没建立而
                导致NotYetConnectedException异常。*/
                if(sc.finishConnect()){//这是一个阻塞方法，三次握手完成之后这个方法才会返回，如果返回false，表示三次握手没有成功，那么就需要重新new出一个SocketChannel，重建连接服务器，这里做简单化处理，直接退出应用程序
                    /*连接既然已经建立，当然就需要注册读事件，
                    写事件一般是不需要注册的。*/
                    socketChannel.register(selector,SelectionKey.OP_READ);//在同一个Channel上调用两次register，后调用的会覆盖前面注册的事件，此处将会覆盖前面注册的连接事件，不过没关系，如果已经连接成功之后，就不需要再关注连接事件了
                }else System.exit(-1);
            }

            /*处理读事件，也就是当前有数据可读*/
            if(key.isReadable()){
                /*创建ByteBuffer，并开辟一个1k的缓冲区*/
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                /*将通道的数据读取到缓冲区，read方法返回读取到的字节数*/
                int readBytes = sc.read(buffer);//对于buffer来讲，此处实际是写，即写到buffer内，然后进行下面的flip转换，再将数据从buffer读取出来，那么此处的返回值也可以说是写出的字节数
                if(readBytes>0){
                    buffer.flip();//切换到读模式，转换position和limit（PPT24页）
                    byte[] bytes = new byte[buffer.remaining()];//remaining(): return limit - position;返回 limit 和 position 之间相对位置差，返回的数字就是写过来的数据字节大小
                    buffer.get(bytes);//将buffer中的数据读取到bytes数组中
                    String result = new String(bytes,"UTF-8");
                    System.out.println("客户端收到消息："+result);
                }
                /*链路已经关闭，释放资源（发生了四次挥手）*/
                else if(readBytes<0){
                    key.cancel();
                    sc.close();
                }

            }
        }
    }

    /*进行连接*/
    private void doConnect() throws IOException {
        /*如果此通道处于非阻塞模式，则调用此方法将启动非阻塞连接操作。
        如果连接马上建立成功，则此方法返回true。
        否则，此方法返回false，说明目前正处于三次握手的阶段
        因此我们必须关注连接就绪事件，
        并通过调用finishConnect方法完成连接操作。*/
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            /*连接成功，关注读事件*/
            socketChannel.register(selector,SelectionKey.OP_READ);
        }
        else{
            /*告诉select，应该关注连接事件，如果连接完成了，通知当前连接通道*/
            /*senyang：把该通道注册给选择器，并指定注册事件，这样选择器就可以选择处理该通道上的指定事件。该方法会返回注册的选择器键*/
            socketChannel.register(selector,SelectionKey.OP_CONNECT);//问题，注册多个怎么处理？
        }
    }

    /*写数据对外暴露的API*/
    public void sendMsg(String msg) throws IOException {
        /*向SocketChannel中写数据，即将数据写到缓冲区中*/
        doWrite(socketChannel,msg);
    }

    private void doWrite(SocketChannel sc,String request) throws IOException {
        /*但是与channel打交道的只能是buffer，NIO是面向缓冲区的，而不是面向流的，所以还需要包装成buffer*/
        byte[] bytes = request.getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);//写往buffer
        writeBuffer.flip();//切换成“读”（实际工作中用的比较少，因为直接使用netty）
        sc.write(writeBuffer);//读向channel（虽然是write，但对于channel来讲是读，对于buffer来讲是写）
        //问题：没写完怎么处理？
    }
}
