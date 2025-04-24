package PSH.controller;

import PSH.service.MemberService;
import PSH.model.Member;
import util.PasswordUtil;  // PasswordUtil 임포트
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private MemberService memberService = new MemberService();

    // 로그인 페이지 표시
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInMember") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/login.jsp").forward(request, response);
    }

    // 로그인 처리
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 폼 데이터 받기
        String email = request.getParameter("email");
        String inputPassword = request.getParameter("password");  // 사용자가 입력한 비밀번호
        String autoLogin = request.getParameter("autoLogin");

        // 이메일로 사용자 정보 조회
        Member member = memberService.getMemberByEmail(email);

        if (member != null) {
            String storedHash = member.getPassword();  // 저장된 암호화된 비밀번호
            String storedSalt = member.getSalt();      // 저장된 Salt 값
            
            // 입력한 비밀번호와 해시된 비밀번호 비교 디버깅
            System.out.println("🔍 입력된 비밀번호: " + inputPassword);
            System.out.println("🔍 저장된 해시 값: " + storedHash);
            System.out.println("🔍 저장된 Salt 값: " + storedSalt);

            // 비밀번호 검증
            boolean isPasswordValid = PasswordUtil.validatePassword(inputPassword, storedHash, storedSalt);
          

             if (isPasswordValid) {
                // 로그인 성공
                HttpSession session = request.getSession();
                session.setAttribute("loggedInMember", member);
                session.setAttribute("loggedInMemberId", member.getId()); // 이 부분 추가
                System.out.println("✅ 로그인 성공, 세션에 회원 정보 저장됨: " + member.getEmail());

                // 기존 회원이라면 비밀번호 자동 암호화 (Salt가 없으면)
                if (storedSalt == null || storedSalt.isEmpty()) {
                    System.out.println("🔄 기존 회원 감지, 비밀번호 보안 강화 진행 중...");

                    // 새 Salt 생성 및 비밀번호 재암호화
                    String newSalt = PasswordUtil.generateSalt();
                    String newHashedPassword = PasswordUtil.hashPassword(inputPassword, newSalt);

                    // DB에 새로운 암호화된 비밀번호와 Salt 저장
                    memberService.updatePasswordWithSalt(member.getId(), newHashedPassword, newSalt);
                }

                // 로그인 성공 후 리다이렉트
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }


        // 로그인 실패 시 처리
        request.setAttribute("errorMessage", "이메일 또는 비밀번호가 올바르지 않습니다.");
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/login.jsp")
               .forward(request, response);
    }
}
}
