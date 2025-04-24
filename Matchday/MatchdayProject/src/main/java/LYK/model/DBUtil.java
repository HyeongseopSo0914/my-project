package LYK.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; // MySQL JDBC 드라이버
    private static final String URL = "jdbc:mysql://matchdaydatab.cpuqcekcu7i5.ap-northeast-2.rds.amazonaws.com:3306/matchdaydatab?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "admin";  // RDS 사용자명
    private static final String PASSWORD = "Matchday2025!";  // RDS 비밀번호

    static {
        try {
            // MySQL 드라이버 로드
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database Driver Load Error", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("DB Connection Failed: " + e.getMessage());
            throw e;  // 예외를 던져 호출한 곳에서 처리하도록 함
        }
    }
}