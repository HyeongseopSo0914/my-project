package PSH.service;

import java.sql.Timestamp;

import PSH.dao.MemberDAO;
import PSH.model.Member;
import util.PasswordUtil;

public class MemberService {
    private MemberDAO memberDAO = new MemberDAO();

    // 회원가입 메서드 (Salt와 암호화된 비밀번호 저장)
 // 수정된 코드
    public int registerMember(String email, String password, String nickname, String salt, 
            boolean termsAgreed, Timestamp termsAgreedAt,
            boolean privacyAgreed, Timestamp privacyAgreedAt,
            boolean marketingAgreed, Timestamp marketingAgreedAt) {
        
        System.out.println("📌 registerMember - Parameters received:");
        System.out.println("Email: " + email);
        System.out.println("Nickname: " + nickname);
        System.out.println("Terms Agreed: " + termsAgreed);
        System.out.println("Privacy Agreed: " + privacyAgreed);
        System.out.println("Marketing Agreed: " + marketingAgreed);

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(password);
        member.setNickname(nickname);
        member.setSalt(salt);
        member.setTermsAgreed(termsAgreed);
        member.setTermsAgreedAt(termsAgreedAt);
        member.setPrivacyAgreed(privacyAgreed);
        member.setPrivacyAgreedAt(privacyAgreedAt);
        member.setMarketingAgreed(marketingAgreed);
        member.setMarketingAgreedAt(marketingAgreedAt);
        
        System.out.println("📌 Member object created:");
        System.out.println("Member Email: " + member.getEmail());
        System.out.println("Member Nickname: " + member.getNickname());

        int memberId = memberDAO.insertMember(member);
        return memberId;
    }
    
    public boolean checkNicknameDuplicate(String nickname) {
        return memberDAO.isNicknameExists(nickname);
    }
    
    public boolean checkEmailDuplicate(String email) {
        return memberDAO.isEmailExists(email);
    }

    // 로그인 메서드
    public Member loginMember(String email, String inputPassword) {
        System.out.println("📌 loginMember 호출됨! 이메일: " + email);

        // 1. 이메일로 회원 찾기
        Member member = memberDAO.findByEmail(email);

        if (member != null) {
            String storedHash = member.getPassword();  // 저장된 암호화된 비밀번호
            String storedSalt = member.getSalt();      // 저장된 Salt 값

            // 2. 입력한 비밀번호 검증
            boolean isPasswordValid = PasswordUtil.validatePassword(inputPassword, storedHash, storedSalt);

            if (isPasswordValid) {
                System.out.println("✅ 비밀번호 일치! 로그인 성공.");
                member.setPassword(null);  // 보안을 위해 비밀번호는 세션에 저장하지 않음
                return member;
            } else {
                System.out.println("❌ 비밀번호 불일치! 로그인 실패.");
            }
        } else {
            System.out.println("❌ 해당 이메일로 등록된 회원이 없습니다.");
        }

        return null;  // 로그인 실패 시 null 반환
    }

    // 이메일로 회원 정보 조회 (LoginServlet에서 사용)
    public Member getMemberByEmail(String email) {
        return memberDAO.findByEmail(email);
    }
    public void updatePasswordWithSalt(int userId, String newHashedPassword, String newSalt) {
        memberDAO.updatePassword(userId, newHashedPassword, newSalt);
    }
    
    public void saveAuthCode(String email, String authCode) {
        memberDAO.saveAuthCode(email, authCode);
    }

    // 이메일 인증 코드 검증
    public boolean checkAuthCodeInDatabase(String email, String authCode) {
        return memberDAO.checkAuthCodeInDatabase(email, authCode);
    }

    // 이메일 인증 상태 업데이트
    public boolean updateEmailVerifiedStatus(String email) {
        return memberDAO.updateEmailVerifiedStatus(email);
    }

}
