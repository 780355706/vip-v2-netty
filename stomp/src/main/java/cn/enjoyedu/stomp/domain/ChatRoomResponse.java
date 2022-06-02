package cn.enjoyedu.stomp.domain;

/**
 * @author Mark老师   享学课堂 https://enjoy.ke.qq.com
 * 类说明：聊天室的应答实体
 */
public class ChatRoomResponse {
    private String name;
    private String chatValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatValue() {
        return chatValue;
    }

    public void setChatValue(String chatValue) {
        this.chatValue = chatValue;
    }
}
