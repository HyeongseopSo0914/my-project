package PSH.controller;



import PSH.dao.MeetingDAO;
import PSH.dao.NotificationDAO;
import PSH.model.Meeting;
import PSH.model.NotificationType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/meeting/kick")
public class KickParticipantServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	 request.setCharacterEncoding("UTF-8");
    	    response.setContentType("application/json; charset=UTF-8");
    	    response.setCharacterEncoding("UTF-8");
    	response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String meetingIdParam = request.getParameter("meetingId");
        String participantIdParam = request.getParameter("participantId");

        // 디버깅 로그 추가
        System.out.println("퇴장 요청 도착: meetingId=" + meetingIdParam + ", participantId=" + participantIdParam);

        if (meetingIdParam == null || participantIdParam == null || participantIdParam.trim().isEmpty()) {
            System.out.println("ERROR: meetingId 또는 participantId가 null 또는 빈 문자열");
            out.write("{\"success\": false, \"message\": \"잘못된 요청입니다.\"}");
            return;
        }

        int meetingId;
        int participantId;
        try {
            meetingId = Integer.parseInt(meetingIdParam);
            participantId = Integer.parseInt(participantIdParam);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: meetingId 또는 participantId가 숫자가 아님");
            out.write("{\"success\": false, \"message\": \"잘못된 요청입니다.\"}");
            return;
        }

        // 모임장이 맞는지 확인 (이 로직은 세션에서 가져온 로그인 사용자 ID와 비교)
        Integer loggedInUserId = (Integer) request.getSession().getAttribute("loggedInMemberId");
        System.out.println("로그인한 사용자 ID: " + loggedInUserId);
        
        MeetingDAO meetingDAO = new MeetingDAO();
        NotificationDAO notificationDAO = new NotificationDAO();
        Meeting meeting = meetingDAO.getMeetingById(meetingId);
        
        System.out.println("모임 ID의 모임장 ID: " + meeting.getCreatedBy());

        if (loggedInUserId == null || loggedInUserId != meeting.getCreatedBy()) {
            System.out.println("ERROR: 모임장이 아님");
            out.write("{\"success\": false, \"message\": \"권한이 없습니다.\"}");
            return;
        }

        // 참가자 퇴장 처리
        boolean success = meetingDAO.removeParticipant(meetingId, participantId);
        
        if (success) {
            // 퇴장된 멤버에게 알림 전송
            notificationDAO.createNotification(
                participantId, 
                NotificationType.MEETING_KICKED, 
                "'" + meeting.getTitle() + "' 모임에서 퇴장되었습니다.", 
                meetingId
            );

            System.out.println("퇴장 성공: participantId=" + participantId);
            out.write("{\"success\": true, \"message\": \"참가자를 퇴장시켰습니다.\"}");
        } else {
            System.out.println("ERROR: 퇴장 실패");
            out.write("{\"success\": false, \"message\": \"퇴장 처리 중 오류가 발생했습니다.\"}");
        }
    }}
