package PSH.dao;

import PSH.model.Member;
import PSH.model.Review;
import PSH.model.Tag;
import PSH.model.UserReviewSummary;
import util.DBUtil;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    
    // 리뷰 생성 (트랜잭션으로 리뷰와 태그를 함께 저장)
    public boolean createReview(Review review, List<Integer> tagIds) {
        String insertReviewSQL = "INSERT INTO reviews (from_user_id, to_user_id, meeting_id, rating) VALUES (?, ?, ?, ?)";
        String insertTagSQL = "INSERT INTO review_tags (review_id, tag_id) VALUES (?, ?)";
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            
            // 1. 리뷰 저장
            try (PreparedStatement pstmt = conn.prepareStatement(insertReviewSQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, review.getFromUserId());
                pstmt.setInt(2, review.getToUserId());
                pstmt.setInt(3, review.getMeetingId());
                pstmt.setInt(4, review.getRating());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("리뷰 생성 실패");
                }
                
                // 생성된 리뷰의 ID 가져오기
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int reviewId = generatedKeys.getInt(1);
                        
                        // 2. 태그 연결
                        try (PreparedStatement tagPstmt = conn.prepareStatement(insertTagSQL)) {
                            for (Integer tagId : tagIds) {
                                tagPstmt.setInt(1, reviewId);
                                tagPstmt.setInt(2, tagId);
                                tagPstmt.addBatch();
                            }
                            tagPstmt.executeBatch();
                        }
                    }
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 특정 모임의 특정 사용자에 대한 리뷰 존재 여부 확인
    public boolean hasReviewed(int fromUserId, int toUserId, int meetingId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE from_user_id = ? AND to_user_id = ? AND meeting_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fromUserId);
            pstmt.setInt(2, toUserId);
            pstmt.setInt(3, meetingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 사용자의 리뷰 요약 정보 조회
    public UserReviewSummary getUserReviewSummary(int userId) {
        UserReviewSummary summary = new UserReviewSummary();
        
        // 1. 평균 별점과 전체 리뷰 수 조회
        String ratingSQL = "SELECT AVG(rating) as avg_rating, COUNT(*) as total_reviews FROM reviews WHERE to_user_id = ?";
        
        // 2. 가장 많이 받은 태그 조회
        String tagSQL = "SELECT t.tag_id, t.tag_name, COUNT(*) as tag_count " +
                       "FROM reviews r " +
                       "JOIN review_tags rt ON r.review_id = rt.review_id " +
                       "JOIN tags t ON rt.tag_id = t.tag_id " +
                       "WHERE r.to_user_id = ? " +
                       "GROUP BY t.tag_id, t.tag_name " +
                       "ORDER BY tag_count DESC " +
                       "LIMIT 5";
        
        // 3. 최근 리뷰 조회
        String reviewSQL = "SELECT r.*, m.nickname as from_user_nickname " +
                         "FROM reviews r " +
                         "JOIN members m ON r.from_user_id = m.id " +
                         "WHERE r.to_user_id = ? " +
                         "ORDER BY r.created_at DESC " +
                         "LIMIT 5";
        
        try (Connection conn = DBUtil.getConnection()) {
            // 1. 평균 별점과 전체 리뷰 수 조회
            try (PreparedStatement pstmt = conn.prepareStatement(ratingSQL)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        summary.setAverageRating(rs.getDouble("avg_rating"));
                        summary.setTotalReviews(rs.getInt("total_reviews"));
                    }
                }
            }

            // 2. 가장 많이 받은 태그 조회
            List<Tag> topTags = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(tagSQL)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Tag tag = new Tag();
                        tag.setTagId(rs.getInt("tag_id"));
                        tag.setTagName(rs.getString("tag_name"));
                        tag.setCount(rs.getInt("tag_count"));
                        topTags.add(tag);
                    }
                }
            }
            summary.setTopTags(topTags);

            // 3. 최근 리뷰 조회
            List<Review> recentReviews = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(reviewSQL)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Review review = new Review();
                        review.setReviewId(rs.getInt("review_id"));
                        review.setFromUserId(rs.getInt("from_user_id"));
                        review.setToUserId(rs.getInt("to_user_id"));
                        review.setMeetingId(rs.getInt("meeting_id"));
                        review.setRating(rs.getInt("rating"));
                        review.setCreatedAt(rs.getTimestamp("created_at"));
                        review.setFromUserNickname(rs.getString("from_user_nickname"));
                        recentReviews.add(review);
                    }
                }
            }
            summary.setRecentReviews(recentReviews);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }

    // 모든 태그 조회
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM tags ORDER BY tag_name";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Tag tag = new Tag();
                tag.setTagId(rs.getInt("tag_id"));
                tag.setTagName(rs.getString("tag_name"));
                tag.setCreatedAt(rs.getTimestamp("created_at"));
                tags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    // 리뷰의 모든 태그 조회
    public List<Tag> getReviewTags(int reviewId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.* FROM tags t " +
                    "JOIN review_tags rt ON t.tag_id = rt.tag_id " +
                    "WHERE rt.review_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reviewId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tag tag = new Tag();
                    tag.setTagId(rs.getInt("tag_id"));
                    tag.setTagName(rs.getString("tag_name"));
                    tag.setCreatedAt(rs.getTimestamp("created_at"));
                    tags.add(tag);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    // 특정 모임의 리뷰 가능한 참가자 목록 조회
    public List<Member> getReviewableMembers(int meetingId, int currentUserId) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT DISTINCT m.* FROM members m " +
                    "JOIN meeting_participants mp ON m.id = mp.member_id " +
                    "WHERE mp.meeting_id = ? " +
                    "AND m.id != ? " +
                    "AND NOT EXISTS (" +
                    "    SELECT 1 FROM reviews r " +
                    "    WHERE r.meeting_id = ? " +
                    "    AND r.from_user_id = ? " +
                    "    AND r.to_user_id = m.id" +
                    ")";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            pstmt.setInt(2, currentUserId);
            pstmt.setInt(3, meetingId);
            pstmt.setInt(4, currentUserId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}