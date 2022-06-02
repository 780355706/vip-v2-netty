package cn.enjoyedu.rpc.server;


import cn.enjoyedu.rpc.server.rpc.RpcServerFrameReg;
import cn.enjoyedu.rpc.service.SendSms;
import cn.enjoyedu.rpc.service.impl.SendSmsImpl;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *类说明：rpc的服务端，提供服务
 */
public class SmsRpcServerReg {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                try{
                    RpcServerFrameReg serviceServer = new RpcServerFrameReg(9189);
                    serviceServer.registerSerive(SendSms.class,SendSmsImpl.class);
                    serviceServer.startService();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
