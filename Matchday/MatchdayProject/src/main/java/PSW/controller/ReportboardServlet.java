package PSW.controller;

import PSW.model.Report;
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

@WebServlet("/reportboard")
public class ReportboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
        if (session.getAttribute("loggedInMember") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
    	request.setCharacterEncoding("UTF-8");

        String search = request.getParameter("search");
        List<Report> reports = new ArrayList<>();

        String sql = "SELECT r.report_id, r.report_title, r.report_content, r.report_regdate, " +
                     "m.nickname " +
                     "FROM report_board r " +
                     "LEFT JOIN members m ON r.report_writer_id = m.id " + // members 테이블과 JOIN
                     (search != null && !search.isEmpty() ? "WHERE r.report_title LIKE ? OR r.report_content LIKE ?" : "") +
                     " ORDER BY r.report_regdate DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (search != null && !search.isEmpty()) {
                pstmt.setString(1, "%" + search + "%");
                pstmt.setString(2, "%" + search + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Report report = new Report();
                    report.setId(rs.getInt("report_id"));
                    report.setTitle(rs.getString("report_title"));
                    report.setContent(rs.getString("report_content"));
                    report.setNickname(rs.getString("nickname") != null ? rs.getString("nickname") : "익명"); // 닉네임이 없으면 "익명"
                    report.setRegdate(rs.getTimestamp("report_regdate"));

                    reports.add(report);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("reports", reports);
        request.getRequestDispatcher("/WEB-INF/views/PSWJSP/reportboard.jsp").forward(request, response);
    }
}
