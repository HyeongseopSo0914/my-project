package PSW.controller;

import PSW.model.Bulletin;
import util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/freeboard")
public class FreeboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
        if (session.getAttribute("loggedInMember") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
    	String searchQuery = request.getParameter("search");  // 검색어 가져오기

        List<Bulletin> bulletins = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            // 기본적으로 모든 게시글 조회
            String sql = "SELECT bb.bulletin_id, bb.bulletin_title, m.nickname, bb.bulletin_recommend, bb.bulletin_regdate, bb.bulletin_content " +
                         "FROM bulletin_board bb " +
                         "JOIN members m ON bb.bulletin_writer_id = m.id";
            
            // 검색어가 있으면 WHERE 조건 추가
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                sql += " WHERE bb.bulletin_title LIKE ?";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    pstmt.setString(1, "%" + searchQuery + "%");
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Bulletin bulletin = new Bulletin(
                            rs.getInt("bulletin_id"),
                            rs.getString("bulletin_title"),
                            rs.getString("nickname"),
                            rs.getInt("bulletin_recommend"),
                            rs.getTimestamp("bulletin_regdate"),
                            rs.getString("bulletin_content")  // content도 추가
                        );
                        bulletins.add(bulletin);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("bulletins", bulletins);
        request.getRequestDispatcher("/WEB-INF/views/PSWJSP/freeboard.jsp").forward(request, response);
    }
}
