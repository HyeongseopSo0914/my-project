package PSH.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import PSH.service.MemberService;

@WebServlet("/check-nickname")
public class CheckNicknameServlet extends HttpServlet {
    private MemberService memberService = new MemberService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String nickname = request.getParameter("nickname");
        boolean exists = memberService.checkNicknameDuplicate(nickname);

        String json = String.format("{\"available\": %b}", !exists);
        response.getWriter().write(json);
    }}