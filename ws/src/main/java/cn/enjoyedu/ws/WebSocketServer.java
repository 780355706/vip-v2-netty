package cn.enjoyedu.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *@author Mark老师   享学课堂 https://enjoy.ke.qq.com
 *类说明：提供WebSocket服务
 */
@ServerEndpoint(value = "/ws/asset")
@Component
public class WebSocketServer {

    private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private static final AtomicInteger onlineCount
            = new AtomicInteger(0);
    /*线程安全Set，用来存放每个客户端对应的Session对象。*/
    private static CopyOnWriteArraySet<Session> sessionSet
            = new CopyOnWriteArraySet<Session>();
    /*线程安全Map，用来存放每个客户端sessionid和用户名的对应关系。*/
    private static Map<String,String> sessionMap
            = new ConcurrentHashMap<>();


    /*** 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        //将用户session，session和用户名对应关系放入本地缓存
        sessionSet.add(session);
        Map<String, List<String>> pathParameters
                = session.getRequestParameterMap();
        String userId = pathParameters.get("toUserId").get(0);
        sessionMap.put(session.getId(),userId);
        log.info("有连接加入，当前连接数为：{}", onlineCount.incrementAndGet());

        try {
            //通知所有用户有新用户上线
            broadCastInfo("系统消息@^用户["+userId+"]加入群聊。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*** 连接关闭调用的方法*/
    @OnClose
    public void onClose(Session session) {
        //将用户session，session和用户名对应关系从本地缓存移除
        sessionSet.remove(session);
        Map<String, List<String>> pathParameters
                = session.getRequestParameterMap();
        String userId = sessionMap.get(session.getId());
        sessionMap.remove(session.getId());
        int cnt = onlineCount.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
        try {
            //通知所有用户有用户下线
            broadCastInfo("系统消息@^用户["+userId+"]退出群聊。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*** 收到客户端消息后调用的方法*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端{}的消息：{}",
                sessionMap.get(session.getId()),message);
        if(message.startsWith("ToUser:")){
            //这里可以实现一对一聊天sendMessageAlone();
        }else{
            //实现群聊
            String msger = sessionMap.get(session.getId());
            try {
                broadCastInfo(msger+"@^"+message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***出现错误时的处理*/
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}",error.getMessage(),session.getId());
        error.printStackTrace();
    }

    /*** 发送消息的基础方法*/
    public static void basicSendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("发送消息出错：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /*** 群发消息*/
    public static void broadCastInfo(String message) throws IOException {
        for (Session session : sessionSet) {
            if(session.isOpen()){
                basicSendMessage(session, message);
            }
        }
    }

    /*** 指定Session发送消息*/
    public static void sendMessageAlone(String sessionId, String message) throws IOException {
        Session session = null;
        for (Session s : sessionSet) {
            if(s.getId().equals(sessionId)){
                session = s;
                break;
            }
        }
        if(session!=null){
            basicSendMessage(session, message);
        }
        else{
            log.warn("没有找到你指定ID的会话：{}",sessionId);
        }
    }

}
