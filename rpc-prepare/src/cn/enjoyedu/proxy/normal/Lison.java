package cn.enjoyedu.proxy.normal;

import cn.enjoyedu.proxy.IGetServant;
import cn.enjoyedu.proxy.Receptionist;

/**
 */
public class Lison {

    public static void main(String[] args) {
        IGetServant getServant = new Receptionist();
        getServant.choice("御姐范风格");

//        IGetServant james = new James();
//        james.choice("喜欢的样子");
    }

}
