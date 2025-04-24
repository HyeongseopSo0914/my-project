package PSH.controller;

import PSH.model.Member;
import PSH.model.BoardGame;
import PSH.dao.BoardGameDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/mypage/add-games")
public class AddGamesServlet extends HttpServlet {
    private BoardGameDAO boardGameDAO = new BoardGameDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        
        if (loggedInMember != null) {
            List<Integer> favoriteGameIds = boardGameDAO.getFavoriteGameIds(loggedInMember.getId());
            request.setAttribute("favoriteGameIds", favoriteGameIds);
        }

        String searchKeyword = request.getParameter("search");
        System.out.println("Search keyword received: " + searchKeyword);

        List<BoardGame> games;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            games = boardGameDAO.searchGames(searchKeyword.trim());
            System.out.println("Search results count: " + games.size());
        } else {
            games = boardGameDAO.getRecommendedGames();
            System.out.println("Recommended games count: " + games.size());
        }
        
        request.setAttribute("games", games);
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/game_list.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // 세션에서 로그인 정보 확인
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            sendJsonResponse(response, false, "로그인이 필요합니다.");
            return;
        }

        try {
            // JSON 요청 읽기
            StringBuilder sb = new StringBuilder();
            String line;
            try (var reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            // JSON 파싱
            String json = sb.toString();
            String gameIdsStr = json.replaceAll("[^0-9,]", ""); // 숫자와 콤마만 남김
            
            if (gameIdsStr.isEmpty()) {
                sendJsonResponse(response, false, "게임 ID가 필요합니다.");
                return;
            }

            // 게임 ID 리스트 생성
            List<Integer> gameIds = new ArrayList<>();
            for (String idStr : gameIdsStr.split(",")) {
                if (!idStr.isEmpty()) {
                    gameIds.add(Integer.parseInt(idStr));
                }
            }

            // 게임 추가 처리
            boolean success = boardGameDAO.addFavoriteGames(loggedInMember.getId(), gameIds);
            sendJsonResponse(response, success, success ? "게임이 추가되었습니다." : "게임 추가에 실패했습니다.");

        } catch (Exception e) {
            sendJsonResponse(response, false, "처리 중 오류가 발생했습니다.");
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) 
            throws IOException {
        String json = String.format("{\"success\":%b,\"message\":\"%s\"}", success, message);
        response.getWriter().write(json);
    }
}