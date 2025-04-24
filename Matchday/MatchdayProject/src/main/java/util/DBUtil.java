package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;  // Added this import



public class DBUtil {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://matchdaydatab.cpuqcekcu7i5.ap-northeast-2.rds.amazonaws.com:3306/matchdaydatab?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "admin";  // RDS 사용자명 (변경 필요)
    private static final String PASSWORD = "Matchday2025!";  // RDS 비밀번호 (변경 필요)

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database Driver Load Error", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    // Connection 닫기 메서드 추가
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // PreparedStatement 닫기 메서드 추가
    public static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
 // Add these methods to DBUtil.java:

 // 모든 리소스 한번에 닫기 메서드
 // ResultSet 닫기 메서드
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void close(PreparedStatement pstmt, Connection conn) {
        close(pstmt);
        close(conn);
    }

    // 모든 리소스 한번에 닫기 메서드
    public static void close(PreparedStatement pstmt, Connection conn, ResultSet rs) {
        close(rs);
        close(pstmt);
        close(conn);
    }
}
