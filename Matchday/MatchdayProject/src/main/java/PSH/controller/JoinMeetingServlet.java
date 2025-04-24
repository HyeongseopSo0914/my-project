package PSH.controller;

import PSH.dao.MeetingDAO;
import PSH.model.Member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/meeting/join")
public class JoinMeetingServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            sendJsonResponse(response, false, "로그인이 필요합니다.");
            return;
        }

        String meetingIdStr = request.getParameter("meetingId");
        if (meetingIdStr == null || meetingIdStr.trim().isEmpty()) {
            sendJsonResponse(response, false, "모임 ID가 필요합니다.");
            return;
        }

        try {
            int meetingId = Integer.parseInt(meetingIdStr);
            
            if (meetingDAO.isUserJoined(meetingId, loggedInMember.getId())) {
                sendJsonResponse(response, false, "이미 참가한 모임입니다.");
                return;
            }

            boolean success = meetingDAO.joinMeeting(meetingId, loggedInMember.getId());
            if (success) {
                sendJsonResponse(response, true, "모임 참가가 완료되었습니다.");
            } else {
                sendJsonResponse(response, false, "모임 참가에 실패했습니다. 정원이 가득 찼을 수 있습니다.");
            }

        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "잘못된 모임 ID입니다.");
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) 
            throws IOException {
        String json = String.format("{\"success\":%b,\"message\":\"%s\"}", success, message);
        response.getWriter().write(json);
    }
}