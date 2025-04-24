package PSH.model;

import java.sql.Timestamp;

public class FriendRequest {
    private int id;
    private int senderId;
    private int receiverId;
    private String message;
    private String status;
    private Timestamp createdAt;
    private String senderName;    // 송신자 닉네임 (조인 결과)
    private String receiverName;  // 수신자 닉네임 (조인 결과)
    private String senderProfileUrl;  // 송신자 프로필 이미지 URL
    private String receiverProfileUrl; // 수신자 프로필 이미지 URL
    private String senderRegions;    // 보낸 사람의 활동지역들
    private String senderGames;      // 보낸 사람의 관심게임들

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderProfileUrl() {
        return senderProfileUrl;
    }

    public void setSenderProfileUrl(String senderProfileUrl) {
        this.senderProfileUrl = senderProfileUrl;
    }

    public String getReceiverProfileUrl() {
        return receiverProfileUrl;
    }

    public void setReceiverProfileUrl(String receiverProfileUrl) {
        this.receiverProfileUrl = receiverProfileUrl;
    }
    public String getSenderRegions() {
        return senderRegions;
    }
    
    public void setSenderRegions(String senderRegions) {
        this.senderRegions = senderRegions;
    }
    
    public String getSenderGames() {
        return senderGames;
    }
    
    public void setSenderGames(String senderGames) {
        this.senderGames = senderGames;
    }
}
