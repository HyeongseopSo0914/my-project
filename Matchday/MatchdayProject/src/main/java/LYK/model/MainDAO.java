package LYK.model;

import java.sql.*;
import java.util.*;

import PSW.model.Notice;
import util.DBUtil;  // util 패키지의 DBUtil 사용

public class MainDAO {
	// MainDAO.java에 추가할 메서드
	public List<Notice> getRecentNoticesWithId() {
	    List<Notice> notices = new ArrayList<>();
	    String sql = "SELECT notice_id, notice_title FROM notice_board ORDER BY notice_regdate DESC LIMIT 5";
	    
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        while (rs.next()) {
	            Notice notice = new Notice();
	            notice.setNoticeId(rs.getInt("notice_id"));
	            notice.setNoticeTitle(rs.getString("notice_title"));
	            notices.add(notice);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return notices;
	}
    // 최근 공지사항 제목을 가져오는 메서드
	public List<String> getRecentNotices() {
	    List<String> noticeTitles = new ArrayList<>();
	    String sql = "SELECT notice_title FROM notice_board WHERE notice_regdate <= NOW() ORDER BY notice_regdate DESC LIMIT 5";

	    try (Connection conn = DBUtil.getConnection()) {
	        System.out.println("DB 연결 성공"); // DB 연결 확인
	        
	        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            System.out.println("실행할 SQL: " + sql); // SQL 쿼리 확인
	            
	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    String title = rs.getString("notice_title");
	                    noticeTitles.add(title);
	                    System.out.println("가져온 공지: " + title); // 각 공지사항 확인
	                }
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("DB 에러 발생: " + e.getMessage()); // 에러 메시지 상세 출력
	        e.printStackTrace();
	    }

	    System.out.println("최종 공지사항 목록: " + noticeTitles); // 최종 결과 확인
	    return noticeTitles;
	}
	
    // 인기 게시글 제목을 가져오는 메서드
    public List<Map<String, Object>> getPopularTitles() {
        List<Map<String, Object>> populars = new ArrayList<>();
        String sql = "SELECT bulletin_title, bulletin_id FROM bulletin_board ORDER BY bulletin_recommend DESC LIMIT 3";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
            	Map<String, Object> popular = new HashMap<>();
            	popular.put("title", rs.getString("bulletin_title"));
            	popular.put("id", rs.getInt("bulletin_id"));
				populars.add(popular);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return populars;
    }
    
    // 최신 게시글 제목을 가져오는 메서드
    public List<Map<String, Object>> getLatestTitles() {
    	List<Map<String, Object>> latests = new ArrayList<>();
        String sql = "SELECT bulletin_title, bulletin_id FROM bulletin_board ORDER BY bulletin_regdate DESC LIMIT 3";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
            	Map<String, Object> lasest = new HashMap<>();
            	lasest.put("title", rs.getString("bulletin_title"));
            	lasest.put("id", rs.getInt("bulletin_id"));
            	latests.add(lasest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return latests;
    }

    // 신고 게시글 제목을 가져오는 메서드
    public List<Map<String, Object>> getReportTitles() {
    	List<Map<String, Object>> reports = new ArrayList<>();
        String sql = "SELECT report_title, report_id FROM report_board ORDER BY report_regdate DESC LIMIT 3";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
            	Map<String, Object> report = new HashMap<>();
            	report.put("title", rs.getString("report_title"));
            	report.put("id", rs.getInt("report_id"));
            	reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }
	
    // 모임 목록을 가져오는 메서드
    private static final String SQL = "SELECT m.title, b.name AS game_name, r.region_name, d.district_name, m.date, m.time, m.max_participants " +
                                      "FROM meetings m " +
                                      "JOIN board_games b ON m.game_id = b.game_id " +
                                      "JOIN regions r ON m.region_id = r.region_id " +
                                      "JOIN districts d ON m.district_id = d.district_id " +
                                      "WHERE m.date >= CURDATE() " +
                                      "ORDER BY m.date ASC, m.time ASC " +
                                      "LIMIT 4;";

    public List<Map<String, Object>> getUpcomingMeetings() {
        List<Map<String, Object>> meetings = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> meeting = new HashMap<>();
                meeting.put("title", rs.getString("title"));
                meeting.put("game_name", rs.getString("game_name"));
                meeting.put("region_name", rs.getString("region_name"));
                meeting.put("district_name", rs.getString("district_name"));
                meeting.put("date", rs.getString("date"));
                meeting.put("time", rs.getString("time"));
                meeting.put("max_participants", rs.getInt("max_participants"));

                meetings.add(meeting);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meetings;
    }
}
