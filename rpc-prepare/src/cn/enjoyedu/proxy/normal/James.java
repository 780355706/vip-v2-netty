package cn.enjoyedu.proxy.normal;


import cn.enjoyedu.proxy.IGetServant;
import cn.enjoyedu.proxy.Receptionist;

/**
 *类说明：James，实现了选择服务接口，准备代理lison
 */
public class James implements IGetServant {

    private IGetServant receptionist = new Receptionist();

    @Override
    public void choice(String desc) {
        System.out.println("James找到前台小姐姐.....");
        //do other something....
        receptionist.choice(desc);//前台小姐姐进行处理
        //do other something....,自己选一个
        System.out.println("James:能不能第二个半价？我喜欢小家碧玉，初恋的感觉");
    }
}
