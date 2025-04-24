package PSH.controller;

import PSH.dao.ReviewDAO;
import PSH.model.Member;
import PSH.model.Review;
import PSH.model.Tag;
import PSH.model.UserReviewSummary;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet("/reviews/*")
public class ReviewServlet extends HttpServlet {
    private ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        
        // 로그인 체크
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        
        if (loggedInMember == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다.");
            return;
        }

     // 태그 목록 반환 엔드포인트
        if (pathInfo != null && pathInfo.equals("/tags")) {  // null 체크 추가
            response.setContentType("application/json");
            List<Tag> tags = reviewDAO.getAllTags();
            String json = convertTagsToJson(tags);
            response.getWriter().write(json);
            return;
        }


        if (pathInfo == null || pathInfo.equals("/")) {
            // 리뷰 요약 정보 조회
            UserReviewSummary summary = reviewDAO.getUserReviewSummary(loggedInMember.getId());
            request.setAttribute("summary", summary);
            request.getRequestDispatcher("/WEB-INF/views/reviews/summary.jsp")
                   .forward(request, response);
        } 
        else if (pathInfo.equals("/write")) {
            // 리뷰 작성 폼 요청 처리
            int meetingId = Integer.parseInt(request.getParameter("meetingId"));
            
            // 리뷰 가능한 참가자 목록 조회
            List<Member> reviewableMembers = reviewDAO.getReviewableMembers(meetingId, loggedInMember.getId());
            List<Tag> allTags = reviewDAO.getAllTags();
            
            request.setAttribute("meetingId", meetingId);
            request.setAttribute("reviewableMembers", reviewableMembers);
            request.setAttribute("tags", allTags);
            
            request.getRequestDispatcher("/WEB-INF/views/reviews/write.jsp")
                   .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // 로그인 체크
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        
        if (loggedInMember == null) {
            sendJsonResponse(response, false, "로그인이 필요합니다.");
            return;
        }

        // 리뷰 데이터 파싱
        int toUserId = Integer.parseInt(request.getParameter("toUserId"));
        int meetingId = Integer.parseInt(request.getParameter("meetingId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String[] tagIdsStr = request.getParameterValues("tagIds");
        
        // 이미 리뷰를 작성했는지 확인
        if (reviewDAO.hasReviewed(loggedInMember.getId(), toUserId, meetingId)) {
            sendJsonResponse(response, false, "이미 리뷰를 작성했습니다.");
            return;
        }

        // 리뷰 객체 생성
        Review review = new Review();
        review.setFromUserId(loggedInMember.getId());
        review.setToUserId(toUserId);
        review.setMeetingId(meetingId);
        review.setRating(rating);

        // 태그 ID 리스트 생성
        List<Integer> tagIds = new ArrayList<>();
        if (tagIdsStr != null) {
            for (String tagId : tagIdsStr) {
                tagIds.add(Integer.parseInt(tagId));
            }
        }

        // 리뷰 저장
        boolean success = reviewDAO.createReview(review, tagIds);
        if (success) {
            sendJsonResponse(response, true, "리뷰가 성공적으로 저장되었습니다.");
        } else {
            sendJsonResponse(response, false, "리뷰 저장에 실패했습니다.");
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) 
            throws IOException {
        String json = String.format("{\"success\":%b,\"message\":\"%s\"}", success, message);
        response.getWriter().write(json);
    }

    // 태그 목록을 JSON으로 변환하는 메서드
    private String convertTagsToJson(List<Tag> tags) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) json.append(",");
            Tag tag = tags.get(i);
            json.append(String.format(
                "{\"tagId\":%d,\"tagName\":\"%s\"}",
                tag.getTagId(),
                tag.getTagName()
            ));
        }
        json.append("]");
        return json.toString();
    }
}