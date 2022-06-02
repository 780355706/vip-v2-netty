package cn.enjoyedu.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *类说明：代理类
 */
public class JamesDyna implements InvocationHandler {

    private Object receptionist;

    public JamesDyna(Object receptionist) {
        this.receptionist = receptionist;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        //把网络通信相关的内容隐藏到代理对象的内部
        System.out.println("James find recp....");
        Object result = method.invoke(receptionist,args);
        System.out.println("half price...");
        return result;
    }


}
