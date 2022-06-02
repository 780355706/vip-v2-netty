package cn.enjoyedu.rpc.server.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *类说明：rpc框架的服务端部分
 */
public class RpcServerFrameReg {

    private static ExecutorService executorService
            = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    //服务在本地的注册中心，主要是接口名和实现类的对照
    private static final Map<String,Class> serviceHolder
            = new HashMap<>();

    //服务的端口号
    private int port;

    public RpcServerFrameReg(int port) {
        this.port = port;
    }

    //服务注册
    public void registerSerive(Class<?> serviceInterface,Class impl) throws IOException {
        Socket socket = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;

        /*向注册中心注册服务*/
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1",9999));
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeBoolean(false);
            output.writeUTF(serviceInterface.getName());
            output.writeUTF("127.0.0.1");
            output.writeInt(port);
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            if(input.readBoolean()){
                serviceHolder.put(serviceInterface.getName(),impl);
                System.out.println(serviceInterface.getName()+"服务注册成功");
            }else{
                System.out.println(serviceInterface.getName()+"服务注册失败");
            };
        }finally {
            if (socket!=null) socket.close();
            if (output!=null) output.close();
            if (input!=null) input.close();
        }
    }
    

    //处理服务请求任务
    private static class ServerTask implements Runnable{

        private Socket client = null;

        public ServerTask(Socket client){
            this.client = client;
        }

        public void run() {

            try(ObjectInputStream inputStream =
                        new ObjectInputStream(client.getInputStream());
                ObjectOutputStream outputStream =
                        new ObjectOutputStream(client.getOutputStream())){

                //方法所在类名接口名
                String serviceName = inputStream.readUTF();
                //方法的名字
                String methodName = inputStream.readUTF();
                //方法的入参类型
                Class<?>[] parmTypes = (Class<?>[]) inputStream.readObject();
                //方法入参的值
                Object[] args = (Object[]) inputStream.readObject();

                Class serviceClass = serviceHolder.get(serviceName);
                if (serviceClass == null){
                    throw new ClassNotFoundException(serviceName+" Not Found");
                }

                Method method = serviceClass.getMethod(methodName,parmTypes);
                Object result = method.invoke(serviceClass.newInstance(),args);

                outputStream.writeObject(result);
                outputStream.flush();

            }catch(Exception e){
                e.printStackTrace();
            }finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //启动RPC服务
    public void startService() throws IOException{
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
        System.out.println("RPC server on:"+port+":运行");
        try{
            while(true){
                executorService.execute(new ServerTask(serverSocket.accept()));
            }
        }finally {
            serverSocket.close();
        }
    }

}

