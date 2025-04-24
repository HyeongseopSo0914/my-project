// DeleteMeetingServlet.java
package PSH.controller;

import PSH.dao.MeetingDAO;
import PSH.model.Member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/meeting/delete")
public class DeleteMeetingServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            System.out.println("❌ [DeleteMeetingServlet] 로그인하지 않은 사용자입니다.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int meetingId = Integer.parseInt(request.getParameter("meetingId"));
        if (meetingDAO.getMeetingById(meetingId).getCreatedBy() != loggedInMember.getId()) {
            System.out.println("❌ [DeleteMeetingServlet] 모임장만 삭제할 수 있습니다.");
            response.sendRedirect(request.getContextPath() + "/meetings");
            return;
        }

        boolean isDeleted = meetingDAO.deleteMeeting(meetingId);
        if (isDeleted) {
            System.out.println("🗑 [DeleteMeetingServlet] 모임이 성공적으로 삭제되었습니다.");
        } else {
            System.out.println("❌ [DeleteMeetingServlet] 모임 삭제에 실패했습니다.");
        }

        response.sendRedirect(request.getContextPath() + "/meetings");
    }
}
