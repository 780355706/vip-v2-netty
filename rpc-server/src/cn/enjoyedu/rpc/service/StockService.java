package cn.enjoyedu.rpc.service;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *更多课程咨询 依娜老师  QQ：2470523467  VIP课程咨询 依娜老师  QQ：2470523467
 *
 *类说明：变动库存服务接口
 */
public interface StockService {
    void addStock(String goodsId, int addAmout);
    void deduceStock(String goodsId, int deduceAmout);
}
