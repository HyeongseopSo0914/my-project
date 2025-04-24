package PSW.controller;

import PSW.dao.*;

import PSW.model.Bulletin;
import PSW.model.Notice;
import util.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/noticeboard")
public class NoticeBoardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchKeyword = request.getParameter("search");  // 검색어 가져오기
        NoticeDAO noticeDAO = new NoticeDAO();
        List<Notice> notices;

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            // 검색어가 있으면 검색 결과 가져오기
            notices = noticeDAO.searchNotices(searchKeyword);
        } else {
            // 검색어가 없으면 전체 공지사항 가져오기
            notices = noticeDAO.getAllNotices();
        }

        request.setAttribute("notices", notices); // 검색 결과 또는 전체 공지사항 전달
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/PSWJSP/noticeboard.jsp");
        dispatcher.forward(request, response);
    }
}
