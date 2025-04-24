package PSH.dao;

import PSH.model.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;

public class MemberDAO {
    // 🔧 SQL 쿼리 수정: salt 컬럼 추가
	private static final String INSERT_MEMBER_SQL = 
		    "INSERT INTO members (email, password, nickname, salt, terms_agreed, terms_agreed_at, " +
		    "privacy_agreed, privacy_agreed_at, marketing_agreed, marketing_agreed_at, profile_image_url, email_verified) " +
		    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		

    
    private static final String FIND_BY_EMAIL_SQL = 
        "SELECT * FROM members WHERE email = ?";
    
    private static final String FIND_BY_ID_SQL = 
        "SELECT * FROM members WHERE id = ?";

    public int insertMember(Member member) {
        System.out.println("📌 insertMember 실행! 이메일: " + member.getEmail());

        // 먼저 이메일 인증 상태 확인
        String checkVerificationSql = "SELECT verified FROM email_verification WHERE email = ?";

        boolean isEmailVerified = false;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkVerificationSql)) {

            checkStmt.setString(1, member.getEmail());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    isEmailVerified = rs.getBoolean("verified");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_MEMBER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, member.getEmail());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getNickname());
            pstmt.setString(4, member.getSalt());
            pstmt.setBoolean(5, member.isTermsAgreed());
            pstmt.setTimestamp(6, member.getTermsAgreedAt());
            pstmt.setBoolean(7, member.isPrivacyAgreed());
            pstmt.setTimestamp(8, member.getPrivacyAgreedAt());
            pstmt.setBoolean(9, member.isMarketingAgreed());
            pstmt.setTimestamp(10, member.getMarketingAgreedAt());
            pstmt.setString(11, "https://res.cloudinary.com/dnjqljait/image/upload/v1736337453/icononly_cytjxl.png");
            pstmt.setBoolean(12, isEmailVerified);  // 🔥 이메일 인증 상태 반영

            int affected = pstmt.executeUpdate();
            System.out.println("📌 SQL 실행 결과: " + affected + " rows affected");

            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int memberId = rs.getInt(1);
                        System.out.println("✅ 회원 가입 완료! ID: " + memberId);
                        return memberId;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ SQL 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }


    public Member findByEmail(String email) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractMemberFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Member findById(int id) {
        String sql = "SELECT m.*, " +
                     "(SELECT GROUP_CONCAT(bg.name SEPARATOR ', ') " +
                     " FROM user_favorite_games ufg " +
                     " JOIN board_games bg ON ufg.game_id = bg.game_id " +
                     " WHERE ufg.member_id = m.id) AS favorite_games, " +
                     "(CASE WHEN f.user1_id IS NOT NULL THEN 'FRIENDS' ELSE 'NONE' END) AS friend_status " +
                     "FROM members m " +
                     "LEFT JOIN friendships f ON (f.user1_id = m.id OR f.user2_id = m.id) " +
                     "WHERE m.id = ? " +
                     "GROUP BY m.id";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Member member = new Member();
                    member.setId(rs.getInt("id"));
                    member.setEmail(rs.getString("email"));
                    member.setNickname(rs.getString("nickname"));
                    member.setProfileImageUrl(rs.getString("profile_image_url") != null ? rs.getString("profile_image_url") : "");
                    member.setFavoriteGames(rs.getString("favorite_games") != null ? rs.getString("favorite_games") : "");
                    member.setFriendStatus(rs.getString("friend_status") != null ? rs.getString("friend_status") : "NONE");
                    member.setTermsAgreed(rs.getBoolean("terms_agreed"));
                    member.setTermsAgreedAt(rs.getTimestamp("terms_agreed_at"));

                    return member;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // ResultSet에서 Member 객체를 추출하는 유틸리티 메소드
    private Member extractMemberFromResultSet(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setEmail(rs.getString("email"));
        member.setPassword(rs.getString("password"));
        member.setNickname(rs.getString("nickname"));
        member.setSalt(rs.getString("salt"));
        member.setTermsAgreed(rs.getBoolean("terms_agreed"));
        member.setTermsAgreedAt(rs.getTimestamp("terms_agreed_at"));
        member.setPrivacyAgreed(rs.getBoolean("privacy_agreed"));
        member.setPrivacyAgreedAt(rs.getTimestamp("privacy_agreed_at"));
        member.setMarketingAgreed(rs.getBoolean("marketing_agreed"));
        member.setMarketingAgreedAt(rs.getTimestamp("marketing_agreed_at"));
        return member;
    }
    public boolean updateProfileImage(int memberId, String imageUrl) {
        String sql = "UPDATE members SET profile_image_url = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, imageUrl);
            pstmt.setInt(2, memberId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        // 특정 지역의 모든 사용자 조회
        public List<Member> getMembersByDistrict(int districtId) {
            String sql = "SELECT DISTINCT m.* FROM members m " +
                        "JOIN member_districts md ON m.id = md.member_id " +
                        "WHERE md.district_id = ?";
            List<Member> members = new ArrayList<>();
            
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, districtId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Member member = extractMemberFromResultSet(rs);
                        members.add(member);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return members;
        }
        
        public boolean isEmailExists(String email) {
            String sql = "SELECT COUNT(*) FROM members WHERE email = ?";
            
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, email);
                
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
        

        // 특정 게임을 관심 게임으로 등록한 사용자들 조회
        public List<Member> getMembersByFavoriteGame(int gameId) {
            String sql = "SELECT DISTINCT m.* FROM members m " +
                        "JOIN user_favorite_games ufg ON m.id = ufg.member_id " +
                        "WHERE ufg.game_id = ?";
            List<Member> members = new ArrayList<>();
            
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, gameId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Member member = extractMemberFromResultSet(rs);
                        members.add(member);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return members;
        }
        public void updatePassword(int userId, String hashedPassword, String salt) {
            String sql = "UPDATE members SET password = ?, salt = ? WHERE id = ?";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, hashedPassword);
                pstmt.setString(2, salt);
                pstmt.setInt(3, userId);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ 비밀번호 및 Salt 업데이트 성공!");
                } else {
                    System.out.println("❌ 비밀번호 업데이트 실패!");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        public boolean isNicknameExists(String nickname) {
            String sql = "SELECT COUNT(*) FROM members WHERE nickname = ?";
            
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, nickname);
                
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
        
        public void deleteMember(int memberId) throws SQLException {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                conn = DBUtil.getConnection();
                conn.setAutoCommit(false);  // 트랜잭션 시작

                // 1. 친구 요청 삭제
                pstmt = conn.prepareStatement("DELETE FROM friend_requests WHERE sender_id = ? OR receiver_id = ?");
                pstmt.setInt(1, memberId);
                pstmt.setInt(2, memberId);
                int friendRequestsDeleted = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 친구 요청 수: " + friendRequestsDeleted);

                // 2. 친구 관계 삭제
                pstmt = conn.prepareStatement("DELETE FROM friendships WHERE user1_id = ? OR user2_id = ?");
                pstmt.setInt(1, memberId);
                pstmt.setInt(2, memberId);
                int friendshipsDeleted = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 친구 관계 수: " + friendshipsDeleted);

                // 3-1. 모임과 관련된 리뷰의 태그 삭제
                pstmt = conn.prepareStatement(
                    "DELETE FROM review_tags WHERE review_id IN " +
                    "(SELECT r.review_id FROM reviews r " +
                    "INNER JOIN meetings m ON r.meeting_id = m.meeting_id " +
                    "WHERE m.created_by = ?)"
                );
                pstmt.setInt(1, memberId);
                int meetingReviewTagsDeleted = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 모임 리뷰 태그 수: " + meetingReviewTagsDeleted);

                // 3-2. 받은 리뷰의 태그 삭제
                pstmt = conn.prepareStatement(
                    "DELETE FROM review_tags WHERE review_id IN " +
                    "(SELECT review_id FROM reviews WHERE to_user_id = ?)"
                );
                pstmt.setInt(1, memberId);
                int reviewTagsDeleted = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 받은 리뷰 태그 수: " + reviewTagsDeleted);

                // 4-1. 모임과 관련된 리뷰 삭제
                pstmt = conn.prepareStatement(
                    "DELETE FROM reviews WHERE meeting_id IN " +
                    "(SELECT meeting_id FROM meetings WHERE created_by = ?)"
                );
                pstmt.setInt(1, memberId);
                int meetingReviewsDeleted = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 모임 리뷰 수: " + meetingReviewsDeleted);

                // 4-2. 받은 리뷰 삭제
                pstmt = conn.prepareStatement("DELETE FROM reviews WHERE to_user_id = ?");
                pstmt.setInt(1, memberId);
                int reviewsDeleted = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 받은 리뷰 수: " + reviewsDeleted);

                // 5. 모임 참가 삭제
                pstmt = conn.prepareStatement("DELETE FROM meeting_participants WHERE member_id = ?");
                pstmt.setInt(1, memberId);
                pstmt.executeUpdate();

                // 6. 회원이 생성한 모임의 참가자 정보 삭제
                pstmt = conn.prepareStatement(
                    "DELETE FROM meeting_participants WHERE meeting_id IN " +
                    "(SELECT meeting_id FROM meetings WHERE created_by = ?)"
                );
                pstmt.setInt(1, memberId);
                pstmt.executeUpdate();

                // 7. 모임 변경 이력 삭제
                pstmt = conn.prepareStatement("DELETE FROM meeting_changes WHERE changed_by = ?");
                pstmt.setInt(1, memberId);
                pstmt.executeUpdate();

                // 8. 회원이 생성한 모임 삭제
                pstmt = conn.prepareStatement("DELETE FROM meetings WHERE created_by = ?");
                pstmt.setInt(1, memberId);
                pstmt.executeUpdate();

                // 9. 활동 지역 삭제
                pstmt = conn.prepareStatement("DELETE FROM member_districts WHERE member_id = ?");
                pstmt.setInt(1, memberId);
                int deletedDistricts = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 활동 지역 수: " + deletedDistricts);

                // 10. 알림 삭제
                pstmt = conn.prepareStatement("DELETE FROM notifications WHERE member_id = ?");
                pstmt.setInt(1, memberId);
                int deletedNotifications = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 알림 수: " + deletedNotifications);

                // 11. 즐겨찾기한 게임 삭제
                pstmt = conn.prepareStatement("DELETE FROM user_favorite_games WHERE member_id = ?");
                pstmt.setInt(1, memberId);
                int deletedFavorites = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 즐겨찾기 게임 수: " + deletedFavorites);

                // 12. 회원 정보 삭제
                pstmt = conn.prepareStatement("DELETE FROM members WHERE id = ?");
                pstmt.setInt(1, memberId);
                int membersDeleted = pstmt.executeUpdate();
                System.out.println("🔍 삭제된 회원 정보 수: " + membersDeleted);

                if (membersDeleted == 0) {
                    throw new SQLException("회원 정보 삭제 실패: 해당 ID의 회원이 존재하지 않습니다.");
                }

                conn.commit();  // 트랜잭션 커밋
                System.out.println("✅ 모든 데이터 삭제 및 트랜잭션 커밋 완료.");
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();  // 오류 발생 시 롤백
                    System.out.println("❌ 트랜잭션 롤백: " + e.getMessage());
                }
                throw e;
            } finally {
                DBUtil.close(pstmt);
                DBUtil.close(conn);
            }
        }
        
        public void saveAuthCode(String email, String authCode) {
            String sql = "INSERT INTO email_verification (email, auth_code, created_at) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE auth_code = ?, created_at = ?";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                
                stmt.setString(1, email);
                stmt.setString(2, authCode);
                stmt.setTimestamp(3, currentTime);
                stmt.setString(4, authCode);
                stmt.setTimestamp(5, currentTime);
                
                System.out.println("📝 Saving auth code - Email: " + email);
                System.out.println("📝 Saving auth code - Code: " + authCode);
                System.out.println("📝 Saving auth code - Time: " + currentTime);
                
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("❌ Error saving auth code: " + e.getMessage());
                e.printStackTrace();
            }
        }
        // 인증 코드 검증
        public boolean checkAuthCodeInDatabase(String email, String authCode) {
            String selectSql = "SELECT auth_code, created_at FROM email_verification " +
                               "WHERE email = ? ORDER BY created_at DESC LIMIT 1";
            
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

                selectStmt.setString(1, email);

                System.out.println("📧 Checking auth code - Email: " + email);
                System.out.println("🔑 Checking auth code - Input Code: " + authCode);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        String storedCode = rs.getString("auth_code");
                        Timestamp createdAt = rs.getTimestamp("created_at");
                        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                        System.out.println("💾 Stored Code: " + storedCode);
                        System.out.println("⏰ Created At: " + createdAt);
                        System.out.println("⏰ Current Time: " + currentTime);

                        long minutesDifference = (currentTime.getTime() - createdAt.getTime()) / (60 * 1000);
                        System.out.println("⏱️ Time difference in minutes: " + minutesDifference);

                        // 3분 이내이고 인증 코드가 일치하는 경우
                        if (minutesDifference <= 3 && storedCode.equals(authCode)) {
                            System.out.println("✅ Verification successful!");

                            // ✅ `email_verification` 테이블의 `verified` 값 업데이트
                            String updateVerificationSql = "UPDATE email_verification SET verified = 1 WHERE email = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateVerificationSql)) {
                                updateStmt.setString(1, email);
                                updateStmt.executeUpdate();
                            }

                            return true;  // 인증 성공
                        } else {
                            System.out.println("❌ Verification failed - " + 
                                (minutesDifference > 3 ? "Time expired" : "Code mismatch"));
                            return false;
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("❌ SQL Error: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }


        // 이메일 인증 상태 업데이트
        public boolean updateEmailVerifiedStatus(String email) {
            String sql = "UPDATE members SET email_verified = true WHERE email = ?";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, email);
                int rowsUpdated = stmt.executeUpdate();

                System.out.println("📌 Email verification update result: " + rowsUpdated);
                System.out.println("📌 Email being updated: " + email);

                return rowsUpdated > 0; // 업데이트 성공 여부 반환
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }


    }