package PSW.controller;

import util.DBUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/NoticeWriteServlet")
public class NoticeWriteServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        System.out.println("📌 입력된 제목: " + title);
        System.out.println("📌 입력된 내용: " + content);

        // 제목이나 내용이 비어있으면 에러 처리
        if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
            System.out.println("❌ 오류: 제목 또는 내용이 비어있음");
            response.sendRedirect(request.getContextPath() + "/noticewrite.jsp?error=emptyFields");
            return;
        }

        // INSERT SQL 문 (새로운 공지사항 작성)
        String sql = "INSERT INTO notice_board (notice_title, notice_content, notice_regdate) VALUES (?, ?, NOW())";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, content);

            int result = pstmt.executeUpdate();
            System.out.println("DB 처리 결과 (영향받은 행 수): " + result);

            if (result > 0) {
                System.out.println("✅ 공지사항 작성 성공!");
                response.sendRedirect(request.getContextPath() + "/noticeboard");  // 공지사항 목록 페이지로 이동
            } else {
                System.out.println("❌ 공지사항 작성 실패!");
                response.sendRedirect(request.getContextPath() + "/noticewrite.jsp?error=insertFail");
            }

        } catch (SQLException e) {
            System.out.println("❌ SQL 오류 발생!");
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/noticewrite.jsp?error=sqlError");
        } catch (Exception e) {
            System.out.println("❌ 예기치 않은 오류 발생!");
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/noticewrite.jsp?error=unknownError");
        }
    }
}
