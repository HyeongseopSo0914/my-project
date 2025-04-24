package PSH.model;

import java.util.List;

public class UserReviewSummary {
    private double averageRating;       // 평균 별점
    private int totalReviews;           // 전체 리뷰 수
    private List<Tag> topTags;          // 가장 많이 받은 태그들
    private List<Review> recentReviews;  // 최근 받은 리뷰들

    // Getters and Setters
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    
    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
    
    public List<Tag> getTopTags() { return topTags; }
    public void setTopTags(List<Tag> topTags) { this.topTags = topTags; }
    
    public List<Review> getRecentReviews() { return recentReviews; }
    public void setRecentReviews(List<Review> recentReviews) { this.recentReviews = recentReviews; }
}