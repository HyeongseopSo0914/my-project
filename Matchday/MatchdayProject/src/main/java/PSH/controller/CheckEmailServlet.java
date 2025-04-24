package PSH.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import PSH.service.MemberService;

@WebServlet("/checkEmail")
public class CheckEmailServlet extends HttpServlet {
    private MemberService memberService = new MemberService();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        boolean isDuplicate = memberService.checkEmailDuplicate(email);
        
        String jsonResponse = gson.toJson(new EmailCheckResponse(isDuplicate));
        response.getWriter().write(jsonResponse);
    }
    
    private class EmailCheckResponse {
        private boolean isDuplicate;
        
        public EmailCheckResponse(boolean isDuplicate) {
            this.isDuplicate = isDuplicate;
        }
        
        public boolean getIsDuplicate() {
            return isDuplicate;
        }
    }
}