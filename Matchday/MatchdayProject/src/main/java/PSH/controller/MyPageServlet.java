package PSH.controller;

import PSH.model.Member;
import PSH.model.Review;
import PSH.model.BoardGame;
import PSH.model.Meeting;
import PSH.model.UserReviewSummary;
import PSH.service.MemberService;
import PSH.dao.BoardGameDAO;
import PSH.dao.ReviewDAO;
import PSH.dao.MeetingDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/mypage")
public class MyPageServlet extends HttpServlet {
    private BoardGameDAO boardGameDAO = new BoardGameDAO();
    private MemberService memberService = new MemberService();
    private ReviewDAO reviewDAO = new ReviewDAO();
    private MeetingDAO meetingDAO = new MeetingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 세션에서 로그인 정보 확인
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 사용자의 관심 게임 목록 조회
        List<BoardGame> favoriteGames = boardGameDAO.getFavoriteGames(loggedInMember.getId());
        
        // 사용자의 리뷰 요약 정보 조회
        UserReviewSummary reviewSummary = reviewDAO.getUserReviewSummary(loggedInMember.getId());
        
        // 리뷰를 작성해야 하는 완료된 모임 목록 조회
        List<Meeting> completedMeetings = meetingDAO.getCompletedMeetingsNeedingReview(loggedInMember.getId());
        
        // 속성 설정
        request.setAttribute("favoriteGames", favoriteGames);
        request.setAttribute("member", loggedInMember);
        request.setAttribute("reviewSummary", reviewSummary);
        request.setAttribute("completedMeetings", completedMeetings);

        // 마이페이지로 포워드
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/mypage.jsp")
               .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            response.getWriter().write("{\"success\": false, \"message\": \"로그인이 필요합니다.\"}");
            return;
        }

        String action = request.getParameter("action");
        
        if ("removeGame".equals(action)) {
            handleRemoveGame(request, response, loggedInMember);
        } else if ("writeReview".equals(action)) {
            handleWriteReview(request, response, loggedInMember);
        } else {
            response.getWriter().write("{\"success\": false, \"message\": \"잘못된 요청입니다.\"}");
        }
    }

    private void handleRemoveGame(HttpServletRequest request, HttpServletResponse response, Member loggedInMember) 
            throws IOException {
        String gameIdStr = request.getParameter("gameId");
        if (gameIdStr == null || gameIdStr.isEmpty()) {
            response.getWriter().write("{\"success\": false, \"message\": \"잘못된 요청입니다.\"}");
            return;
        }

        int gameId = Integer.parseInt(gameIdStr);
        boolean success = boardGameDAO.removeFavoriteGame(loggedInMember.getId(), gameId);

        response.getWriter().write("{\"success\": " + success + ", \"message\": \"" +
                (success ? "게임이 삭제되었습니다." : "삭제에 실패했습니다.") + "\"}");
    }

    private void handleWriteReview(HttpServletRequest request, HttpServletResponse response, Member loggedInMember) 
            throws IOException {
        int meetingId = Integer.parseInt(request.getParameter("meetingId"));
        int toUserId = Integer.parseInt(request.getParameter("toUserId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String[] tagIds = request.getParameterValues("tagIds");
        
        // 이미 리뷰를 작성했는지 확인
        if (reviewDAO.hasReviewed(loggedInMember.getId(), toUserId, meetingId)) {
            response.getWriter().write("{\"success\": false, \"message\": \"이미 리뷰를 작성했습니다.\"}");
            return;
        }

        // 리뷰 객체 생성
        Review review = new Review();
        review.setFromUserId(loggedInMember.getId());
        review.setToUserId(toUserId);
        review.setMeetingId(meetingId);
        review.setRating(rating);

        // 태그 ID 리스트 생성
        List<Integer> tagIdList = new ArrayList<>();
        if (tagIds != null) {
            for (String tagId : tagIds) {
                tagIdList.add(Integer.parseInt(tagId));
            }
        }

        // 리뷰 저장
        boolean success = reviewDAO.createReview(review, tagIdList);
        
        response.getWriter().write("{\"success\": " + success + ", \"message\": \"" +
                (success ? "리뷰가 성공적으로 저장되었습니다." : "리뷰 저장에 실패했습니다.") + "\"}");
    }
}