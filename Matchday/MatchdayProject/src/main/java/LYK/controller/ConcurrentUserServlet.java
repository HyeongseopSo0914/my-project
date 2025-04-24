package LYK.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import LYK.util.SessionCounterListener;

@WebServlet("/Concurrent")
public class ConcurrentUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(javax.servlet.http.HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        // 🔥 CORS 문제 해결을 위한 응답 헤더 추가
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.getWriter().write(String.valueOf(SessionCounterListener.getActiveSessions()));
    }
}
