package PSW.dao;

import java.sql.*;
import java.util.*;
import PSW.model.*;
import util.DBUtil;

public class NoticeDAO {
    private PreparedStatement psmt;
    private ResultSet rs;

    // 공지사항 목록 가져오기
    public List<Notice> getAllNotices() {
        List<Notice> noticeList = new ArrayList<>();
        String sql = "SELECT * FROM notice_board ORDER BY notice_regdate DESC";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            psmt = conn.prepareStatement(sql);
            rs = psmt.executeQuery();
            while (rs.next()) {
                int notice_id = rs.getInt("notice_id");
                String notice_title = rs.getString("notice_title");
                String notice_content = rs.getString("notice_content");
                Timestamp notice_regdate = rs.getTimestamp("notice_regdate");
                noticeList.add(new Notice(notice_id, notice_title, notice_content, notice_regdate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(psmt, conn, rs); // 자원 해제
        }
        return noticeList;
    }

    // 공지사항 상세보기
    public Notice getNoticeById(int notice_id) {
        Notice notice = null;
        String sql = "SELECT * FROM notice_board WHERE notice_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            psmt = conn.prepareStatement(sql);
            psmt.setInt(1, notice_id);
            rs = psmt.executeQuery();
            if (rs.next()) {
                String notice_title = rs.getString("notice_title");
                String notice_content = rs.getString("notice_content");
                Timestamp notice_regdate = rs.getTimestamp("notice_regdate");
                notice = new Notice(notice_id, notice_title, notice_content, notice_regdate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(psmt, conn, rs); // 자원 해제
        }
        return notice;
    }
    
	 // 검색된 공지사항 목록 가져오기
	    public List<Notice> searchNotices(String keyword) {
	        List<Notice> noticeList = new ArrayList<>();
	        String sql = "SELECT * FROM notice_board WHERE notice_title LIKE ? OR notice_content LIKE ? ORDER BY notice_regdate DESC";
	        Connection conn = null;
	        try {
	            conn = DBUtil.getConnection();
	            psmt = conn.prepareStatement(sql);
	            psmt.setString(1, "%" + keyword + "%");
	            psmt.setString(2, "%" + keyword + "%");
	            rs = psmt.executeQuery();
	            while (rs.next()) {
	                int notice_id = rs.getInt("notice_id");
	                String notice_title = rs.getString("notice_title");
	                String notice_content = rs.getString("notice_content");
	                Timestamp notice_regdate = rs.getTimestamp("notice_regdate");
	                noticeList.add(new Notice(notice_id, notice_title, notice_content, notice_regdate));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	            DBUtil.close(psmt, conn, rs); // 자원 해제
	        }
	        return noticeList;
	    }
	    
	   
	     


	    
	 
}