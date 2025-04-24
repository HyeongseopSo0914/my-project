package SHS.service;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import SHS.dao.ChatMessageDAO;
import java.util.List;
import SHS.model.ChatMessage;  // 메시지 모델 클래스를 사용한다고 가정합니다.

@WebServlet("/getChatMessages")
public class GetChatMessagesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int meetingId = Integer.parseInt(request.getParameter("meetingId")); // 클라이언트에서 meetingId를 전달받음

        List<ChatMessage> messages = ChatMessageDAO.getRecentMessages(meetingId, 20); // meetingId 기반으로 메시지 가져옴

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.write("[");
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage msg = messages.get(i);
            String jsonMessage = String.format(
                "{\"nickname\":\"%s\",\"message\":\"%s\",\"sentTime\":\"%s\"}",
                msg.getNickname(), msg.getMessage(), msg.getSentTime());
            out.write(jsonMessage);
            if (i < messages.size() - 1) {
                out.write(",");
            }
        }
        out.write("]");
    }
}


