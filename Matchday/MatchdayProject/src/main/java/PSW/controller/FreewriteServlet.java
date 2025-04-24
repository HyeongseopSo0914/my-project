package PSW.controller;

import PSH.model.Member; // Member 클래스 임포트
import util.DBUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/freewrite")
public class FreewriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/PSWJSP/freewrite.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 한글 인코딩 처리

        // 로그인 여부 확인
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember"); // 세션에서 Member 객체 가져오기

        if (loggedInMember == null) { // 로그인되지 않은 경우
            response.sendRedirect(request.getContextPath() + "/freewrite?error=not_logged_in");
            return;
        }

        int writerId = loggedInMember.getId(); // Member 객체에서 ID 가져오기

        // 사용자 입력 값 받기
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 데이터베이스 저장 로직
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "INSERT INTO bulletin_board (bulletin_title, bulletin_content, bulletin_writer_id, bulletin_regdate) VALUES (?, ?, ?, ?)";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, writerId);
            pstmt.setString(4, formattedDate);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                response.sendRedirect(request.getContextPath() + "/freeboard"); // 성공 시 글 목록 페이지로 이동
            } else {
                response.sendRedirect(request.getContextPath() + "/freewrite?error=db_error"); // DB 저장 실패 시 에러 메시지 전달
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/freewrite?error=db_error"); // 예외 발생 시 에러 메시지 전달
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }
}
