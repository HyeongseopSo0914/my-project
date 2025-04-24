package SHS.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 필요한 데이터를 request 객체에 담고
        // chat.jsp로 포워딩
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/SHSJSP/chat.jsp");
        dispatcher.forward(request, response);
    }
}

