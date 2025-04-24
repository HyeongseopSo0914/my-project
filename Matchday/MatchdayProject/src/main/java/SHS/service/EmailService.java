package SHS.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.security.SecureRandom;

public class EmailService {
    private static final String FROM_EMAIL = "matchday_2025@naver.com";
    private static final String PASSWORD = "9V99U8J1Y1MH";
    
    // 6자리 랜덤 인증번호 생성
    public static String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);  // 100000-999999 범위
        return String.valueOf(code);
    }
    
    public void sendVerificationEmail(String toEmail, String verificationCode) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.naver.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.naver.com");
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Matchday"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Matchday 이메일 인증");
            
            String htmlContent = String.format(
                "<div style='background-color: #f5f5f5; padding: 20px; font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 10px;'>" +
                    "<h1 style='color: #333; text-align: center;'>Matchday 이메일 인증</h1>" +
                    "<p style='color: #666; font-size: 16px; line-height: 1.5;'>안녕하세요,</p>" +
                    "<p style='color: #666; font-size: 16px; line-height: 1.5;'>Matchday 회원가입을 위한 이메일 인증번호입니다.</p>" +
                    "<div style='background-color: #f8f9fa; padding: 15px; margin: 20px 0; text-align: center; border-radius: 5px;'>" +
                        "<h2 style='color: #007bff; margin: 0; font-size: 24px;'>%s</h2>" +
                    "</div>" +
                    "<p style='color: #666; font-size: 16px; line-height: 1.5;'>위 인증번호를 입력하여 이메일 인증을 완료해 주세요.</p>" +
                    "<p style='color: #666; font-size: 14px; margin-top: 30px;'>본 이메일은 발신전용이며, 문의사항이 있으시면 고객센터로 연락해 주시기 바랍니다.</p>" +
                "</div>" +
                "</div>",
                verificationCode
            );
            
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            
        } catch (Exception e) {
            throw new MessagingException("Failed to send email: " + e.getMessage());
        }
    }
}