package PSH.controller;

import PSH.dao.BoardGameDAO;
import PSH.model.BoardGame;
import PSH.model.Member;

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
import java.util.stream.Collectors;
import java.io.PrintWriter;

@WebServlet("/register/games")
public class SignupGamesServlet extends HttpServlet {
    private BoardGameDAO boardGameDAO = new BoardGameDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 세션에서 임시 회원 정보 확인
        HttpSession session = request.getSession();
        Member tempMember = (Member) session.getAttribute("tempMember");

        if (tempMember == null) {
            // 임시 회원 정보가 없으면 회원가입 첫 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        // 검색 키워드 가져오기
        String searchKeyword = request.getParameter("search");
        List<BoardGame> games;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // 검색 키워드가 있으면 검색 결과 반환
            games = boardGameDAO.searchGames(searchKeyword.trim());
        } else {
            // 검색 키워드가 없으면 추천 게임 목록 반환
            games = boardGameDAO.getRecommendedGames();
        }

        // 선택된 게임 ID 목록 가져오기 (임시로 세션에 저장된 선택된 게임 ID 사용)
        List<Integer> selectedGameIds = (List<Integer>) session.getAttribute("selectedGameIds");
        if (selectedGameIds == null) {
            selectedGameIds = new ArrayList<>();
        }

        // 각 게임이 선택되었는지 여부를 설정
        for (BoardGame game : games) {
            if (selectedGameIds.contains(game.getGameId())) {
                game.setSelected(true);
            }
        }
        
        request.setAttribute("games", games);
        request.setAttribute("searchKeyword", searchKeyword); // 검색창에 키워드 유지를 위해

        // 게임 선택 페이지로 포워드
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/signup_games.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // action 파라미터 확인
        String action = request.getParameter("action");
        
        // action 파라미터가 있으면 ajax 요청 처리 (게임 선택/해제)
        if (action != null) {
            handleAjaxRequest(request, response, action);
            return;
        }

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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("게임을 선택해주세요.");
            return;
        }

        // 게임 ID 리스트 생성
        List<Integer> gameIds = Arrays.stream(gameIdsStr.split(","))
                                   .filter(s -> !s.isEmpty())
                                   .map(Integer::parseInt)
                                   .collect(Collectors.toList());

        // 세션에서 임시 회원 정보 가져오기
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("tempMember");

        if (member == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("회원 정보를 찾을 수 없습니다.");
            return;
        }

        try {
            // 선택한 게임들을 관심 게임으로 등록
            boolean success = boardGameDAO.addFavoriteGames(member.getId(), gameIds);

            if (success) {
                // 회원가입 완료 후 세션의 임시 데이터 삭제
                session.removeAttribute("tempMember");
                session.removeAttribute("selectedGameIds");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("게임 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("처리 중 오류가 발생했습니다.");
        }
    }
    private void handleAjaxRequest(HttpServletRequest request, HttpServletResponse response, String action) 
            throws IOException {
        String gameIdStr = request.getParameter("gameId");

        if (gameIdStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int gameId;
        try {
            gameId = Integer.parseInt(gameIdStr);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        HttpSession session = request.getSession();
        List<Integer> selectedGameIds = (List<Integer>) session.getAttribute("selectedGameIds");
        if (selectedGameIds == null) {
            selectedGameIds = new ArrayList<>();
        }

        if (action.equals("add")) {
            if (!selectedGameIds.contains(gameId)) {
                selectedGameIds.add(gameId);
            }
        } else if (action.equals("remove")) {
            selectedGameIds.remove(Integer.valueOf(gameId));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        session.setAttribute("selectedGameIds", selectedGameIds);

        // JSON 응답 반환
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"status\":\"success\"}");
    }

    private void handleFinalSubmission(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // 선택된 게임 ID들을 배열로 받기
        String[] gameIdStrings = request.getParameterValues("gameIds");

        if (gameIdStrings == null || gameIdStrings.length == 0) {
            // 최소 1개 이상의 게임을 선택하지 않은 경우
            request.setAttribute("error", "최소 1개 이상의 게임을 선택해주세요.");
            doGet(request, response); // 다시 게임 선택 페이지로 포워드
            return;
        }

        // 문자열 배열을 Integer 리스트로 변환
        List<Integer> gameIds = Arrays.stream(gameIdStrings)
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toList());

        // 세션에서 임시 회원 정보 가져오기
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("tempMember");

        if (member == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 선택한 게임들을 관심 게임으로 등록
        boolean success = boardGameDAO.addFavoriteGames(member.getId(), gameIds);

        if (success) {
            // 회원가입 완료 후 세션의 임시 데이터 삭제
            session.removeAttribute("tempMember");
            response.sendRedirect(request.getContextPath() + "/register-success");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
