package PSW.controller;

import util.DBUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/DeletefreeboardServlet")
public class DeletefreeboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");

        // id가 null 또는 빈 문자열이면 오류 방지
        if (idParam == null || idParam.trim().isEmpty()) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('잘못된 요청입니다.');");
            response.getWriter().println("location.href='" + request.getContextPath() + "/freeboard';");
            response.getWriter().println("</script>");
            return;
        }

        try {
            int id = Integer.parseInt(idParam); // 정상적인 숫자인지 확인

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM bulletin_board WHERE bulletin_id = ?")) {
                
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();

                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println("<script>");
                if (affectedRows > 0) {
                    response.getWriter().println("alert('게시글이 삭제되었습니다.');");
                } else {
                    response.getWriter().println("alert('삭제할 게시글이 존재하지 않습니다.');");
                }
                response.getWriter().println("location.href='" + request.getContextPath() + "/freeboard';");
                response.getWriter().println("</script>");
            }
        } catch (NumberFormatException e) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('잘못된 요청입니다.');");
            response.getWriter().println("location.href='" + request.getContextPath() + "/freeboard';");
            response.getWriter().println("</script>");
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>");
            response.getWriter().println("alert('서버 오류가 발생했습니다. 다시 시도해주세요.');");
            response.getWriter().println("location.href='" + request.getContextPath() + "/freeboard';");
            response.getWriter().println("</script>");
        }
    }
}
