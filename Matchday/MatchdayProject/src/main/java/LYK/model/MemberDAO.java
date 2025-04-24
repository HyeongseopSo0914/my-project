package LYK.model;

import java.sql.*;

import LYK.model.DBUtil;

public class MemberDAO {
	// 로그인
	public boolean isValidMember(String email, String password) {
		String sql = "SELECT * FROM members WHERE email = ? AND password = ?";
		try(Connection conn = DBUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)){
			
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			
			return rs.next();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// 사용자 정보 엑세스
	public MemberDTO getMemberInfo(String email) {
		String sql = "SELECT * FROM members WHERE email = ?";
		try(Connection conn = DBUtil.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)){
			
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				MemberDTO member = new MemberDTO();
				member.setEmail(rs.getString("email"));
				member.setNickname(rs.getString("nickname"));
				// +추가적인 정보 처리 가능
				
				return member;
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
