package SHS.controller;

import PSH.service.MemberService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/verifyEmail")
public class EmailVerificationCheckServlet extends HttpServlet {
    private MemberService memberService = new MemberService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        String authCode = request.getParameter("authCode");
        
        System.out.println("📧 Received verification request - Email: " + email);
        System.out.println("🔑 Received verification request - Auth Code: " + authCode);

        if (email == null || authCode == null || email.trim().isEmpty() || authCode.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"이메일과 인증 코드를 모두 입력해주세요.\"}");
            return;
        }

        // DB에서 인증 코드 확인
        boolean isValidCode = memberService.checkAuthCodeInDatabase(email, authCode);
        System.out.println("✅ Is Valid Code: " + isValidCode);

        if (isValidCode) {
            // 이메일 인증 처리
            boolean isUpdated = memberService.updateEmailVerifiedStatus(email);
            System.out.println("✅ Email verification status updated: " + isUpdated);
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\":true,\"message\":\"이메일 인증이 완료되었습니다.\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"잘못된 인증번호입니다.\"}");
        }
    }}