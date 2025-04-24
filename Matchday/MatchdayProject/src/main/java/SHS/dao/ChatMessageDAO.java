package SHS.dao;

import SHS.model.ChatMessage;
import util.DBUtil;

import java.sql.*;
import java.util.*;

public class ChatMessageDAO {
   
	public static void saveMessage(String nickname, String message, int writerId, int meetingId) {
	    String sql = "INSERT INTO chat_messages (nickname, message, sent_time, writer_id, reg_date, meeting_id) VALUES (?, ?, ?, ?, NOW(), ?)";
	    
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, nickname);
	        pstmt.setString(2, message);
	        pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
	        pstmt.setInt(4, writerId);
	        pstmt.setInt(5, meetingId); // 🔥 meetingId 추가

	        pstmt.executeUpdate();
	        System.out.println("Message saved successfully for meeting " + meetingId);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Error saving message to DB.");
	    }
	}


 // 최근 N개의 메시지를 가져오는 메서드
	public static List<ChatMessage> getRecentMessages(int meetingId, int limit) {
	    List<ChatMessage> messages = new ArrayList<>();
	    String sql = "SELECT nickname, message, sent_time, meeting_id FROM chat_messages WHERE meeting_id = ? ORDER BY reg_date ASC LIMIT ?";

	    try (Connection conn = DBUtil.getConnection(); 
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, meetingId);
	        stmt.setInt(2, limit);
	        ResultSet rs = stmt.executeQuery();
	        
	        while (rs.next()) {
	            ChatMessage chatMessage = new ChatMessage();
	            chatMessage.setNickname(rs.getString("nickname"));
	            chatMessage.setMessage(rs.getString("message"));
	            chatMessage.setSentTime(rs.getTimestamp("sent_time"));
	            chatMessage.setMeetingId(rs.getInt("meeting_id"));  // 🔥 meetingId 저장
	            messages.add(chatMessage);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return messages;
	}


}