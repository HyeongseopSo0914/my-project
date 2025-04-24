package SHS.service;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import SHS.dao.ChatMessageDAO;

import java.sql.*;

@WebServlet("/saveChatMessageServlet")

public class saveChatMessageServlet extends HttpServlet {
	
    /**
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    System.out.println("saveChatMessage");

	    request.setCharacterEncoding("UTF-8");  
	    String nickname = request.getParameter("nickname");
	    String message = request.getParameter("message");
	    int writerId = Integer.parseInt(request.getParameter("writerId"));
	    int meetingId = Integer.parseInt(request.getParameter("meetingId")); // 🔥 meetingId 추가

	    // 메시지 저장 DB 처리
	    ChatMessageDAO.saveMessage(nickname, message, writerId, meetingId);

	    response.setStatus(HttpServletResponse.SC_OK);
	}
}


