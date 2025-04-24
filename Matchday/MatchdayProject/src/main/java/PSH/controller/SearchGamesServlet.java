package PSH.controller;

import PSH.dao.BoardGameDAO;
import PSH.model.BoardGame;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/create_meeting/search-games")
public class SearchGamesServlet extends HttpServlet {
    private BoardGameDAO boardGameDAO = new BoardGameDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String searchKeyword = request.getParameter("search");
        List<BoardGame> games;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            games = boardGameDAO.searchGames(searchKeyword.trim());
        } else {
            games = boardGameDAO.getRecommendedGames();
        }
        
        request.setAttribute("games", games);
        request.getRequestDispatcher("/WEB-INF/views/PSHJSP/game_list.jsp")
               .forward(request, response);
    }
}