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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/meetings")
public class ListMeetingsServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();
    private RegionDAO regionDAO = new RegionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 세션에서 로그인 정보 확인
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        // 검색 필터 파라미터
        String gameSearch = request.getParameter("gameSearch");
        String regionIdStr = request.getParameter("region");
        String districtIdStr = request.getParameter("district");
        
        // 페이지 번호 파라미터
        int currentPage = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.isEmpty()) {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            }
        } catch (NumberFormatException e) {
            // 파싱 실패시 기본값 1 사용
        }

        List<Meeting> meetings;
        int totalPages;
        int regionId = -1;
        int districtId = -1;

        try {
            // 지역 ID 파싱
            if (regionIdStr != null && !regionIdStr.isEmpty()) {
                regionId = Integer.parseInt(regionIdStr);
            }
            
            // 시/군/구 ID 파싱
            if (districtIdStr != null && !districtIdStr.isEmpty()) {
                districtId = Integer.parseInt(districtIdStr);
            }

            // 검색 조건에 따른 모임 조회
            if (gameSearch != null || regionId != -1 || districtId != -1) {
                meetings = meetingDAO.searchMeetingsByPage(gameSearch, regionId, districtId, currentPage);
                totalPages = meetingDAO.getSearchTotalPages(gameSearch, regionId, districtId);
            } else {
                meetings = meetingDAO.getMeetingsByPage(currentPage);
                totalPages = meetingDAO.getTotalPages();
            }
        } catch (NumberFormatException e) {
            System.out.println("지역/시군구 ID 파싱 오류: " + e.getMessage());
            meetings = meetingDAO.getMeetingsByPage(currentPage);
            totalPages = meetingDAO.getTotalPages();
        }

        // 지역 데이터 준비
        List<Map<String, Object>> regions = regionDAO.getAllRegions();
        List<Map<String, Object>> districts = new ArrayList<>();

        // 선택된 시/도가 있는 경우 해당 시/군/구 목록 조회
        if (regionId != -1) {
            districts = regionDAO.getDistrictsByRegion(regionId);
        }

        // "내가 참가한 모임" 필터 추가
        boolean filterMyMeetings = "on".equals(request.getParameter("myMeetings"));
        if (loggedInMember != null) {
            for (Meeting meeting : meetings) {
                boolean isParticipating = meeting.getCreatedBy() == loggedInMember.getId() || 
                                        meetingDAO.isUserJoined(meeting.getMeetingId(), loggedInMember.getId());
                meeting.setUserParticipating(isParticipating);
            }
            if (filterMyMeetings) {
                meetings.removeIf(meeting -> !meeting.isUserParticipating());
            }
        }

        // JSP로 데이터 전달
        request.setAttribute("meetings", meetings);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("regions", regions);
        request.setAttribute("districts", districts);
        request.setAttribute("loggedInMember", loggedInMember);
        request.setAttribute("selectedRegion", regionId);
        request.setAttribute("selectedDistrict", districtId);
        request.setAttribute("searchKeyword", gameSearch);

        // 모임 찾기 페이지로 포워드
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/meetings.jsp")
               .forward(request, response);
    }
}