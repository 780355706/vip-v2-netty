package cn.enjoyedu.rpc.vo;

import java.io.Serializable;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com 
 *
 *更多课程咨询 芊芊老师  QQ：2130753077  VIP课程咨询 依娜老师  QQ：2470523467
 *
 *类说明：用户的实体类，已实现序列化
 */
public class UserInfo implements Serializable {

    private final String name;
    private final String phone;

    public UserInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
