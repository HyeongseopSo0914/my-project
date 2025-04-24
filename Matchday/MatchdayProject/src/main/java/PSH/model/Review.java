// Review.java
package PSH.model;

import java.sql.Timestamp;
import java.util.List;

public class Review {
    private int reviewId;
    private int fromUserId;
    private int toUserId;
    private int meetingId;
    private int rating;
    private Timestamp createdAt;
    private String fromUserNickname;  // 리뷰 작성자 닉네임
    private String toUserNickname;    // 리뷰 대상자 닉네임
    private List<Tag> tags;           // 리뷰에 포함된 태그들

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }
    
    public int getFromUserId() { return fromUserId; }
    public void setFromUserId(int fromUserId) { this.fromUserId = fromUserId; }
    
    public int getToUserId() { return toUserId; }
    public void setToUserId(int toUserId) { this.toUserId = toUserId; }
    
    public int getMeetingId() { return meetingId; }
    public void setMeetingId(int meetingId) { this.meetingId = meetingId; }
    
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public String getFromUserNickname() { return fromUserNickname; }
    public void setFromUserNickname(String fromUserNickname) { this.fromUserNickname = fromUserNickname; }
    
    public String getToUserNickname() { return toUserNickname; }
    public void setToUserNickname(String toUserNickname) { this.toUserNickname = toUserNickname; }
    
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
}