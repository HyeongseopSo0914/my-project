package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    // Salt 생성
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    // 비밀번호 해시화 (Salt 적용)
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // 비밀번호와 Salt를 바이트 배열로 변환 후 결합
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);

            byte[] combined = new byte[passwordBytes.length + saltBytes.length];
            System.arraycopy(passwordBytes, 0, combined, 0, passwordBytes.length);
            System.arraycopy(saltBytes, 0, combined, passwordBytes.length, saltBytes.length);

            byte[] hash = combined;

            // Stretching: 10000번 반복 해시
            for (int i = 0; i < 10000; i++) {
                md.reset();
                hash = md.digest(hash);
            }

            // 최종 해시 값을 Base64로 인코딩하여 반환
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


 // 🔑 비밀번호 검증 메서드 (기존 회원 + 신규 회원 처리)
    public static boolean validatePassword(String inputPassword, String storedHash, String storedSalt) {
        if (storedSalt == null || storedSalt.isEmpty()) {
            // 기존 회원: 평문 비밀번호와 직접 비교
            return inputPassword.equals(storedHash);
        } else {
            // 신규 회원: Salt + 해시 검증
            String hashedInputPassword = hashPassword(inputPassword, storedSalt);
            return hashedInputPassword.equals(storedHash);
        }
    }
}
