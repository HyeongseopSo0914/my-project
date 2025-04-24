package PSW.controller;

import PSW.model.Notice;
import util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/noticedetail")
public class NoticeDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String noticeIdStr = request.getParameter("noticeId");
        
        if (noticeIdStr != null) {
            try {
                int noticeId = Integer.parseInt(noticeIdStr);
                String sql = "SELECT n.notice_id, n.notice_title, n.notice_content, n.notice_regdate " +
                         "FROM notice_board n " +
                         "WHERE n.notice_id = ?";

                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setInt(1, noticeId);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            Notice notice = new Notice();
                            notice.setNoticeId(rs.getInt("notice_id"));
                            notice.setNoticeTitle(rs.getString("notice_title"));
                            notice.setNoticeContent(rs.getString("notice_content"));
                            notice.setNoticeRegdate(rs.getTimestamp("notice_regdate"));
                            
                            request.setAttribute("notice", notice);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        request.getRequestDispatcher("/WEB-INF/views/PSWJSP/noticedetail.jsp").forward(request, response);
    }
}