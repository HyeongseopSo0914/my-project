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

@WebServlet("/EditNoticeWriteServlet")
public class EditnoticeboardServlet2 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지

        String noticeId = request.getParameter("noticeId"); // 수정할 공지사항 ID
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        System.out.println("📌 입력된 ID: " + noticeId);
        System.out.println("📌 입력된 제목: " + title);
        System.out.println("📌 입력된 내용: " + content);

        // noticeId가 없으면 수정 불가능 → 에러 처리
        if (noticeId == null || noticeId.isEmpty()) {
            System.out.println("❌ 오류: noticeId가 없음");
            response.sendRedirect(request.getContextPath() + "/noticeboard?error=noNoticeId");
            return;
        }

        // UPDATE SQL 문
        String sql = "UPDATE notice_board SET notice_title = ?, notice_content = ?, notice_regdate = NOW() WHERE notice_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, Integer.parseInt(noticeId)); // ID를 정수로 변환

            int result = pstmt.executeUpdate();
            System.out.println("DB 처리 결과 (영향받은 행 수): " + result);

            if (result > 0) {
                System.out.println("✅ 공지사항 수정 성공!");
                response.sendRedirect(request.getContextPath() + "/noticeboard");  // NoticeBoardServlet으로 이동
            } else {
                System.out.println("❌ 공지사항 수정 실패! (존재하지 않는 ID)");
                response.sendRedirect(request.getContextPath() + "/noticewrite.jsp?error=updateFail");
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
