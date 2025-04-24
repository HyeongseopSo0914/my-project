package PSH.model;

import java.sql.Timestamp;

public class Notification {
    private int id;                     // 알림 고유 ID
    private int memberId;               // 알림을 받는 사용자의 ID
    private String type;                // 알림 유형 (FRIEND_REQUEST, MEETING_CONFIRMED 등)
    private String content;             // 알림 내용
    private Integer relatedId;          // 관련된 엔티티의 ID (모임 ID, 친구 요청 ID 등)
    private boolean isRead;             // 읽음 여부
    private Timestamp createdAt;        // 알림 생성 시간

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // toString 메서드 오버라이드
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", relatedId=" + relatedId +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}