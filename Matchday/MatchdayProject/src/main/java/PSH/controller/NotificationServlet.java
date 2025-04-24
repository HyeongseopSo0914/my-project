package PSH.controller;

import PSH.dao.NotificationDAO;
import PSH.model.Member;
import PSH.model.Notification;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/notifications/*")
public class NotificationServlet extends HttpServlet {
    private NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 로그인 체크
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        
        if (loggedInMember == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"로그인이 필요합니다.\"}");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 기본 알림 목록 (최근 20개)
                List<Notification> notifications = notificationDAO.getRecentNotifications(loggedInMember.getId());
                String json = convertNotificationsToJson(notifications);
                response.getWriter().write(json);
            } 
            else if (pathInfo.equals("/unread-count")) {
                // 읽지 않은 알림 개수
                int count = notificationDAO.getUnreadCount(loggedInMember.getId());
                response.getWriter().write("{\"count\":" + count + "}");
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Invalid endpoint\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"서버 오류가 발생했습니다.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 로그인 체크
        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        
        if (loggedInMember == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"로그인이 필요합니다.\"}");
            return;
        }

        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo.equals("/mark-read")) {
                // 단일 알림 읽음 처리
                String notificationId = request.getParameter("notificationId");
                if (notificationId != null) {
                    boolean success = notificationDAO.markAsRead(Integer.parseInt(notificationId));
                    response.getWriter().write("{\"success\":" + success + "}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"알림 ID가 필요합니다.\"}");
                }
            }
            else if (pathInfo.equals("/mark-all-read")) {
                // 모든 알림 읽음 처리
                boolean success = notificationDAO.markAllAsRead(loggedInMember.getId());
                response.getWriter().write("{\"success\":" + success + "}");
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Invalid endpoint\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"서버 오류가 발생했습니다.\"}");
        }
    }

    // 알림 목록을 JSON 형식으로 변환
    private String convertNotificationsToJson(List<Notification> notifications) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < notifications.size(); i++) {
            if (i > 0) json.append(",");
            Notification notification = notifications.get(i);
            json.append("{")
                .append("\"id\":").append(notification.getId()).append(",")
                .append("\"type\":\"").append(notification.getType()).append("\",")
                .append("\"content\":\"").append(notification.getContent()).append("\",")
                .append("\"isRead\":").append(notification.isRead()).append(",")
                .append("\"createdAt\":\"").append(notification.getCreatedAt()).append("\"")
                .append("}");
        }
        json.append("]");
        return json.toString();
    }
}