package cn.enjoyedu.rpc.client;


import cn.enjoyedu.rpc.service.SendSms;
import cn.enjoyedu.rpc.service.StockService;
import cn.enjoyedu.rpc.vo.UserInfo;
import cn.enjoyedu.rpc.client.rpc.RpcClientFrame;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *类说明：rpc的客户端，调用远端服务
 */
public class RpcClient {
    public static void main(String[] args) {

        UserInfo userInfo
                = new UserInfo("Mark","Mark@xiangxue.com");

        SendSms sendSms = RpcClientFrame.getRemoteProxyObj(SendSms.class,
                "127.0.0.1",9189);
        System.out.println("Send mail: "+ sendSms.sendMail(userInfo));

        StockService stockService
                = RpcClientFrame.getRemoteProxyObj(StockService.class,
                "127.0.0.1",9190);
        stockService.addStock("A001",1000);
        stockService.deduceStock("B002",50);

//        StockService stockService = new StockServiceImpl();
//        stockService.addStock("A001",1000);
//        stockService.deduceStock("B002",50);

    }
}
