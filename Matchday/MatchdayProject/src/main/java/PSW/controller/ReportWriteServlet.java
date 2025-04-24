package PSW.controller;


import util.DBUtil;

import PSH.model.Member;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/reportwrite")
public class ReportWriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/PSWJSP/reportwrite.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Member loggedInMember = (session != null) ? (Member) session.getAttribute("loggedInMember") : null;

        // 로그인 체크
        if (loggedInMember == null) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>alert('로그인이 필요합니다.'); location.href='" + request.getContextPath() + "/login';</script>");
            return;
        }

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>alert('제목과 내용을 입력해주세요.'); history.back();</script>");
            return;
        }

        String sql = "INSERT INTO report_board (report_title, report_content, report_writer_id, report_regdate) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, loggedInMember.getId());
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            int result = pstmt.executeUpdate();

            if (result > 0) {
                response.sendRedirect(request.getContextPath() + "/reportboard");
            } else {
                response.getWriter().println("<script>alert('신고 등록에 실패했습니다.'); history.back();</script>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('오류가 발생했습니다. 다시 시도해주세요.'); history.back();</script>");
        }
    }
}
