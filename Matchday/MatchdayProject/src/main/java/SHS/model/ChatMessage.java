package SHS.model;

import java.sql.Timestamp;

public class ChatMessage {
    private Long id;
    private Integer senderId;
    private String nickname;
    private String message;
    private Timestamp sentTime;
    private Integer meetingId;  // 🔥 meetingId 추가

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Timestamp getSentTime() { return sentTime; }
    public void setSentTime(Timestamp sentTime) { this.sentTime = sentTime; }

    public Integer getMeetingId() { return meetingId; }  // 🔥 Getter 추가
    public void setMeetingId(Integer meetingId) { this.meetingId = meetingId; }  // 🔥 Setter 추가
}
