package cn.enjoyedu.bio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com
 *
 *类说明：Bio通信的服务端
 */
public class Server {

    public static void main(String[] args) throws IOException {
        /*服务器必备*/
        ServerSocket serverSocket = new ServerSocket();
        /*绑定监听端口*/
        serverSocket.bind(new InetSocketAddress(10001));
        System.out.println("Server start.......");

        while(true){
            /**
             * 虽然此处使用while(true)，但并不是采用轮询的方式，因为accept()是阻塞式方法，监听不到会等待；
             * accept()方法会不断的监听10001端口，检查是否有客户端要连接我
             */
           new Thread(new ServerTask(serverSocket.accept())).start();
        }
    }

    private static class ServerTask implements Runnable{

        private Socket socket = null;//监听到的socket

        public ServerTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            /*拿和客户端通讯的输入输出流*/
            try(ObjectInputStream inputStream
                        = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream
             = new ObjectOutputStream(socket.getOutputStream())){

                /*服务器的输入*/
                String userName = inputStream.readUTF();//读取文本
                System.out.println("Accept clinet message:"+userName);

                outputStream.writeUTF("Hello,"+userName);//写到输出缓冲区
                outputStream.flush();//刷到对端


            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
