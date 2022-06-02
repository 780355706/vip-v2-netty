package cn.enjoyedu.rpc.client.rpc;

import cn.enjoyedu.rpc.vo.RegisterServiceVo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Set;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *类说明：rpc框架的客户端代理部分
 */
public class RpcClientFrameReg {

    //远程代理对象
    public static <T> T getRemoteProxyObj(final Class<?> serviceInterface){
        final InetSocketAddress addr
                = new InetSocketAddress("127.0.0.1",9999);
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface}
        ,new DynProxy(serviceInterface,addr));
    }


    //动态代理类
    private static class DynProxy implements InvocationHandler {

        private final Class<?> serviceInterface;
        private final InetSocketAddress addr;
        private RegisterServiceVo[] serviceArray;/*远程服务在本地的缓存列表*/

        public DynProxy(Class<?> serviceInterface, InetSocketAddress addr) {
            this.serviceInterface = serviceInterface;
            this.addr = addr;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            Socket socket = null;
            ObjectOutputStream output = null;
            ObjectInputStream input = null;

            /*检索远程服务并填充本地的缓存列表*/
            if(serviceArray==null){
                try{
                    socket = new Socket();
                    socket.connect(addr);
                    output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeBoolean(true);
                    output.writeUTF(serviceInterface.getName());
                    output.flush();
                    input = new ObjectInputStream(socket.getInputStream());
                    Set<RegisterServiceVo> result = (Set<RegisterServiceVo>)input.readObject();
                    serviceArray = new RegisterServiceVo[result.size()];
                    result.toArray(serviceArray);
                }finally {
                    if (socket!=null) socket.close();
                    if (output!=null) output.close();
                    if (input!=null) input.close();
                }

            }

            /*本地的缓存列表取得一个远端服务器的地址端口
            * 可以考虑使用更复杂的算法，以实现服务器的负载均衡
            * 这里简单化处理，用随机数挑选*/
            Random r  = new Random();
            int index = r.nextInt(serviceArray.length);
            InetSocketAddress serviceAddr
                    = new InetSocketAddress(serviceArray[index].getHost(),serviceArray[index].getPort());

            try{
                socket = new Socket();
                socket.connect(serviceAddr);

                output = new ObjectOutputStream(socket.getOutputStream());
                output.writeUTF(serviceInterface.getName());//方法所在的类
                output.writeUTF(method.getName());//方法的名
                output.writeObject(method.getParameterTypes());//方法的入参类型
                output.writeObject(args);
                output.flush();

                input = new ObjectInputStream(socket.getInputStream());
                return input.readObject();

            }finally{
                if (socket!=null) socket.close();
                if (output!=null) output.close();
                if (input!=null) input.close();
            }

        }
    }

}
