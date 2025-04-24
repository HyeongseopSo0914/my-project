package PSH.controller;

import PSH.model.Meeting;
import PSH.model.Member;
import LYK.model.MainDAO;  // MainDAO 임포트
import PSH.dao.MeetingDAO;  // MeetingDAO로 변경
import PSW.model.Notice; 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("")  // 루트 경로("/")로 설정
public class LandingServlet extends HttpServlet {
    private MainDAO mainDAO;  // 선언만 하고
    private MeetingDAO meetingDAO;  // MeetingDAO 추가

    @Override
    public void init() throws ServletException {
        super.init();
        mainDAO = new MainDAO(); 
        meetingDAO = new MeetingDAO();  /// init에서 초기화
        System.out.println("LandingServlet initialized with MainDAO"); // 초기화 로그
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("LandingServlet doGet started"); // 시작 로그

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
       
        try {
            // 공지사항 데이터 가져오기
        	// 공지사항 데이터 가져오기 (ID 포함)
            List<Notice> recentNotices = mainDAO.getRecentNoticesWithId();
            System.out.println("Loaded notices: " + recentNotices);
            request.setAttribute("recentNotices", recentNotices);

            // 예정된 모임 데이터 가져오기
            List<Meeting> upcomingMeetings = meetingDAO.getUpcomingMeetings();  // MeetingDAO 사용
            request.setAttribute("upcomingMeetings", upcomingMeetings);

        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
       
        // 세션에서 로그인 정보 확인
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        
        System.out.println("User login status: " + (loggedInMember != null ? "Logged in" : "Not logged in"));
       
        // 로그인 상태에 따라 다른 페이지로 포워딩
        if (loggedInMember != null) {
            request.getRequestDispatcher("/WEB-INF/views/landing.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/landing.jsp").forward(request, response);
        }
    }
}