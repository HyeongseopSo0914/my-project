package PSH.controller;

import PSH.dao.MeetingDAO;
import PSH.dao.RegionDAO;
import PSH.model.Meeting;
import PSH.model.Member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/edit_meeting")
public class EditMeetingServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();
    private RegionDAO regionDAO = new RegionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int meetingId = Integer.parseInt(request.getParameter("meetingId"));
        Meeting meeting = meetingDAO.getMeetingById(meetingId);

        if (meeting == null) {
            response.sendRedirect(request.getContextPath() + "/meetings");
            return;
        }

        List<Map<String, Object>> regions = regionDAO.getAllRegions();
        
        // 안전한 districts 필터링
        List<Map<String, Object>> districts = regionDAO.getDistrictsByRegion(meeting.getRegionId())
            .stream()
            .filter(district -> 
                district.get("region_id") != null && 
                district.get("id") != null
            )
            .collect(Collectors.toList());

        request.setAttribute("meeting", meeting);
        request.setAttribute("regions", regions);
        request.setAttribute("districts", districts);

        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/edit_meeting.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int meetingId = Integer.parseInt(request.getParameter("meetingId"));
        String description = request.getParameter("description");
        String newDate = request.getParameter("date");
        String newTime = request.getParameter("time");
        String title = request.getParameter("title");
        int newRegionId = Integer.parseInt(request.getParameter("regionId"));
        int newDistrictId = Integer.parseInt(request.getParameter("districtId"));
        int newGameId = Integer.parseInt(request.getParameter("gameId"));
        int newMaxParticipants = Integer.parseInt(request.getParameter("maxParticipants")); // 추가: 수정된 최대 인원

        Meeting meeting = meetingDAO.getMeetingById(meetingId);

        if (meeting == null || meeting.getCreatedBy() != loggedInMember.getId()) {
            response.sendRedirect(request.getContextPath() + "/meetings");
            return;
        }
        
     // 현재 참가자 수 확인
        int currentParticipants = meeting.getCurrentParticipants();

        // 현재 참가자 수보다 작은 인원으로 수정할 경우 차단
        if (newMaxParticipants < currentParticipants) {
            request.setAttribute("errorMessage", "현재 참가자 수보다 적은 인원으로 수정할 수 없습니다.");
            request.setAttribute("meeting", meeting);
            request.getRequestDispatcher("/WEB-INF/views/PSHJSP/edit_meeting.jsp").forward(request, response);
            return;
        }


        boolean isUpdated = meetingDAO.updateMeetingDetails(meetingId, title, description, newDate, newTime, newRegionId, newDistrictId, newGameId, newMaxParticipants);

        if (isUpdated) {
            System.out.println("🔔 모임 '" + meeting.getTitle() + "' 모임 정보가 변경되었습니다.");
        }

        response.sendRedirect(request.getContextPath() + "/meeting/detail?meetingId=" + meetingId);
    }
}
