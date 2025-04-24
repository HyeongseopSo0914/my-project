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

@WebServlet("/Deletenotice")
public class DeletenoticeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String noticeId = request.getParameter("noticeId"); // 삭제할 공지사항의 ID

        if (noticeId != null && !noticeId.isEmpty()) {
            String sql = "DELETE FROM notice_board WHERE notice_id = ?";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, Integer.parseInt(noticeId)); // ID를 파라미터로 설정

                int result = pstmt.executeUpdate(); // 삭제 수행

                if (result > 0) {
                    System.out.println("✅ 공지사항 삭제 성공!");
                    response.sendRedirect(request.getContextPath() + "/noticeboard"); // 삭제 후 공지사항 목록으로 리다이렉트
                } else {
                    System.out.println("❌ 공지사항 삭제 실패!");
                    response.sendRedirect(request.getContextPath() + "/noticeboard"); // 실패 시에도 공지사항 목록으로 리다이렉트
                }

            } catch (SQLException e) {
                System.out.println("❌ SQL 오류 발생!");
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/noticeboard"); // 오류 발생 시 공지사항 목록으로 리다이렉트
            } catch (Exception e) {
                System.out.println("❌ 예기치 않은 오류 발생!");
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/noticeboard"); // 예기치 않은 오류 시 공지사항 목록으로 리다이렉트
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/noticeboard"); // noticeId가 없을 경우 공지사항 목록으로 리다이렉트
        }
    }
}
