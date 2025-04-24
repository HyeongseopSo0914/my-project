package PSH.controller;

import PSH.dao.FriendDAO;
import PSH.dao.NotificationDAO;
import PSH.dao.RegionDAO;
import PSH.dao.BoardGameDAO;
import PSH.model.Member;
import PSH.model.NotificationType;
import PSH.model.FriendRequest;
import PSH.model.BoardGame;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/friends/*")
public class FriendServlet extends HttpServlet {
    private FriendDAO friendDAO = new FriendDAO();
    private RegionDAO regionDAO = new RegionDAO();
    private BoardGameDAO boardGameDAO = new BoardGameDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            showFriendSearchPage(request, response);
        } else if (pathInfo.equals("/requests")) {
            showFriendRequests(request, response);
        } else if (pathInfo.equals("/list")) {
            showFriendList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            sendJsonResponse(response, false, "로그인이 필요합니다.");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo.equals("/send-request") || pathInfo.equals("/send-request/")) {
            handleFriendRequest(request, response, loggedInMember);
        } else if (pathInfo.equals("/respond-request") || pathInfo.equals("/respond-request/")) {
            handleFriendResponse(request, response, loggedInMember);
        } else if (pathInfo.equals("/delete") || pathInfo.equals("/delete/")) {
            handleFriendDelete(request, response, loggedInMember);
        } else {
            sendJsonResponse(response, false, "잘못된 요청 경로입니다.");
        }
    }
    

    private void showFriendSearchPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Member loggedInMember = (Member) request.getSession().getAttribute("loggedInMember");
        
        List<Map<String, Object>> regions = regionDAO.getAllRegions();
        request.setAttribute("regions", regions);

        String regionId = request.getParameter("region");
        List<Map<String, Object>> districts = null;
        if (regionId != null && !regionId.isEmpty()) {
            districts = regionDAO.getDistrictsByRegion(Integer.parseInt(regionId));
            request.setAttribute("districts", districts);
        }

        List<BoardGame> games = boardGameDAO.getRecommendedGames();
        request.setAttribute("games", games);

        String districtId = request.getParameter("district");
        String gameName = request.getParameter("game");
        List<Member> searchResults = null;

        if (gameName != null && !gameName.isEmpty()) {
            // 게임 검색만 했을 때
            if (districtId == null && regionId == null) {
                searchResults = friendDAO.searchFriendsByGame(gameName);
            } 
            // 시/군/구 선택 후 게임 검색
            else if (districtId != null && !districtId.isEmpty()) {
                searchResults = friendDAO.searchFriendsByGameAndDistrict(
                    Integer.parseInt(districtId),
                    gameName
                );
            } 
            // 시/도 선택 후 게임 검색
            else if (regionId != null && !regionId.isEmpty()) {
                searchResults = friendDAO.searchFriendsByGameAndRegion(
                    Integer.parseInt(regionId),
                    gameName
                );
            }
        } 
        // 게임 검색 없이 지역만 선택했을 때
        else {
            if (districtId != null && !districtId.isEmpty()) {
                searchResults = friendDAO.searchFriendsByDistrict(Integer.parseInt(districtId));
            } else if (regionId != null && !regionId.isEmpty()) {
                searchResults = friendDAO.searchFriendsByRegion(Integer.parseInt(regionId));
            }
        }

        if (searchResults != null) {
            for (Member member : searchResults) {
                if (member.getId() != loggedInMember.getId()) {
                    if (friendDAO.areFriends(loggedInMember.getId(), member.getId())) {
                        member.setFriendStatus("FRIENDS");
                    } else {
                        String requestStatus = friendDAO.getFriendRequestStatus(loggedInMember.getId(), member.getId());
                        member.setFriendStatus(requestStatus != null ? requestStatus : "NONE");
                    }
                    // 게임 검색을 했을 때만 관심 게임 정보 유지
                    if (gameName == null || gameName.isEmpty()) {
                        member.setFavoriteGames(null);
                    }
                }
            }
        }

        request.setAttribute("searchResults", searchResults);
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/friends_search.jsp")
            .forward(request, response);
    }

    private void showFriendRequests(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Member loggedInMember = (Member) request.getSession().getAttribute("loggedInMember");

        List<FriendRequest> receivedRequests = friendDAO.getReceivedRequests(loggedInMember.getId());
        List<FriendRequest> sentRequests = friendDAO.getSentRequests(loggedInMember.getId());

        request.setAttribute("receivedRequests", receivedRequests);
        request.setAttribute("sentRequests", sentRequests);

        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/friends_requests.jsp")
               .forward(request, response);
    }

    private void showFriendList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Member loggedInMember = (Member) request.getSession().getAttribute("loggedInMember");

        List<Member> friends = friendDAO.getFriends(loggedInMember.getId());
        request.setAttribute("friends", friends);

        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/friends_list.jsp")
               .forward(request, response);
    }

    private void handleFriendRequest(HttpServletRequest request, HttpServletResponse response, Member sender) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Form 데이터에서 receiverId 읽기
        String receiverIdStr = request.getParameter("receiverId");
        
        if (receiverIdStr == null || receiverIdStr.trim().isEmpty()) {
            sendJsonResponse(response, false, "잘못된 요청입니다.");
            return;
        }
        
        try {
            int receiverId = Integer.parseInt(receiverIdStr);
            NotificationDAO notificationDAO = new NotificationDAO();

            if (friendDAO.areFriends(sender.getId(), receiverId)) {
                sendJsonResponse(response, false, "이미 친구입니다.");
                return;
            }

            boolean success = friendDAO.sendFriendRequest(sender.getId(), receiverId);
            if (success) {
                notificationDAO.createNotification(
                    receiverId, 
                    NotificationType.FRIEND_REQUEST, 
                    sender.getNickname() + "님이 친구 요청을 보냈습니다.", 
                    sender.getId()
                );
            }
            sendJsonResponse(response, success, success ? "친구 요청을 보냈습니다." : "요청 전송에 실패했습니다.");
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "잘못된 사용자 ID 형식입니다.");
        }
    }

    private void handleFriendResponse(HttpServletRequest request, HttpServletResponse response, Member loggedInMember) 
            throws IOException {
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String action = request.getParameter("action");

        FriendRequest friendRequest = friendDAO.getFriendRequestById(requestId);
        if (friendRequest == null || friendRequest.getReceiverId() != loggedInMember.getId()) {
            sendJsonResponse(response, false, "유효하지 않은 요청입니다.");
            return;
        }

        boolean success = friendDAO.updateFriendRequestStatus(requestId, 
            "accept".equals(action) ? "ACCEPTED" : "REJECTED");

        sendJsonResponse(response, success, success ? 
            ("accept".equals(action) ? "친구 요청을 수락했습니다." : "친구 요청을 거절했습니다.") : "요청 처리에 실패했습니다.");
    }

    private void handleFriendDelete(HttpServletRequest request, HttpServletResponse response, Member loggedInMember) 
            throws IOException {
        int friendId = Integer.parseInt(request.getParameter("friendId"));

        boolean success = friendDAO.deleteFriendship(loggedInMember.getId(), friendId);
        sendJsonResponse(response, success, success ? "친구가 삭제되었습니다." : "친구 삭제에 실패했습니다.");
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) 
            throws IOException {
        response.getWriter().write(
            String.format("{\"success\":%b,\"message\":\"%s\"}", success, message)
        );
    }
}
