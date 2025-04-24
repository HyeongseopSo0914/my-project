package LYK.websocket;

import LYK.model.MemberDTO;
import PSH.model.Member;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.servlet.http.HttpSession;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

@ServerEndpoint(value = "/chat", configurator = RealTimeChat.Configurator.class)
public class RealTimeChat {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private Member member;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if (httpSession != null && httpSession.getAttribute("loggedInMember") != null) {
            member = (Member) httpSession.getAttribute("loggedInMember");
            System.out.println("✅ WebSocket 연결됨, 닉네임: " + member.getNickname());
        } else {
        	member = null;
        	System.out.println("❌ WebSocket 연결됨, 로그인되지 않은 사용자");
        }
        sessions.add(session);
        System.out.println("WebSocket 연결됨: "+session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session senderSession) {
        if (member == null) {
            System.out.println("❌ 메시지를 보낼 수 없음: 로그인되지 않은 사용자");
            return;
        }
        
        System.out.println("📩 받은 메시지: " + message);

        String formattedMessage = member.getNickname() + " : " + message;
        System.out.println("📤 클라이언트로 보낼 메시지: " + formattedMessage);

        synchronized (sessions) {
            for (Session session : sessions) {
                try {
                    session.getBasicRemote().sendText(formattedMessage);
                    System.out.println("✅ 메시지 전송 완료: " + formattedMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    public static class Configurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
            HttpSession httpSession = (HttpSession) request.getHttpSession();
            config.getUserProperties().put(HttpSession.class.getName(), httpSession);
        }
    }
}