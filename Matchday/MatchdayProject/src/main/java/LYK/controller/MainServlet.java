package LYK.controller;

import LYK.model.MemberDTO;
import PSH.dao.MeetingDAO;
import PSH.model.Meeting;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import LYK.model.MainDAO;

@WebServlet("/Main")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MainDAO dao = new MainDAO();
        MeetingDAO meetingDAO = new MeetingDAO();
        
        HttpSession session = request.getSession();
        MemberDTO member = (MemberDTO) session.getAttribute("member");
        if(member != null) {
        	request.setAttribute("member", member);
        }
      
        // 공지사항 데이터 가져오기
        List<String> recentNotices = dao.getRecentNotices();
        request.setAttribute("recentNotices", recentNotices);

        // 인기 게시글 데이터 가져오기
        List<Map<String, Object>> popularTitles = dao.getPopularTitles();
        request.setAttribute("popularTitles", popularTitles);

        // 최신 게시글 데이터 가져오기
        List<Map<String, Object>> latestTitles = dao.getLatestTitles();
        request.setAttribute("latestTitles", latestTitles);

        // 신고 게시글 데이터 가져오기
        List<Map<String, Object>> reportTitles = dao.getReportTitles();
        request.setAttribute("reportTitles", reportTitles);

        // 예정된 회의 데이터 가져오기
        List<Meeting> upcomingMeetings = meetingDAO.getUpcomingMeetings();
        request.setAttribute("upcomingMeetings", upcomingMeetings);

        // JSP로 포워딩
        RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
        dispatcher.forward(request, response);
    }
}



