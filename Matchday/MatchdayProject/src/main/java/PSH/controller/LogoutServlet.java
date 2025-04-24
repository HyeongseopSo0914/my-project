package PSH.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 현재 세션 가져오기
        HttpSession session = request.getSession(false); // false는 새 세션을 생성하지 않음
        
        if (session != null) {
            // 세션에서 사용자 정보 삭제
            session.removeAttribute("loggedInMember");
            
            // 세션 무효화
            session.invalidate();
            
            // 쿠키 삭제 (자동 로그인 관련 쿠키가 있다면)
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (javax.servlet.http.Cookie cookie : cookies) {
                    if (cookie.getName().equals("autoLogin")) {
                        cookie.setMaxAge(0); // 쿠키 만료
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }
        }
        
        // 로그인 페이지로 리다이렉트
        response.sendRedirect(request.getContextPath() + "/");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response); // POST 요청도 GET으로 처리
    }
}