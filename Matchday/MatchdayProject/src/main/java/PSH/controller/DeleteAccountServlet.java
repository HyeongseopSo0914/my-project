package PSH.controller;

import java.io.IOException;

import PSH.dao.MemberDAO;
import PSH.model.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.DBUtil;

@WebServlet("/delete_account")
public class DeleteAccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (session == null || loggedInMember == null) {
            System.out.println("❌ 로그인된 사용자가 없습니다. 탈퇴 불가.");
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        int memberId = loggedInMember.getId();
        MemberDAO memberDAO = new MemberDAO();

        System.out.println("🔍 탈퇴 시도: 회원 ID = " + memberId);

        try {
            memberDAO.deleteMember(memberId);  // 회원 삭제 시도
            System.out.println("✅ 회원 탈퇴 성공: ID = " + memberId);

            session.invalidate();  // 세션 종료
            response.sendRedirect(request.getContextPath() + "/");
        } catch (Exception e) {
            System.out.println("❌ 회원 탈퇴 실패: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "회원 탈퇴 중 오류 발생: " + e.getMessage());
        }
    }
}

