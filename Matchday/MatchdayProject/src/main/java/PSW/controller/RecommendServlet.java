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

@WebServlet("/recommend")
public class RecommendServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bulletinIdStr = request.getParameter("bulletinId");

        if (bulletinIdStr != null) {
            try {
                int bulletinId = Integer.parseInt(bulletinIdStr);

                // 추천 수 증가
                String sql = "UPDATE bulletin_board SET bulletin_recommend = bulletin_recommend + 1 WHERE bulletin_id = ?";

                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, bulletinId);
                    pstmt.executeUpdate();
                }
                
                

                // 추천 후 게시글 상세 페이지로 리다이렉트
                response.sendRedirect(request.getContextPath() + "/freedetail?bulletinId=" + bulletinId);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "추천 처리 중 오류가 발생했습니다.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 요청입니다.");
        }
    }
}
