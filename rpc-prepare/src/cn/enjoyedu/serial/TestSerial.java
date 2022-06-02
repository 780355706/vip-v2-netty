package cn.enjoyedu.serial;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 类说明：测试JDK的序列化机制
 */
public class TestSerial {
    public static void main(String[] args) {
        // 创建一个User对象
//        UserInfo user = new UserInfo("Mark","123456");
//        // 创建一个List对象
//        List<String> list = new ArrayList<String>();
//        list.add("My name");
//        list.add(" is");
//        list.add(" Mark");
//        try {
//            ObjectOutputStream os = new ObjectOutputStream(
//                    new FileOutputStream("C:/Serial.txt"));
//            os.writeObject(user);// 将User对象写进文件
//            os.writeObject(list);// 将List列表写进文件
//            os.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(
                    "C:/Serial.txt"));
            UserInfo temp = (UserInfo) is.readObject();// 从流中读取User的数据
            System.out.println(temp.getPhone());
            System.out.println(temp.getName());
            List tempList = (List) is.readObject();// 从流中读取List的数据
            for (Iterator iterator = tempList.iterator(); iterator.hasNext();) {
                System.out.print(iterator.next());
            }
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
