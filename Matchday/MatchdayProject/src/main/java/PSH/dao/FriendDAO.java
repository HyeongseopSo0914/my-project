package PSH.dao;

import PSH.model.Member;
import PSH.model.NotificationType;
import PSH.model.FriendRequest;
import util.DBUtil;

import java.sql.*;
import java.util.*;

public class FriendDAO {
	private NotificationDAO notificationDAO = new NotificationDAO();
	// FriendDAO.java의 sendFriendRequest 메서드 수정
	public boolean sendFriendRequest(int senderId, int receiverId) {
	    String sql = "INSERT INTO friend_requests (sender_id, receiver_id, status) VALUES (?, ?, 'PENDING')";
	    
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setInt(1, senderId);
	        pstmt.setInt(2, receiverId);
	        
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
    
    // 친구 요청 수락/거절
    public boolean updateFriendRequestStatus(int requestId, String status) {
        String sql = "UPDATE friend_requests SET status = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
                // 요청 정보 조회
                FriendRequest request = getFriendRequestById(requestId);
                if (request == null) {
                    return false;
                }
            try {
                // 상태 업데이트
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, status);
                    pstmt.setInt(2, requestId);
                    pstmt.executeUpdate();
                }
                
                // 요청이 수락된 경우 friendship 테이블에 추가
                // 요청이 수락된 경우
                if (status.equals("ACCEPTED")) {
                    addFriendship(conn, request.getSenderId(), request.getReceiverId());
                    
                    // 수락 알림 생성
                    notificationDAO.createNotification(
                        request.getSenderId(),
                        NotificationType.FRIEND_ACCEPTED,
                        "친구 요청이 수락되었습니다.",
                        request.getReceiverId()
                    );
                } else if (status.equals("REJECTED")) {
                    // 거절 알림 생성
                    notificationDAO.createNotification(
                        request.getSenderId(),
                        NotificationType.FRIEND_REJECTED,
                        "친구 요청이 거절되었습니다.",
                        request.getReceiverId()
                    );
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    // 받은 친구 요청 목록 조회
    public List<FriendRequest> getReceivedRequests(int userId) {
    	// FriendDAO.java의 getReceivedRequests 메소드의 SQL 쿼리 수정
    	String sql = "SELECT fr.*, " +
    	            "m_sender.nickname as sender_name, " +
    	            "m_sender.profile_image_url as sender_profile_url, " +
    	            // 활동지역 정보
    	            "GROUP_CONCAT(DISTINCT CONCAT(r.region_name, ' ', d.district_name) SEPARATOR ', ') as sender_regions, " +
    	            // 관심게임 정보
    	            "(SELECT GROUP_CONCAT(DISTINCT bg.name SEPARATOR ', ') " +
    	            " FROM user_favorite_games ufg " +
    	            " JOIN board_games bg ON ufg.game_id = bg.game_id " +
    	            " WHERE ufg.member_id = fr.sender_id) as sender_games " +
    	            "FROM friend_requests fr " +
    	            "JOIN members m_sender ON fr.sender_id = m_sender.id " +
    	            // 활동지역 조인
    	            "LEFT JOIN member_districts md ON fr.sender_id = md.member_id " +
    	            "LEFT JOIN districts d ON md.district_id = d.district_id " +
    	            "LEFT JOIN regions r ON d.region_id = r.region_id " +
    	            "WHERE fr.receiver_id = ? AND fr.status = 'PENDING' " +
    	            "GROUP BY fr.id, fr.sender_id, fr.receiver_id, fr.message, fr.status, " +
    	            "fr.created_at, m_sender.nickname, m_sender.profile_image_url";
                        
        List<FriendRequest> requests = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    FriendRequest request = new FriendRequest();
                    request.setId(rs.getInt("id"));
                    request.setSenderId(rs.getInt("sender_id"));
                    request.setReceiverId(rs.getInt("receiver_id"));
                    request.setMessage(rs.getString("message"));
                    request.setStatus(rs.getString("status"));
                    request.setCreatedAt(rs.getTimestamp("created_at"));
                    request.setSenderName(rs.getString("sender_name"));
                    request.setSenderProfileUrl(rs.getString("sender_profile_url"));
                    // 새로운 정보 설정
                    request.setSenderRegions(rs.getString("sender_regions"));
                    request.setSenderGames(rs.getString("sender_games"));
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return requests;
    }
    
    // 보낸 친구 요청 목록 조회
    public List<FriendRequest> getSentRequests(int userId) {
        String sql = "SELECT fr.*, " +
                    "m_receiver.nickname as receiver_name, m_receiver.profile_image_url as receiver_profile_url " +
                    "FROM friend_requests fr " +
                    "JOIN members m_receiver ON fr.receiver_id = m_receiver.id " +
                    "WHERE fr.sender_id = ? AND fr.status = 'PENDING'";
                    
        List<FriendRequest> requests = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    FriendRequest request = new FriendRequest();
                    request.setId(rs.getInt("id"));
                    request.setSenderId(rs.getInt("sender_id"));
                    request.setReceiverId(rs.getInt("receiver_id"));
                    request.setMessage(rs.getString("message"));
                    request.setStatus(rs.getString("status"));
                    request.setCreatedAt(rs.getTimestamp("created_at"));
                    request.setReceiverName(rs.getString("receiver_name"));
                    request.setReceiverProfileUrl(rs.getString("receiver_profile_url"));
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return requests;
    }
    
    // 친구 목록 조회
    public List<Member> getFriends(int userId) {
        String sql = "SELECT m.* FROM members m " +
                    "JOIN friendships f ON (f.user1_id = m.id OR f.user2_id = m.id) " +
                    "WHERE (f.user1_id = ? OR f.user2_id = ?) AND m.id != ?";
                    
        List<Member> friends = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member friend = new Member();
                    friend.setId(rs.getInt("id"));
                    friend.setNickname(rs.getString("nickname"));
                    friend.setProfileImageUrl(rs.getString("profile_image_url"));
                    friends.add(friend);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return friends;
    }
    
 // FriendDAO 내 검색 메서드 수정
    public List<Member> searchFriendsByRegion(int regionId) {
        String sql = "SELECT DISTINCT m.*, " +
                     "(SELECT GROUP_CONCAT(bg.name SEPARATOR ', ') " +
                     " FROM user_favorite_games uf " +
                     " JOIN board_games bg ON uf.game_id = bg.game_id " +
                     " WHERE uf.member_id = m.id) AS favorite_games " +
                     "FROM members m " +
                     "LEFT JOIN member_districts md ON m.id = md.member_id " +
                     "LEFT JOIN districts d ON md.district_id = d.district_id " +
                     "WHERE d.region_id = ?";

        List<Member> members = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, regionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url"));
                    member.setFavoriteGames(rs.getString("favorite_games"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    public List<Member> searchFriendsByDistrict(int districtId) {
        String sql = "SELECT DISTINCT m.*, " +
                     "(SELECT GROUP_CONCAT(bg.name SEPARATOR ', ') " +
                     " FROM user_favorite_games uf " +
                     " JOIN board_games bg ON uf.game_id = bg.game_id " +
                     " WHERE uf.member_id = m.id) AS favorite_games " +
                     "FROM members m " +
                     "JOIN member_districts md ON m.id = md.member_id " +
                     "WHERE md.district_id = ?";
                         
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, districtId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url"));
                    member.setFavoriteGames(rs.getString("favorite_games"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }

    public List<Member> searchFriendsByGameAndRegion(int regionId, String gameName) {
        String sql = "SELECT DISTINCT m.*, " +
                     "(SELECT GROUP_CONCAT(bg2.name) " +
                     " FROM user_favorite_games uf2 " +
                     " JOIN board_games bg2 ON uf2.game_id = bg2.game_id " +
                     " WHERE uf2.member_id = m.id) as favorite_games " +
                     "FROM members m " +
                     "JOIN member_districts md ON m.id = md.member_id " +
                     "JOIN districts d ON md.district_id = d.district_id " +
                     "JOIN user_favorite_games uf ON m.id = uf.member_id " +
                     "JOIN board_games bg ON uf.game_id = bg.game_id " +
                     "WHERE d.region_id = ? " +
                     "AND bg.name LIKE ? " +
                     "GROUP BY m.id";
                             
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, regionId);
            pstmt.setString(2, "%" + gameName + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url"));
                    member.setFavoriteGames(rs.getString("favorite_games"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }

    public List<Member> searchFriendsByGameAndDistrict(int districtId, String gameName) {
        String sql = "SELECT DISTINCT m.*, " +
                     "(SELECT GROUP_CONCAT(bg2.name) " +
                     " FROM user_favorite_games uf2 " +
                     " JOIN board_games bg2 ON uf2.game_id = bg2.game_id " +
                     " WHERE uf2.member_id = m.id) as favorite_games " +
                     "FROM members m " +
                     "JOIN member_districts md ON m.id = md.member_id " +
                     "JOIN user_favorite_games uf ON m.id = uf.member_id " +
                     "JOIN board_games bg ON uf.game_id = bg.game_id " +
                     "WHERE md.district_id = ? " +
                     "AND bg.name LIKE ? " +
                     "GROUP BY m.id";
                             
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, districtId);
            pstmt.setString(2, "%" + gameName + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url"));
                    member.setFavoriteGames(rs.getString("favorite_games"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    public List<Member> searchFriendsByGame(String gameName) {
        String sql = "SELECT DISTINCT m.*, " +
                     "(SELECT GROUP_CONCAT(bg2.name) " +
                     " FROM user_favorite_games uf2 " +
                     " JOIN board_games bg2 ON uf2.game_id = bg2.game_id " +
                     " WHERE uf2.member_id = m.id) as favorite_games " +
                     "FROM members m " +
                     "JOIN user_favorite_games uf ON m.id = uf.member_id " +
                     "JOIN board_games bg ON uf.game_id = bg.game_id " +
                     "WHERE bg.name LIKE ? " +
                     "GROUP BY m.id";
                             
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + gameName + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url"));
                    member.setFavoriteGames(rs.getString("favorite_games"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return members;
    }
    // 친구 요청 조회 (내부 사용)
    public FriendRequest getFriendRequestById(int requestId) {
        String sql = "SELECT * FROM friend_requests WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, requestId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    FriendRequest request = new FriendRequest();
                    request.setId(rs.getInt("id"));
                    request.setSenderId(rs.getInt("sender_id"));
                    request.setReceiverId(rs.getInt("receiver_id"));
                    request.setMessage(rs.getString("message"));
                    request.setStatus(rs.getString("status"));
                    request.setCreatedAt(rs.getTimestamp("created_at"));
                    return request;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // 친구 관계 추가 (내부 사용)
    private void addFriendship(Connection conn, int user1Id, int user2Id) throws SQLException {
        String sql = "INSERT INTO friendships (user1_id, user2_id) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Math.min(user1Id, user2Id));  // 항상 작은 ID가 user1_id가 되도록
            pstmt.setInt(2, Math.max(user1Id, user2Id));
            pstmt.executeUpdate();
        }
    }
    
    // 이미 친구인지 확인
    public boolean areFriends(int user1Id, int user2Id) {
        String sql = "SELECT COUNT(*) FROM friendships " +
                    "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
                    
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, user1Id);
            pstmt.setInt(2, user2Id);
            pstmt.setInt(3, user2Id);
            pstmt.setInt(4, user1Id);
            
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
    public boolean deleteFriendship(int userId, int friendId) {
        String sql = "DELETE FROM friendships WHERE " +
                    "(user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Math.min(userId, friendId));
            pstmt.setInt(2, Math.max(userId, friendId));
            pstmt.setInt(3, Math.max(userId, friendId));
            pstmt.setInt(4, Math.min(userId, friendId));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void notifyFriendsAboutNewMeeting(int creatorId, int meetingId, String meetingTitle) {
        String sql = "SELECT user2_id as friend_id FROM friendships WHERE user1_id = ? " +
                    "UNION " +
                    "SELECT user1_id as friend_id FROM friendships WHERE user2_id = ?";
                    
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, creatorId);
            pstmt.setInt(2, creatorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int friendId = rs.getInt("friend_id");
                    notificationDAO.createNotification(
                        friendId,
                        NotificationType.FRIEND_NEW_MEETING,
                        "친구가 새로운 모임 '" + meetingTitle + "'을 생성했습니다.",
                        meetingId
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // 친구 요청 상태 확인
    public String getFriendRequestStatus(int senderId, int receiverId) {
        String sql = "SELECT status FROM friend_requests " +
                    "WHERE sender_id = ? AND receiver_id = ? " +
                    "AND status != 'REJECTED' " +  // REJECTED 상태는 제외
                    "ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // REJECTED이거나 요청이 없는 경우 null 반환
    }


}