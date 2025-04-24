package PSW.controller;

import PSW.model.Bulletin;
import util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/freedetail")
public class FreeboardDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
        if (session.getAttribute("loggedInMember") == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
    	request.getRequestDispatcher("/WEB-INF/views/PSWJSP/freedetail.jsp").forward(request, response);
    }
    
}