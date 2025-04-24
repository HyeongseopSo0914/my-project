package PSH.dao;

import PSH.model.Meeting;
import PSH.model.Member;
import PSH.model.NotificationType;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class MeetingDAO {
	 private NotificationDAO notificationDAO = new NotificationDAO();


    // 모임 생성
    public boolean createMeeting(Meeting meeting) {
        String sql = "INSERT INTO meetings (title, description, game_id, region_id, district_id, date, time, max_participants, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
        		PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
 {

            pstmt.setString(1, meeting.getTitle());
            pstmt.setString(2, meeting.getDescription());
            pstmt.setInt(3, meeting.getGameId());
            pstmt.setInt(4, meeting.getRegionId());
            pstmt.setInt(5, meeting.getDistrictId());
            pstmt.setString(6, meeting.getDate());
            pstmt.setString(7, meeting.getTime());
            pstmt.setInt(8, meeting.getMaxParticipants());
            pstmt.setInt(9, meeting.getCreatedBy());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                // 생성된 모임의 ID 가져오기
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        meeting.setMeetingId(generatedKeys.getInt(1));  // 생성된 모임 ID 설정
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    

    // 모든 모임 목록 조회
 // MeetingDAO.java의 getAllMeetings() 메서드를 다음과 같이 수정

    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        String sql = "SELECT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
                    "r.region_name, d.district_name, m.is_confirmed, " +
                    "COALESCE(AVG(rv.rating), 0) as host_rating, " +  // 평균 별점 계산
                    "COUNT(DISTINCT rv.review_id) as review_count, " + // 리뷰 수 추가
                    "CASE " +
                    "   WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
                    "   WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
                    "   WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
                    "   WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
                    "   WHEN m.is_confirmed THEN 'CONFIRMED' " +
                    "   ELSE 'PENDING' " +
                    "END AS status " +
                    "FROM meetings m " +
                    "JOIN board_games bg ON m.game_id = bg.game_id " +
                    "JOIN members mb ON m.created_by = mb.id " +
                    "JOIN regions r ON m.region_id = r.region_id " +
                    "JOIN districts d ON m.district_id = d.district_id " +
                    "LEFT JOIN reviews rv ON mb.id = rv.to_user_id " + // 리뷰 테이블과 LEFT JOIN
                    "GROUP BY m.meeting_id " +  // 모임별로 그룹화
                    "ORDER BY m.date DESC, m.time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Meeting meeting = new Meeting();
                meeting.setMeetingId(rs.getInt("meeting_id"));
                meeting.setTitle(rs.getString("title"));
                meeting.setDescription(rs.getString("description"));
                meeting.setGameId(rs.getInt("game_id"));
                meeting.setGameName(rs.getString("game_name"));
                meeting.setRegionId(rs.getInt("region_id"));
                meeting.setDistrictId(rs.getInt("district_id"));
                meeting.setRegionName(rs.getString("region_name"));
                meeting.setDistrictName(rs.getString("district_name"));
                meeting.setDate(rs.getString("date"));
                meeting.setTime(rs.getString("time"));
                meeting.setMaxParticipants(rs.getInt("max_participants"));
                meeting.setCurrentParticipants(getCurrentParticipants(rs.getInt("meeting_id")));
                meeting.setCreatedBy(rs.getInt("created_by"));
                meeting.setHostName(rs.getString("host_name"));
                meeting.setCreatedAt(rs.getTimestamp("created_at"));
                meeting.setConfirmed(rs.getBoolean("is_confirmed"));
                meeting.setStatus(rs.getString("status"));
                
                // 모임장의 평균 별점 설정
                double hostRating = rs.getDouble("host_rating");
                int reviewCount = rs.getInt("review_count");
                // 리뷰가 있는 경우에만 평균 별점 설정
                meeting.setHostRating(reviewCount > 0 ? hostRating : 0.0);
                
                meetings.add(meeting);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }
    

 // 시/군/구 기반 검색
    public List<Meeting> searchMeetingsByDistrict(String gameKeyword, int districtId) {
        List<Meeting> meetings = new ArrayList<>();
        String sql = "SELECT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
                     "r.region_name, d.district_name, m.is_confirmed, " +
                     "COALESCE(AVG(rv.rating), 0) as host_rating, " +
                     "COUNT(DISTINCT rv.review_id) as review_count, " +
                     "CASE " +
                     "   WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
                     "   WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
                     "   WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
                     "   WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
                     "   WHEN m.is_confirmed THEN 'CONFIRMED' " +
                     "   ELSE 'PENDING' " +
                     "END AS status " +
                     "FROM meetings m " +
                     "JOIN board_games bg ON m.game_id = bg.game_id " +
                     "JOIN members mb ON m.created_by = mb.id " +
                     "JOIN regions r ON m.region_id = r.region_id " +
                     "JOIN districts d ON m.district_id = d.district_id " +
                     "LEFT JOIN reviews rv ON mb.id = rv.to_user_id " +
                     "WHERE m.district_id = ? " +
                     (gameKeyword != null && !gameKeyword.isEmpty() ? 
                        "AND bg.name LIKE ? " : "") +
                     "GROUP BY m.meeting_id " +
                     "ORDER BY m.date DESC, m.time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, districtId);
            if (gameKeyword != null && !gameKeyword.isEmpty()) {
                pstmt.setString(2, "%" + gameKeyword + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Meeting meeting = extractMeetingFromResultSet(rs);
                    meetings.add(meeting);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    // 시/도 기반 검색
 // MeetingDAO.java

 // 시/도 기반 검색
 public List<Meeting> searchMeetingsByRegion(String gameKeyword, int regionId) {
     List<Meeting> meetings = new ArrayList<>();
     String sql = "SELECT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
                  "r.region_name, d.district_name, m.is_confirmed, " +
                  "COALESCE(AVG(rv.rating), 0) as host_rating, " +
                  "COUNT(DISTINCT rv.review_id) as review_count, " +
                  "CASE " +
                  "   WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
                  "   WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
                  "   WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
                  "   WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
                  "   WHEN m.is_confirmed THEN 'CONFIRMED' " +
                  "   ELSE 'PENDING' " +
                  "END AS status " +
                  "FROM meetings m " +
                  "JOIN board_games bg ON m.game_id = bg.game_id " +
                  "JOIN members mb ON m.created_by = mb.id " +
                  "JOIN regions r ON m.region_id = r.region_id " +
                  "JOIN districts d ON m.district_id = d.district_id " +
                  "LEFT JOIN reviews rv ON mb.id = rv.to_user_id " +
                  "WHERE m.region_id = ? " +
                  (gameKeyword != null && !gameKeyword.isEmpty() ? 
                     "AND bg.name LIKE ? " : "") +
                  "GROUP BY m.meeting_id " +
                  "ORDER BY m.date DESC, m.time DESC";

     try (Connection conn = DBUtil.getConnection();
          PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setInt(1, regionId);
         if (gameKeyword != null && !gameKeyword.isEmpty()) {
             pstmt.setString(2, "%" + gameKeyword + "%");
         }

         try (ResultSet rs = pstmt.executeQuery()) {
             while (rs.next()) {
                 Meeting meeting = extractMeetingFromResultSet(rs);
                 meetings.add(meeting);
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return meetings;
 }

 // 게임명으로만 검색
 public List<Meeting> searchMeetingsByGame(String gameKeyword) {
     List<Meeting> meetings = new ArrayList<>();
     String sql = "SELECT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
                  "r.region_name, d.district_name, m.is_confirmed, " +
                  "COALESCE(AVG(rv.rating), 0) as host_rating, " +
                  "COUNT(DISTINCT rv.review_id) as review_count, " +
                  "CASE " +
                  "   WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
                  "   WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
                  "   WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
                  "   WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
                  "   WHEN m.is_confirmed THEN 'CONFIRMED' " +
                  "   ELSE 'PENDING' " +
                  "END AS status " +
                  "FROM meetings m " +
                  "JOIN board_games bg ON m.game_id = bg.game_id " +
                  "JOIN members mb ON m.created_by = mb.id " +
                  "JOIN regions r ON m.region_id = r.region_id " +
                  "JOIN districts d ON m.district_id = d.district_id " +
                  "LEFT JOIN reviews rv ON mb.id = rv.to_user_id " +
                  "WHERE bg.name LIKE ? " +
                  "GROUP BY m.meeting_id " +
                  "ORDER BY m.date DESC, m.time DESC";

     try (Connection conn = DBUtil.getConnection();
          PreparedStatement pstmt = conn.prepareStatement(sql)) {

         pstmt.setString(1, "%" + gameKeyword + "%");

         try (ResultSet rs = pstmt.executeQuery()) {
             while (rs.next()) {
                 Meeting meeting = extractMeetingFromResultSet(rs);
                 meetings.add(meeting);
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return meetings;
 }

 // ResultSet에서 Meeting 객체 추출하는 메서드 수정
 private Meeting extractMeetingFromResultSet(ResultSet rs) throws SQLException {
	    Meeting meeting = new Meeting();
	    meeting.setMeetingId(rs.getInt("meeting_id"));
	    meeting.setTitle(rs.getString("title"));
	    meeting.setDescription(rs.getString("description"));
	    meeting.setGameId(rs.getInt("game_id"));
	    meeting.setGameName(rs.getString("game_name"));
	    meeting.setRegionId(rs.getInt("region_id"));
	    meeting.setDistrictId(rs.getInt("district_id"));
	    meeting.setRegionName(rs.getString("region_name"));
	    meeting.setDistrictName(rs.getString("district_name"));
	    meeting.setDate(rs.getString("date"));
	    meeting.setTime(rs.getString("time"));
	    meeting.setMaxParticipants(rs.getInt("max_participants"));
	    meeting.setCurrentParticipants(getCurrentParticipants(rs.getInt("meeting_id")));
	    meeting.setCreatedBy(rs.getInt("created_by"));
	    meeting.setHostName(rs.getString("host_name"));
	    meeting.setCreatedAt(rs.getTimestamp("created_at"));
	    meeting.setConfirmed(rs.getBoolean("is_confirmed"));
	    meeting.setStatus(rs.getString("status"));
	    
	    // host_rating을 통계값으로 설정
	    double hostRating = rs.getDouble("host_rating");
	    int reviewCount = rs.getInt("review_count");
	    meeting.setHostRating(reviewCount > 0 ? hostRating : 0.0);
	    
	    return meeting;
	}
    
    public List<Meeting> searchMeetings(String gameKeyword, int regionId) {
        if (regionId != -1) {
            return searchMeetingsByRegion(gameKeyword, regionId);
        } else if (gameKeyword != null && !gameKeyword.trim().isEmpty()) {
            return searchMeetingsByGame(gameKeyword.trim());
        } else {
            return getAllMeetings();
        }
    }

    // 모임의 현재 참가자 수 조회 (추후 meeting_participants 테이블 필요)
    private int getCurrentParticipants(int meetingId) {
        String sql = "SELECT COUNT(*) AS count FROM meeting_participants WHERE meeting_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, meetingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public Meeting getMeetingById(int meetingId) {
        String sql = 
           "SELECT m.*, " +
           "       bg.name AS game_name, " +
           "       mb.nickname AS host_name, " +
           "       r.region_name, " +
           "       d.district_name, " +
           "       m.is_confirmed, " +
           "       COALESCE(AVG(rv.rating), 0) as host_rating, " +
           "       COUNT(DISTINCT rv.review_id) as review_count, " +
           "       CASE " +
           "           WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
           "           WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
           "           WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
           "           WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
           "           WHEN m.is_confirmed THEN 'CONFIRMED' " +
           "           ELSE 'PENDING' " +
           "       END AS status " +
           "FROM meetings m " +
           "JOIN board_games bg ON m.game_id = bg.game_id " +
           "JOIN members mb ON m.created_by = mb.id " +
           "JOIN regions r ON m.region_id = r.region_id " +
           "JOIN districts d ON m.district_id = d.district_id " +
           "LEFT JOIN reviews rv ON mb.id = rv.to_user_id " +
           "WHERE m.meeting_id = ? " +
           "GROUP BY m.meeting_id";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractMeetingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 활성 상태의 모든 모임 조회 (PENDING, CONFIRMED 상태)
    public List<Meeting> getAllActiveMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        String sql = "SELECT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
                    "r.region_name, d.district_name, m.is_confirmed, " +
                    "COALESCE(AVG(rv.rating), 0) as host_rating, " + // 별칭 사용
                    "COUNT(DISTINCT rv.review_id) as review_count, " +
                    "CASE " +
                    "   WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
                    "   WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
                    "   WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
                    "   WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
                    "   WHEN m.is_confirmed THEN 'CONFIRMED' " +
                    "   ELSE 'PENDING' " +
                    "END AS status " +
                    "FROM meetings m " +
                    "JOIN board_games bg ON m.game_id = bg.game_id " +
                    "JOIN members mb ON m.created_by = mb.id " +
                    "JOIN regions r ON m.region_id = r.region_id " +
                    "JOIN districts d ON m.district_id = d.district_id " +
                    "LEFT JOIN reviews rv ON mb.id = rv.to_user_id " +
                    "WHERE m.status IN ('PENDING', 'CONFIRMED') " +
                    "GROUP BY m.meeting_id, m.title, m.description, m.game_id, " +
                    "m.region_id, m.district_id, m.date, m.time, " +
                    "m.max_participants, m.created_by, m.created_at, " +
                    "m.is_confirmed, m.status, bg.name, mb.nickname, " +
                    "r.region_name, d.district_name " +
                    "ORDER BY m.date DESC, m.time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Meeting meeting = extractMeetingFromResultSet(rs);
                meetings.add(meeting);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    // 모임 상태 업데이트
    public boolean updateMeetingStatus(int meetingId, String status) {
        String sql = "UPDATE meetings SET status = ?, " +
                    (status.equals("COMPLETED") ? "completed_at = CURRENT_TIMESTAMP" : "completed_at = NULL") +
                    " WHERE meeting_id = ?";
                    
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, meetingId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 모임의 참가자 ID 목록 조회
    public List<Integer> getParticipantIds(int meetingId) {
        List<Integer> participantIds = new ArrayList<>();
        String sql = "SELECT member_id FROM meeting_participants WHERE meeting_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    participantIds.add(rs.getInt("member_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participantIds;
    }

    public boolean isUserJoined(int meetingId, int userId) {
        String sql = "SELECT COUNT(*) FROM meeting_participants WHERE meeting_id = ? AND member_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            pstmt.setInt(2, userId);
            
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

 // 모임 참가
    public boolean joinMeeting(int meetingId, int userId) {
        if (!isJoinable(meetingId)) {
            return false;
        }

        String sql = "INSERT INTO meeting_participants (meeting_id, member_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            pstmt.setInt(2, userId);
            
            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                // 모임장에게 새 참가자 알림
                Meeting meeting = getMeetingById(meetingId);
                Member newParticipant = getMemberById(userId);
                
                // 모임장에게 알림
                if (meeting.getCreatedBy() != userId) {  // 모임장 자신이 참가하는 경우는 제외
                    notificationDAO.createNotification(
                        meeting.getCreatedBy(),
                        NotificationType.NEW_PARTICIPANT,
                        newParticipant.getNickname() + "님이 모임 '" + meeting.getTitle() + "'에 참여했습니다.",
                        meetingId
                    );
                }
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isJoinable(int meetingId) {
        String sql = "SELECT m.max_participants, COUNT(mp.member_id) as current_participants " +
                     "FROM meetings m " +
                     "LEFT JOIN meeting_participants mp ON m.meeting_id = mp.meeting_id " +
                     "WHERE m.meeting_id = ? " +
                     "GROUP BY m.meeting_id, m.max_participants";
                     
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int maxParticipants = rs.getInt("max_participants");
                    int currentParticipants = rs.getInt("current_participants");
                    return currentParticipants < maxParticipants;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
 // 모임 확정 처리
    public boolean confirmMeeting(int meetingId) {
        String sql = "UPDATE meetings SET status = 'CONFIRMED', is_confirmed = 1 WHERE meeting_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, meetingId);
            int rowsAffected = pstmt.executeUpdate();
            
            // 🔍 SQL 실행 결과 확인
            System.out.println("DEBUG: confirmMeeting SQL 실행됨, 변경된 행 개수 = " + rowsAffected);

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 모임 확정 상태 조회
    public boolean isConfirmed(int meetingId) {
        String sql = "SELECT is_confirmed FROM meetings WHERE meeting_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("is_confirmed");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cancelParticipation(int meetingId, int memberId) {
        String sql = "DELETE FROM meeting_participants WHERE meeting_id = ? AND member_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            pstmt.setInt(2, memberId);
            
            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                // 참가 취소한 사용자 정보 가져오기
                Member cancellingMember = getMemberById(memberId);
                // 모임 정보 가져오기
                Meeting meeting = getMeetingById(meetingId);
                // 모임 참가자 목록 가져오기 (모임장 포함)
                List<Member> participants = getMeetingParticipants(meetingId);

                // 모임장과 다른 참가자들에게 알림 전송
                for (Member participant : participants) {
                    if (participant.getId() != memberId) {  // 취소한 본인 제외
                        notificationDAO.createNotification(
                            participant.getId(),
                            NotificationType.PARTICIPANT_CANCELLED,
                            cancellingMember.getNickname() + "님이 '" + meeting.getTitle() + "' 모임 참가를 취소했습니다.",
                            meetingId
                        );
                    }
                }
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean removeParticipant(int meetingId, int participantId) {
        String sql = "DELETE FROM meeting_participants WHERE meeting_id = ? AND member_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
            pstmt.setInt(1, meetingId);
            pstmt.setInt(2, participantId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Member> getMeetingParticipants(int meetingId) {
        List<Member> participants = new ArrayList<>();
        String sql = "SELECT m.* FROM members m " +
                     "JOIN meeting_participants mp ON m.id = mp.member_id " +
                     "WHERE mp.meeting_id = ?";
                     
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, meetingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url"));
                    participants.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }
    
 // 모임 일정/지역 변경 메서드
    public boolean updateMeetingDetails(int meetingId, String title, String description, String newDate, String newTime, 
            int newRegionId, int newDistrictId, int newGameId, int newMaxParticipants) {
        
        // 변경 전 모임 정보 가져오기
        Meeting oldMeeting = getMeetingById(meetingId);
        
        String sql = "UPDATE meetings SET title = ?, description = ?, date = ?, time = ?, region_id = ?, district_id = ?, game_id = ?, max_participants = ? WHERE meeting_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, newDate);
            pstmt.setString(4, newTime);
            pstmt.setInt(5, newRegionId);
            pstmt.setInt(6, newDistrictId);
            pstmt.setInt(7, newGameId);
            pstmt.setInt(8, newMaxParticipants);
            pstmt.setInt(9, meetingId);

            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                // 변경 후 모임 정보 가져오기
                Meeting newMeeting = getMeetingById(meetingId);
                List<Member> participants = getMeetingParticipants(meetingId);
                
                // 변경된 항목들 확인
                StringBuilder changes = new StringBuilder();
                if (!oldMeeting.getTitle().equals(title)) {
                    changes.append("제목, ");
                }
                if (!oldMeeting.getDescription().equals(description)) {
                    changes.append("설명, ");
                }
                if (!oldMeeting.getDate().equals(newDate) || !oldMeeting.getTime().equals(newTime)) {
                    changes.append("일정, ");
                }
                if (oldMeeting.getRegionId() != newRegionId || oldMeeting.getDistrictId() != newDistrictId) {
                    changes.append("장소, ");
                }
                if (oldMeeting.getGameId() != newGameId) {
                    changes.append("게임, ");
                }
                if (oldMeeting.getMaxParticipants() != newMaxParticipants) {
                    changes.append("최대 인원, ");
                }

                // 마지막 쉼표와 공백 제거
                String changesStr = changes.toString().replaceAll(", $", "");
                
                // 참가자들에게 변경된 항목들을 포함한 알림 전송
                for (Member participant : participants) {
                    if (participant.getId() != newMeeting.getCreatedBy()) {
                        notificationDAO.createNotification(
                            participant.getId(),
                            NotificationType.MEETING_UPDATED,
                            "모임 '" + newMeeting.getTitle() + "'의 " + changesStr + "이(가) 변경되었습니다.",
                            meetingId
                        );
                    }
                }

                // 변경 이력 로그 기록
                logMeetingChange(meetingId, newMeeting.getCreatedBy(), changesStr + " 변경");
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    // 변경 이력 저장 메서드
    public void logMeetingChange(int meetingId, int changedBy, String description) {
        String sql = "INSERT INTO meeting_changes (meeting_id, changed_by, change_description) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, meetingId);
            pstmt.setInt(2, changedBy);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Map<String, Object>> getMeetingChanges(int meetingId) {
        List<Map<String, Object>> changes = new ArrayList<>();
        String sql = "SELECT change_description, change_date FROM meeting_changes WHERE meeting_id = ? ORDER BY change_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, meetingId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> change = new HashMap<>();
                change.put("changeDescription", rs.getString("change_description"));
                change.put("changeDate", rs.getTimestamp("change_date"));
                changes.add(change);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return changes;
    }
 // MeetingDAO.java
    // 모임 삭제 (취소)
    public boolean deleteMeeting(int meetingId) {
        String deleteParticipantsSql = "DELETE FROM meeting_participants WHERE meeting_id = ?";
        String deleteMeetingSql = "DELETE FROM meetings WHERE meeting_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            // 먼저 참가자들에게 알림을 보내기 위해 정보 조회
            Meeting meeting = getMeetingById(meetingId);
            List<Member> participants = getMeetingParticipants(meetingId);

            try (PreparedStatement deleteParticipantsStmt = conn.prepareStatement(deleteParticipantsSql);
                 PreparedStatement deleteMeetingStmt = conn.prepareStatement(deleteMeetingSql)) {

                // 참가자 정보 삭제
                deleteParticipantsStmt.setInt(1, meetingId);
                deleteParticipantsStmt.executeUpdate();

                // 모임 삭제
                deleteMeetingStmt.setInt(1, meetingId);
                int result = deleteMeetingStmt.executeUpdate();

                if (result > 0) {
                    // 참가자들에게 알림 전송
                    for (Member participant : participants) {
                        if (participant.getId() != meeting.getCreatedBy()) {
                            notificationDAO.createNotification(
                                participant.getId(),
                                NotificationType.MEETING_CANCELLED,
                                "참여 중이던 모임 '" + meeting.getTitle() + "'이 취소되었습니다.",
                                meetingId
                            );
                        }
                    }
                }

                conn.commit();
                return result > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 // 모임이 시작 시간이 지났는지 확인
    public boolean hasMeetingStarted(Meeting meeting) {
        // 전달된 시간이 정확한지 콘솔에 출력해서 확인
        System.out.println("DB에서 불러온 날짜와 시간: " + meeting.getDate() + " " + meeting.getTime());

        // 시:분까지만 저장된 경우와 시:분:초까지 저장된 경우를 모두 처리할 수 있도록 수정
        String dateTimeStr = meeting.getDate() + " " + meeting.getTime();
        
        DateTimeFormatter formatter;
        if (dateTimeStr.length() == 16) { // "yyyy-MM-dd HH:mm" 형식
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        } else { // "yyyy-MM-dd HH:mm:ss" 형식
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }

        LocalDateTime meetingDateTime = LocalDateTime.parse(dateTimeStr, formatter);

        return LocalDateTime.now().isAfter(meetingDateTime);
    }

    
 // Member 정보 조회 (Helper method)
    private Member getMemberById(int userId) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

 // 리뷰를 작성해야 하는 완료된 모임 목록 조회
 // MeetingDAO.java의 getCompletedMeetingsNeedingReview 메서드 수정
    public List<Meeting> getCompletedMeetingsNeedingReview(int userId) {
        List<Meeting> meetings = new ArrayList<>();
        String sql = "SELECT DISTINCT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
                    "r.region_name, d.district_name " +
                    "FROM meetings m " +
                    "JOIN board_games bg ON m.game_id = bg.game_id " +
                    "JOIN members mb ON m.created_by = mb.id " +
                    "JOIN regions r ON m.region_id = r.region_id " +
                    "JOIN districts d ON m.district_id = d.district_id " +
                    "JOIN meeting_participants mp ON m.meeting_id = mp.meeting_id " +
                    "WHERE m.status = 'COMPLETED' " +
                    "AND mp.member_id = ? " +
                    "AND m.completed_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
                    "AND EXISTS ( " +
                    "    SELECT 1 FROM meeting_participants mp2 " +
                    "    WHERE mp2.meeting_id = m.meeting_id " +
                    "    AND mp2.member_id != ? " +
                    "    AND NOT EXISTS ( " +
                    "        SELECT 1 FROM reviews rev " +
                    "        WHERE rev.meeting_id = m.meeting_id " +
                    "        AND rev.from_user_id = ? " +
                    "        AND rev.to_user_id = mp2.member_id " +
                    "    ) " +
                    ") " +
                    "ORDER BY m.completed_at DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Meeting meeting = extractMeetingFromResultSet(rs);
                    List<Member> participants = getReviewableParticipants(meeting.getMeetingId(), userId);
                    // 본인을 제외한 아직 리뷰를 작성하지 않은 참가자 목록 설정
                    meeting.setParticipants(participants);
                    meetings.add(meeting);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    // 리뷰 가능한 참가자 목록을 가져오는 새로운 메서드
    private List<Member> getReviewableParticipants(int meetingId, int reviewerId) {
        List<Member> participants = new ArrayList<>();
        String sql = "SELECT m.* FROM members m " +
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
            pstmt.setInt(2, reviewerId);
            pstmt.setInt(3, meetingId);
            pstmt.setInt(4, reviewerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url"));
                    participants.add(member);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }
    private static final int MEETINGS_PER_PAGE = 12;

    // 전체 페이지 수 계산
    public int getTotalPages() {
        String sql = "SELECT COUNT(*) FROM meetings";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                int totalMeetings = rs.getInt(1);
                return (int) Math.ceil((double) totalMeetings / MEETINGS_PER_PAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
 // MeetingDAO.java에 추가
    public List<Meeting> getUpcomingMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        String sql = "SELECT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
                    "r.region_name, d.district_name, m.is_confirmed, " +
                    "COALESCE(AVG(rv.rating), 0) as host_rating, " +
                    "COUNT(DISTINCT rv.review_id) as review_count, " +
                    "CASE " +
                    "   WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
                    "   WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
                    "   WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
                    "   WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
                    "   WHEN m.is_confirmed THEN 'CONFIRMED' " +
                    "   ELSE 'PENDING' " +
                    "END AS status " +
                    "FROM meetings m " +
                    "JOIN board_games bg ON m.game_id = bg.game_id " +
                    "JOIN members mb ON m.created_by = mb.id " +
                    "JOIN regions r ON m.region_id = r.region_id " +
                    "JOIN districts d ON m.district_id = d.district_id " +
                    "LEFT JOIN reviews rv ON mb.id = rv.to_user_id " +
                    "WHERE m.date >= CURDATE() " +
                    "GROUP BY m.meeting_id " +
                    "ORDER BY m.date ASC, m.time ASC " +
                    "LIMIT 4";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Meeting meeting = extractMeetingFromResultSet(rs);
                meetings.add(meeting);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return meetings;
    }

    // 페이지별 모임 목록 조회
    public List<Meeting> getMeetingsByPage(int page) {
        List<Meeting> meetings = new ArrayList<>();
        String sql = "SELECT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
                    "r.region_name, d.district_name, m.is_confirmed, " +
                    "COALESCE(AVG(rv.rating), 0) as host_rating, " +
                    "COUNT(DISTINCT rv.review_id) as review_count, " +
                    "CASE " +
                    "   WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
                    "   WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
                    "   WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
                    "   WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
                    "   WHEN m.is_confirmed THEN 'CONFIRMED' " +
                    "   ELSE 'PENDING' " +
                    "END AS status " +
                    "FROM meetings m " +
                    "JOIN board_games bg ON m.game_id = bg.game_id " +
                    "JOIN members mb ON m.created_by = mb.id " +
                    "JOIN regions r ON m.region_id = r.region_id " +
                    "JOIN districts d ON m.district_id = d.district_id " +
                    "LEFT JOIN reviews rv ON mb.id = rv.to_user_id " +
                    "GROUP BY m.meeting_id " +
                    "ORDER BY m.date DESC, m.time DESC " +
                    "LIMIT ? OFFSET ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, MEETINGS_PER_PAGE);
            pstmt.setInt(2, (page - 1) * MEETINGS_PER_PAGE);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Meeting meeting = extractMeetingFromResultSet(rs);
                    meetings.add(meeting);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    // 검색 조건에 맞는 전체 페이지 수 계산
    public int getSearchTotalPages(String gameKeyword, int regionId, int districtId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM meetings m ");
        sql.append("JOIN board_games bg ON m.game_id = bg.game_id ");
        
        List<Object> params = new ArrayList<>();
        if (gameKeyword != null && !gameKeyword.isEmpty()) {
            sql.append("WHERE bg.name LIKE ? ");
            params.add("%" + gameKeyword + "%");
        }
        
        if (regionId != -1) {
            sql.append(params.isEmpty() ? "WHERE " : "AND ");
            sql.append("m.region_id = ? ");
            params.add(regionId);
        }
        
        if (districtId != -1) {
            sql.append(params.isEmpty() ? "WHERE " : "AND ");
            sql.append("m.district_id = ? ");
            params.add(districtId);
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalMeetings = rs.getInt(1);
                    return (int) Math.ceil((double) totalMeetings / MEETINGS_PER_PAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 검색 조건에 맞는 페이지별 모임 목록 조회
    public List<Meeting> searchMeetingsByPage(String gameKeyword, int regionId, int districtId, int page) {
        List<Meeting> meetings = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT m.*, bg.name AS game_name, mb.nickname AS host_name, " +
            "r.region_name, d.district_name, m.is_confirmed, " +
            "COALESCE(AVG(rv.rating), 0) as host_rating, " +
            "COUNT(DISTINCT rv.review_id) as review_count, " +
            "CASE " +
            "   WHEN m.status = 'COMPLETED' THEN 'COMPLETED' " +
            "   WHEN m.status = 'EXPIRED' THEN 'EXPIRED' " +
            "   WHEN m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'COMPLETED' " +
            "   WHEN NOT m.is_confirmed AND CONCAT(m.date, ' ', m.time) < NOW() THEN 'EXPIRED' " +
            "   WHEN m.is_confirmed THEN 'CONFIRMED' " +
            "   ELSE 'PENDING' " +
            "END AS status " +
            "FROM meetings m " +
            "JOIN board_games bg ON m.game_id = bg.game_id " +
            "JOIN members mb ON m.created_by = mb.id " +
            "JOIN regions r ON m.region_id = r.region_id " +
            "JOIN districts d ON m.district_id = d.district_id " +
            "LEFT JOIN reviews rv ON mb.id = rv.to_user_id "
        );

        List<Object> params = new ArrayList<>();
        if (gameKeyword != null && !gameKeyword.isEmpty()) {
            sql.append("WHERE bg.name LIKE ? ");
            params.add("%" + gameKeyword + "%");
        }
        
        if (regionId != -1) {
            sql.append(params.isEmpty() ? "WHERE " : "AND ");
            sql.append("m.region_id = ? ");
            params.add(regionId);
        }
        
        if (districtId != -1) {
            sql.append(params.isEmpty() ? "WHERE " : "AND ");
            sql.append("m.district_id = ? ");
            params.add(districtId);
        }

        sql.append("GROUP BY m.meeting_id ");
        sql.append("ORDER BY m.date DESC, m.time DESC ");
        sql.append("LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            for (Object param : params) {
                pstmt.setObject(paramIndex++, param);
            }
            
            pstmt.setInt(paramIndex++, MEETINGS_PER_PAGE);
            pstmt.setInt(paramIndex, (page - 1) * MEETINGS_PER_PAGE);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Meeting meeting = extractMeetingFromResultSet(rs);
                    meetings.add(meeting);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }
}


