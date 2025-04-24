package LYK.controller;

import LYK.model.MemberDAO;
import LYK.model.MemberDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException{
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		// 이미 로그인된 사용자라면 메인 페이지로 리다이렉트
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInMember") != null) {
            response.sendRedirect(request.getContextPath() + "Main");
            return;
        }
		
        // 로그인 페이지로 포워드
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse reponse)
		throws ServletException, IOException{
		
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		MemberDAO memberDAO = new MemberDAO();
		boolean isValid = memberDAO.isValidMember(email, password);
		
		if(isValid) {
			MemberDTO member = memberDAO.getMemberInfo(email);
			
			HttpSession session = request.getSession();
			session.setAttribute("member", member);
			reponse.sendRedirect(request.getContextPath() + "/Main");
		}else {
			request.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
			request.getRequestDispatcher("/views/login.jsp").forward(request, reponse);
		}
	}
}
