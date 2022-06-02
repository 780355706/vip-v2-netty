package cn.enjoyedu.proxy;

/**
 * 类说明：前台招待
 */
public class Receptionist implements IGetServant {
    @Override
    public void choice(String desc) {
        System.out.println("已经为您选好了........【"+desc+"】");
    }
}
