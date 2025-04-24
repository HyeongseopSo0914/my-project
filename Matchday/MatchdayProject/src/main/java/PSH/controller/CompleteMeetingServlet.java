package PSH.controller;

import PSH.dao.MeetingDAO;
import PSH.dao.NotificationDAO;
import PSH.model.Meeting;
import PSH.model.Member;
import PSH.model.NotificationType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/meeting/complete")
public class CompleteMeetingServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();

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
            
            // 모임 존재 체크
            if (meeting == null) {
                sendJsonResponse(response, false, "존재하지 않는 모임입니다.");
                return;
            }

            // 모임장 권한 체크
            if (meeting.getCreatedBy() != loggedInMember.getId()) {
                sendJsonResponse(response, false, "모임장만 모임을 종료할 수 있습니다.");
                return;
            }

            // 모임이 확정 상태인지 체크
            if (!meeting.isConfirmed()) {
                sendJsonResponse(response, false, "확정되지 않은 모임은 종료할 수 없습니다.");
                return;
            }

            // 이미 종료된 모임인지 체크
            if ("COMPLETED".equals(meeting.getStatus())) {
                sendJsonResponse(response, false, "이미 종료된 모임입니다.");
                return;
            }
            
            // 모임 시작 시간이 지났는지 체크
            if (!meetingDAO.hasMeetingStarted(meeting)) {
                sendJsonResponse(response, false, "모임 시작 시간이 지나지 않았습니다. 모임 종료는 모임 시작 시간 이후에만 가능합니다.");
                return;
            }

            // 모임 종료 처리
            boolean success = meetingDAO.updateMeetingStatus(meetingId, "COMPLETED");
            if (success) {
                // 참가자들에게 알림 전송
                List<Integer> participantIds = meetingDAO.getParticipantIds(meetingId);
                for (Integer participantId : participantIds) {
                    if (participantId != meeting.getCreatedBy()) {
                        notificationDAO.createNotification(
                            participantId,
                            NotificationType.MEETING_COMPLETED,
                            "'" + meeting.getTitle() + "' 모임이 종료되었습니다.",
                            meetingId
                        );
                    }
                }
                sendJsonResponse(response, true, "모임이 성공적으로 종료되었습니다.");
            } else {
                sendJsonResponse(response, false, "모임 종료에 실패했습니다.");
            }
            System.out.println("모임 종료 확인 - 날짜: " + meeting.getDate());
            System.out.println("모임 종료 확인 - 시간: " + meeting.getTime());

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