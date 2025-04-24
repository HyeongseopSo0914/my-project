package PSH.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import PSH.dao.MemberDAO;
import PSH.dao.RegionDAO;
import PSH.dao.ReviewDAO;
import PSH.dao.BoardGameDAO;
import PSH.dao.FriendDAO;
import PSH.model.Member;
import PSH.model.Tag;
import PSH.model.BoardGame;
import PSH.model.UserReviewSummary;

@WebServlet("/friends/details/*")
public class UserDetailServlet extends HttpServlet {
    private MemberDAO memberDAO = new MemberDAO();
    private RegionDAO regionDAO = new RegionDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();
    private BoardGameDAO boardGameDAO = new BoardGameDAO();
    private FriendDAO friendDAO = new FriendDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"로그인이 필요합니다.\"}");
            return;
        }

        String pathInfo = request.getPathInfo();
        System.out.println("UserDetailServlet - PathInfo: " + pathInfo); // 디버깅 로그

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int userId = Integer.parseInt(pathInfo.substring(1));
            System.out.println("UserDetailServlet - Requested User ID: " + userId); // 디버깅 로그

            Member member = memberDAO.findById(userId);
            System.out.println("UserDetailServlet - Fetched Member: " + member); // 디버깅 로그
            
            if (member == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"사용자를 찾을 수 없습니다.\"}");
                return;
            }

            UserReviewSummary reviewSummary = reviewDAO.getUserReviewSummary(userId);
            List<BoardGame> favoriteGames = boardGameDAO.getFavoriteGames(userId);
            List<Map<String, Object>> userRegions = regionDAO.getMemberDistricts(userId);
            String friendStatus = friendDAO.getFriendRequestStatus(loggedInMember.getId(), userId);
            if (friendDAO.areFriends(loggedInMember.getId(), userId)) {
                friendStatus = "FRIENDS";
            }

            // 디버깅용 로그 추가
            System.out.println("Review Summary: " + (reviewSummary != null ? reviewSummary.toString() : "null"));
            System.out.println("Favorite Games: " + favoriteGames);
            System.out.println("User Regions: " + userRegions);
            System.out.println("Friend Status: " + friendStatus);

            StringBuilder json = new StringBuilder();
            json.append("{");

            // 기본 정보
            json.append("\"id\":").append(member.getId()).append(",");
            json.append("\"nickname\":\"").append(escapeJsonString(member.getNickname())).append("\",");
            json.append("\"profileImageUrl\":\"").append(escapeJsonString(member.getProfileImageUrl() != null ? member.getProfileImageUrl() : "")).append("\",");
            
            // 리뷰 정보 (null 체크 추가)
            json.append("\"averageRating\":").append(reviewSummary != null ? reviewSummary.getAverageRating() : 0.0).append(",");
            json.append("\"totalReviews\":").append(reviewSummary != null ? reviewSummary.getTotalReviews() : 0).append(",");
            json.append("\"friendStatus\":\"").append(friendStatus != null ? friendStatus : "NONE").append("\",");

            // 태그 정보
            json.append("\"tags\":[");
            if (reviewSummary != null && reviewSummary.getTopTags() != null && !reviewSummary.getTopTags().isEmpty()) {
                boolean firstTag = true;
                for (Tag tag : reviewSummary.getTopTags()) {
                    if (!firstTag) json.append(",");
                    json.append("{");
                    json.append("\"name\":\"").append(escapeJsonString(tag.getTagName())).append("\",");
                    json.append("\"count\":").append(tag.getCount());
                    json.append("}");
                    firstTag = false;
                }
            }
            json.append("],");

            // 지역 정보
            json.append("\"regions\":[");
            if (userRegions != null && !userRegions.isEmpty()) {
                boolean firstRegion = true;
                for (Map<String, Object> region : userRegions) {
                    if (!firstRegion) json.append(",");
                    json.append("\"").append(escapeJsonString(region.get("regionName") + " " + region.get("districtName"))).append("\"");
                    firstRegion = false;
                }
            }
            json.append("],");

            // 게임 정보
            json.append("\"games\":[");
            if (favoriteGames != null && !favoriteGames.isEmpty()) {
                boolean firstGame = true;
                for (BoardGame game : favoriteGames) {
                    if (!firstGame) json.append(",");
                    json.append("\"").append(escapeJsonString(game.getName())).append("\"");
                    firstGame = false;
                }
            }
            json.append("]");

            json.append("}");
            
            // 최종 JSON 출력 전 로그
            System.out.println("Final JSON Response: " + json.toString());
            
            response.getWriter().write(json.toString());

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"잘못된 사용자 ID 형식입니다.\"}");
        }
    }

    // JSON 문자열 이스케이프 처리
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '"': output.append("\\\""); break;
                case '\\': output.append("\\\\"); break;
                case '\b': output.append("\\b"); break;
                case '\f': output.append("\\f"); break;
                case '\n': output.append("\\n"); break;
                case '\r': output.append("\\r"); break;
                case '\t': output.append("\\t"); break;
                default:
                    if (ch < ' ') {
                        String hex = String.format("\\u%04x", (int) ch);
                        output.append(hex);
                    } else {
                        output.append(ch);
                    }
            }
        }
        return output.toString();
    }
}