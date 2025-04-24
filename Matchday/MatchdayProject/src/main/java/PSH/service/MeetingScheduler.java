package PSH.service;

import PSH.dao.MeetingDAO;
import PSH.dao.NotificationDAO;
import PSH.model.Meeting;
import PSH.model.NotificationType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MeetingScheduler {
    private final MeetingDAO meetingDAO;
    private final NotificationDAO notificationDAO;
    private final ScheduledExecutorService scheduler;
    
    public MeetingScheduler() {
        this.meetingDAO = new MeetingDAO();
        this.notificationDAO = new NotificationDAO();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    public void start() {
        // 매분 모임 상태 체크
        scheduler.scheduleAtFixedRate(this::checkMeetingsStatus, 0, 1, TimeUnit.MINUTES);
    }
    
    private void checkMeetingsStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Meeting> meetings = meetingDAO.getAllActiveMeetings();

        for (Meeting meeting : meetings) {
            String dateTimeStr = meeting.getDate() + " " + meeting.getTime(); 
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 초까지 포함
            LocalDateTime meetingDateTime = LocalDateTime.parse(dateTimeStr, formatter);

            if (meeting.isConfirmed()) {
                // 확정된 모임이 24시간 지났는지 체크
                if (now.isAfter(meetingDateTime.plusHours(24))) {
                    meetingDAO.updateMeetingStatus(meeting.getMeetingId(), "COMPLETED");
                    notifyMeetingCompleted(meeting);
                }
            } else {
                // 미확정 모임이 시간이 지났는지 체크
                if (now.isAfter(meetingDateTime)) {
                    meetingDAO.updateMeetingStatus(meeting.getMeetingId(), "EXPIRED");
                    notifyMeetingExpired(meeting);
                }
            }
        }
    }

    
    private void notifyMeetingCompleted(Meeting meeting) {
        List<Integer> participantIds = meetingDAO.getParticipantIds(meeting.getMeetingId());
        for (Integer participantId : participantIds) {
            notificationDAO.createNotification(
                participantId,
                NotificationType.MEETING_COMPLETED,
                "'" + meeting.getTitle() + "' 모임이 종료되었습니다.",
                meeting.getMeetingId()
            );
        }
    }
    
    private void notifyMeetingExpired(Meeting meeting) {
        List<Integer> participantIds = meetingDAO.getParticipantIds(meeting.getMeetingId());
        for (Integer participantId : participantIds) {
            notificationDAO.createNotification(
                participantId,
                NotificationType.MEETING_EXPIRED,
                "'" + meeting.getTitle() + "' 모임이 기간 만료되었습니다.",
                meeting.getMeetingId()
            );
        }
    }
    
    public void shutdown() {
        scheduler.shutdown();
    }
}