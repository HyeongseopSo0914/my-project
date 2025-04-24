package PSH.controller;

import PSH.dao.BoardGameDAO;
import PSH.model.BoardGame;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/games/select")
public class GameSelectServlet extends HttpServlet {
    private BoardGameDAO boardGameDAO = new BoardGameDAO();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String searchKeyword = request.getParameter("search");
        List<BoardGame> games;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            games = boardGameDAO.searchGames(searchKeyword.trim());
        } else {
            games = boardGameDAO.getRecommendedGames();
        }
        
        String jsonResponse = gson.toJson(games);
        response.getWriter().write(jsonResponse);
    }
}