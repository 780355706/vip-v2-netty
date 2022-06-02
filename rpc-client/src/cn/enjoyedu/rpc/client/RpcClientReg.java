package cn.enjoyedu.rpc.client;


import cn.enjoyedu.rpc.client.rpc.RpcClientFrameReg;
import cn.enjoyedu.rpc.service.SendSms;
import cn.enjoyedu.rpc.service.StockService;
import cn.enjoyedu.rpc.vo.UserInfo;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *类说明：rpc的客户端，调用远端服务
 */
public class RpcClientReg {
    public static void main(String[] args) {

        UserInfo userInfo
                = new UserInfo("Mark","Mark@xiangxue.com");

        SendSms sendSms = RpcClientFrameReg.getRemoteProxyObj(SendSms.class);
        System.out.println("Send mail: "+ sendSms.sendMail(userInfo));

        StockService stockService
                = RpcClientFrameReg.getRemoteProxyObj(StockService.class);
        stockService.addStock("A001",1000);
        stockService.deduceStock("B002",50);


    }
}
