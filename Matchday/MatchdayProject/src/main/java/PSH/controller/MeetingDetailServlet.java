package PSH.controller;

import PSH.dao.MeetingDAO;
import PSH.model.Meeting;
import PSH.model.Member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/meeting/*")
public class MeetingDetailServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/meetings");
            return;
        }

        try {
            int meetingId = Integer.parseInt(pathInfo.substring(1));
            Meeting meeting = meetingDAO.getMeetingById(meetingId);
            
            if (meeting == null) {
                response.sendRedirect(request.getContextPath() + "/meetings");
                return;
            }

            // 세션에서 로그인 정보 확인
            HttpSession session = request.getSession();
            Member loggedInMember = (Member) session.getAttribute("loggedInMember");

            // 현재 사용자가 이미 참가했는지 확인
            boolean isJoined = false;
            if (loggedInMember != null) {
                isJoined = meetingDAO.isUserJoined(meetingId, loggedInMember.getId());
            }

            // 참가자 목록 조회
            List<Member> participants = meetingDAO.getMeetingParticipants(meetingId);

            // 모임 변경 이력 조회
            List<Map<String, Object>> meetingChanges = meetingDAO.getMeetingChanges(meetingId);

            request.setAttribute("meeting", meeting);
            request.setAttribute("isJoined", isJoined);
            request.setAttribute("participants", participants);
            request.setAttribute("loggedInMember", loggedInMember);
            request.setAttribute("meetingChanges", meetingChanges);

            request.getRequestDispatcher("/WEB-INF/views/PSHJSP/meeting_detail.jsp")
                   .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/meetings");
        }
    }
}