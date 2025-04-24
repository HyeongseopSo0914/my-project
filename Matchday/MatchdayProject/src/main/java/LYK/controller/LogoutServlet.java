package LYK.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 세션 종료
        HttpSession session = request.getSession(false);
        if (session != null) {
        	// 세션에서 member 객체 제거
        	session.removeAttribute("member");
        	// 세션 종료
            session.invalidate();
        }
        // 로그인 페이지로 이동
        response.sendRedirect(request.getContextPath() + "/Main");
    }

}
