package PSH.model;

import java.sql.Timestamp;
import java.util.List;

public class Meeting {
    private int meetingId;
    private String title;
    private String description;
    private int gameId;
    private String gameName;
    private int regionId;
    private int districtId;
    private String regionName;
    private String districtName;
    private String location;
    private String date;
    private String time;
    private int maxParticipants;
    private int currentParticipants;
    private int createdBy;
    private String hostName;
    private double hostRating;
    private Timestamp createdAt;
    private boolean isConfirmed;
    private boolean isCompleted;  // 모임 종료 여부 추가
    private boolean isUserParticipating;
    private String status;         // PENDING, CONFIRMED, EXPIRED, COMPLETED
    private Timestamp completedAt; // 모임 종료 시각
    private List<Member> participants;  // 참가자 목록 추가

    // Constructors
    public Meeting() {}

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public int getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public double getHostRating() {
        return hostRating;
    }

    public void setHostRating(double hostRating) {
        this.hostRating = hostRating;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public boolean isUserParticipating() {
        return isUserParticipating;
    }

    public void setUserParticipating(boolean userParticipating) {
        isUserParticipating = userParticipating;
    }
    public List<Member> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Member> participants) {
        this.participants = participants;
    }
}