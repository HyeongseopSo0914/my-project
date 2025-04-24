package PSH.controller;

import PSH.dao.MeetingDAO;
import PSH.model.Meeting;
import PSH.model.Member;
import PSH.dao.NotificationDAO;
import PSH.model.NotificationType;  // 추가
import java.util.List;  // 추가

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/meeting/confirm")
public class ConfirmMeetingServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();  // 추가
    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 로그인 체크
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            sendJsonResponse(response, false, "로그인이 필요합니다.");
            return;
        }

        // 모임 ID 체크
        String meetingIdStr = request.getParameter("meetingId");
        if (meetingIdStr == null || meetingIdStr.trim().isEmpty()) {
            sendJsonResponse(response, false, "모임 ID가 필요합니다.");
            return;
        }

        try {
            int meetingId = Integer.parseInt(meetingIdStr);
            Meeting meeting = meetingDAO.getMeetingById(meetingId);
            
            // 🔍 모임 상태 확인
            System.out.println("DEBUG: 모임 ID = " + meetingId);
            System.out.println("DEBUG: 현재 모임 상태 = " + meeting.getStatus());

            if (meeting == null) {
                sendJsonResponse(response, false, "존재하지 않는 모임입니다.");
                return;
            }

            if (meeting.getCreatedBy() != loggedInMember.getId()) {
                sendJsonResponse(response, false, "모임장만 모임을 확정할 수 있습니다.");
                return;
            }

            if (meeting.isConfirmed()) {
                sendJsonResponse(response, false, "이미 확정된 모임입니다.");
                return;
            }

            boolean success = meetingDAO.confirmMeeting(meetingId);
            
            // 🔍 SQL 실행 결과 확인
            System.out.println("DEBUG: confirmMeeting 실행 결과 = " + success);

            if (success) {
                List<Member> participants = meetingDAO.getMeetingParticipants(meetingId);
                for (Member participant : participants) {
                    if (participant.getId() != meeting.getCreatedBy()) {
                        notificationDAO.createNotification(
                            participant.getId(),
                            NotificationType.MEETING_CONFIRMED,
                            "'" + meeting.getTitle() + "' 모임이 확정되었습니다.",
                            meetingId
                        );
                    }
                }
                sendJsonResponse(response, true, "모임이 확정되었습니다.");
            } else {
                sendJsonResponse(response, false, "모임 확정에 실패했습니다.");
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