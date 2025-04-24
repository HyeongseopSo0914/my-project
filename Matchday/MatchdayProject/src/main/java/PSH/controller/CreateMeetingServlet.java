package PSH.controller;

import PSH.dao.BoardGameDAO;
import PSH.dao.FriendDAO;
import PSH.dao.MeetingDAO;
import PSH.dao.MemberDAO;
import PSH.dao.NotificationDAO;
import PSH.dao.RegionDAO;
import PSH.model.BoardGame;
import PSH.model.Meeting;
import PSH.model.Member;
import PSH.model.NotificationType;

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

@WebServlet("/create_meeting")
public class CreateMeetingServlet extends HttpServlet {
    private MeetingDAO meetingDAO = new MeetingDAO();
    private BoardGameDAO boardGameDAO = new BoardGameDAO(); // 추가
    private MemberDAO memberDAO = new MemberDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    private RegionDAO regionDAO = new RegionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Map<String, Object>> regions = regionDAO.getAllRegions();
        List<Map<String, Object>> districts = new ArrayList<>();
        // 모든 시/군/구 데이터 불러오기
        for (Map<String, Object> region : regions) {
            int regionId = (int) region.get("id");
            districts.addAll(regionDAO.getDistrictsByRegion(regionId));
        }
        
     // 게임 목록 불러오기
        String searchKeyword = request.getParameter("search");
        List<BoardGame> games;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            games = boardGameDAO.searchGames(searchKeyword.trim());
        } else {
            games = boardGameDAO.getRecommendedGames();
        }
        
        request.setAttribute("games", games);
        request.setAttribute("regions", regions);
        request.setAttribute("districts", districts);
        // 모임 생성 폼 페이지로 이동
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/create_meeting.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 세션에서 로그인한 사용자 정보 가져오기
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        // 로그인하지 않은 사용자는 접근 불가
        if (loggedInMember == null) {
        	System.out.println("❌ [CreateMeetingServlet] 로그인하지 않은 사용자입니다.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 폼에서 전송된 데이터 가져오기
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        int gameId = Integer.parseInt(request.getParameter("gameId"));
        int regionId = Integer.parseInt(request.getParameter("regionId"));
        int districtId = Integer.parseInt(request.getParameter("districtId"));
        String regionName = regionDAO.getRegionNameById(regionId);
        String districtName = regionDAO.getDistrictNameById(districtId);
        String date = request.getParameter("date");
        String time = request.getParameter("time");
        int maxParticipants = Integer.parseInt(request.getParameter("maxParticipants"));
        BoardGame boardGame = boardGameDAO.getGameById(gameId);
        String gameName = (boardGame != null) ? boardGame.getName() : "알 수 없는 게임";
       
        
        System.out.println("📌 [CreateMeetingServlet] 받은 데이터:");
        System.out.println("제목: " + title);
        System.out.println("설명: " + description);
        System.out.println("게임 ID: " + gameId);
        System.out.println("지역 ID: " + regionId);
        System.out.println("시/군/구 ID: " + districtId);
        System.out.println("날짜: " + date);
        System.out.println("시간: " + time);
        System.out.println("최대 인원: " + maxParticipants);
        
        try {

        // Meeting 객체 생성 및 데이터 설정
        Meeting meeting = new Meeting();
        meeting.setTitle(title);
        meeting.setDescription(description);
        meeting.setGameId(gameId);
        meeting.setGameName(gameName);  // 게임 이름 설정
        meeting.setRegionId(regionId);
        meeting.setDistrictId(districtId);
        meeting.setDate(date);
        meeting.setTime(time);
        meeting.setMaxParticipants(maxParticipants);
        meeting.setCreatedBy(loggedInMember.getId());
        meeting.setRegionName(regionName);
        meeting.setDistrictName(districtName);

        // DB에 모임 저장
        boolean isCreated = meetingDAO.createMeeting(meeting);

        // 성공/실패에 따른 리다이렉트
        if (isCreated) {
            boolean isHostAdded = meetingDAO.joinMeeting(meeting.getMeetingId(), loggedInMember.getId());
            // 친구들에게 새 모임 알림
            FriendDAO friendDAO = new FriendDAO();
            friendDAO.notifyFriendsAboutNewMeeting(loggedInMember.getId(), meeting.getMeetingId(), meeting.getTitle());
            
            // 지역 기반 알림
            List<Member> regionMembers = memberDAO.getMembersByDistrict(meeting.getDistrictId());
            for (Member member : regionMembers) {
                if (member.getId() != meeting.getCreatedBy()) {
                    notificationDAO.createNotification(
                        member.getId(),
                        NotificationType.MEETING_CREATED_IN_REGION,
                        meeting.getTitle() + "이(가) " + meeting.getRegionName() + " " +
                        meeting.getDistrictName() + "에 생성되었습니다.",
                        meeting.getMeetingId()
                    );
                }
            }

            // 게임 기반 알림
            List<Member> gameMembers = memberDAO.getMembersByFavoriteGame(meeting.getGameId());
            for (Member member : gameMembers) {
                if (member.getId() != meeting.getCreatedBy() && !regionMembers.contains(member)) {
                    notificationDAO.createNotification(
                        member.getId(),
                        NotificationType.MEETING_CREATED_FOR_GAME,
                        meeting.getGameName() + " 게임의 새로운 모임이 생성되었습니다: " + meeting.getTitle(),
                        meeting.getMeetingId()
                    );
                }
            }

            if (isHostAdded) {
                response.sendRedirect(request.getContextPath() + "/meetings");
            } else {
                request.setAttribute("errorMessage", "모임장은 자동 참가에 실패했습니다.");
                request.getRequestDispatcher("/WEB-INF/views/PSHJSP/create_meeting.jsp")
                    .forward(request, response);
            }
        } else {
            request.setAttribute("errorMessage", "모임 생성에 실패했습니다. 다시 시도해 주세요.");
            request.getRequestDispatcher("/WEB-INF/views/PSHJSP/create_meeting.jsp")
                .forward(request, response);
        }} catch (NumberFormatException e) {
        System.out.println("❌ [CreateMeetingServlet] 숫자 변환 오류: " + e.getMessage());
        request.setAttribute("errorMessage", "입력한 값에 오류가 있습니다. 다시 확인해 주세요.");
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/create_meeting.jsp")
               .forward(request, response);
    } catch (Exception e) {
        System.out.println("❌ [CreateMeetingServlet] 예외 발생: " + e.getMessage());
        e.printStackTrace();
        request.setAttribute("errorMessage", "알 수 없는 오류가 발생했습니다.");
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/create_meeting.jsp")
               .forward(request, response);
}
    }}
