package PSH.dao;

import PSH.model.Notification;
import PSH.model.NotificationType;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    
    // 새로운 알림 생성
    public boolean createNotification(int memberId, NotificationType type, String content, Integer relatedId) {
        String sql = "INSERT INTO notifications (member_id, type, content, related_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            pstmt.setString(2, type.name());
            pstmt.setString(3, content);
            if (relatedId != null) {
                pstmt.setInt(4, relatedId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 읽지 않은 알림 개수 조회
    public int getUnreadCount(int memberId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE member_id = ? AND is_read = false";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

 // NotificationDAO.java

    public List<Notification> getRecentNotifications(int memberId) {
        List<Notification> notifications = new ArrayList<>();
        // UNION을 사용하여 안읽은 알림과 최근 알림을 함께 가져오되 중복 제거
        String sql = 
            "(SELECT * FROM notifications WHERE member_id = ? AND is_read = false) " +
            "UNION " +
            "(SELECT * FROM notifications WHERE member_id = ? " +
            "ORDER BY created_at DESC LIMIT 20) " +
            "ORDER BY created_at DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(extractNotificationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 결과가 없는 경우 빈 리스트 반환
        if (notifications.isEmpty()) {
            return notifications;
        }

        return notifications;
    }
    // 알림 읽음 처리
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = true WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, notificationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 모든 알림 읽음 처리
    public boolean markAllAsRead(int memberId) {
        String sql = "UPDATE notifications SET is_read = true WHERE member_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 오래된 알림 삭제 (예: 30일 이상된 알림)
    public boolean deleteOldNotifications(int days) {
        String sql = "DELETE FROM notifications WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, days);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ResultSet에서 Notification 객체 추출
    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setMemberId(rs.getInt("member_id"));
        notification.setType(rs.getString("type"));
        notification.setContent(rs.getString("content"));
        notification.setRelatedId(rs.getObject("related_id", Integer.class));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }
}