package SHS.service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import SHS.dao.ChatMessageDAO;

@ServerEndpoint("/chat/websocket/{meetingId}")
public class Websocket {
    private static Map<Integer, Set<Session>> meetingClients = Collections.synchronizedMap(new HashMap<>());

    @OnOpen
    public void handleOpen(Session session, @PathParam("meetingId") int meetingId) {
        meetingClients.putIfAbsent(meetingId, Collections.synchronizedSet(new HashSet<>()));
        meetingClients.get(meetingId).add(session);
        System.out.println("Client connected to meeting " + meetingId);
    }

    @OnMessage
    public void handleMessage(String message, Session session, @PathParam("meetingId") int meetingId) throws IOException {
        System.out.println("Received message: " + message);
        try {
            JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
            String nickname = jsonMessage.get("nickname").getAsString();
            String msg = jsonMessage.get("message").getAsString();
            int writerId = jsonMessage.get("writerId").getAsInt();

            System.out.println("Parsed message - nickname: " + nickname + ", message: " + msg + ", writerId: " + writerId);

            // DB에 메시지 저장
            ChatMessageDAO.saveMessage(nickname, msg, writerId, meetingId);

            // 브로드캐스트
            synchronized (meetingClients) {
                Set<Session> clients = meetingClients.getOrDefault(meetingId, Collections.emptySet());
                System.out.println("Broadcasting to " + clients.size() + " clients in meeting " + meetingId);
                for (Session client : clients) {
                    if (client.isOpen()) {
                        client.getBasicRemote().sendText(message);
                        System.out.println("Message sent to client: " + client.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClose
    public void handleClose(Session session, @PathParam("meetingId") int meetingId) {
        Set<Session> clients = meetingClients.get(meetingId);
        if (clients != null) {
            clients.remove(session);
            if (clients.isEmpty()) {
                meetingClients.remove(meetingId);
            }
        }
        System.out.println("Client disconnected from meeting " + meetingId);
    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }
}
