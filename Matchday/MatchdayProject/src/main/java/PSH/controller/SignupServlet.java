package PSH.controller;

import PSH.service.MemberService;
import util.PasswordUtil;
import PSH.model.Member;
import PSH.dao.RegionDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet("/register")
public class SignupServlet extends HttpServlet {
    private MemberService memberService = new MemberService();
    private RegionDAO regionDAO = new RegionDAO(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // 회원가입 폼으로 이동
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("📌 회원가입 요청 도착!"); 
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        
        System.out.println("📌 받은 파라미터:");
        System.out.println("Email: " + email);
        System.out.println("Nickname: " + nickname);

        // 닉네임 중복 체크
        if (memberService.checkNicknameDuplicate(nickname)) {
            System.out.println("❌ 닉네임 중복: " + nickname);
            request.setAttribute("errorMessage", "이미 사용 중인 닉네임입니다.");
            request.getRequestDispatcher("/WEB-INF/views/PSHJSP/signup.jsp").forward(request, response);
            return;
        }

        // Salt 생성 및 비밀번호 해시
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(password, salt);
        
        // 현재 시간 생성
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        boolean termsAgreed = "on".equals(request.getParameter("termsAgreed"));
        boolean privacyAgreed = "on".equals(request.getParameter("privacyAgreed"));
        boolean marketingAgreed = "on".equals(request.getParameter("marketingAgreed"));

        // 회원 등록
        int memberId = memberService.registerMember(
            email, 
            hashedPassword, 
            nickname, 
            salt,
            termsAgreed,
            currentTime,
            privacyAgreed,
            currentTime,
            marketingAgreed,
            marketingAgreed ? currentTime : null
        );

        if (memberId > 0) {
            // 세션에 임시 회원 정보 저장
            HttpSession session = request.getSession();
            Member tempMember = new Member();
            tempMember.setId(memberId);
            tempMember.setEmail(email);
            tempMember.setNickname(nickname);
            session.setAttribute("tempMember", tempMember);

            // 활동 지역 저장
            String selectedDistricts = request.getParameter("selectedDistricts");
            if (selectedDistricts != null && !selectedDistricts.isEmpty()) {
                String[] districtIds = selectedDistricts.split(",");
                for (String districtId : districtIds) {
                    regionDAO.addMemberDistrict(memberId, Integer.parseInt(districtId));
                }
            }

            response.sendRedirect(request.getContextPath() + "/register/games");
        } else {
            request.setAttribute("errorMessage", "회원가입에 실패했습니다. 다시 시도해주세요.");
            request.getRequestDispatcher("/WEB-INF/views/PSHJSP/signup.jsp").forward(request, response);
        }
    }}