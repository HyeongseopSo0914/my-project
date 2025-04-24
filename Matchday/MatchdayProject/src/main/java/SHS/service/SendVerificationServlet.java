package SHS.service;

import SHS.service.EmailService;
import PSH.service.MemberService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import jakarta.mail.MessagingException;

@WebServlet("/sendVerification")
public class SendVerificationServlet extends HttpServlet {
    private EmailService emailService = new EmailService();
    private MemberService memberService = new MemberService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            sendErrorResponse(response, "이메일 주소를 입력해주세요.");
            return;
        }

        try {
            // 이메일 중복 체크
            if (memberService.checkEmailDuplicate(email)) {
                sendErrorResponse(response, "이미 등록된 이메일입니다.");
                return;
            }

            // 인증번호 생성 및 발송
            String verificationCode = EmailService.generateVerificationCode();
            emailService.sendVerificationEmail(email, verificationCode);
            
            // DB에 인증코드 저장
            memberService.saveAuthCode(email, verificationCode);
            
            // 성공 응답
            String jsonResponse = "{\"success\":true,\"message\":\"인증번호가 발송되었습니다.\"}";
            response.getWriter().write(jsonResponse);
            
        } catch (MessagingException e) {
            e.printStackTrace();
            sendErrorResponse(response, "이메일 발송 중 오류가 발생했습니다.");
        }
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message) 
            throws IOException {
        String jsonResponse = String.format("{\"success\":false,\"message\":\"%s\"}", message);
        response.getWriter().write(jsonResponse);
    }
}