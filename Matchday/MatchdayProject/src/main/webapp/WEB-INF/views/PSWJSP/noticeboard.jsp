<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="PSW.model.Notice" %> <!-- Notice 모델 클래스 import -->
<%@ page import="PSH.model.Member" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/PSWCSS/board.css?v=2">
    <title>Matchday - 공지사항</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
</head>
<style>
	body {
    display: flex;
    flex-direction: column;
    min-height: 100vh; /* 페이지 높이를 최소 100vh로 설정 */
	}	
	main {
	    flex-grow: 1; /* main 요소가 남은 공간을 차지하도록 설정 */
	}
</style>
<body>
    <%-- ✅ nav.jsp 포함 --%>
    <jsp:include page="nav.jsp" />

    <main class="faq-container">
        <section class="faq-content">
            <div id="top">
                <h1>공지사항</h1>
                <div class="faq-search">
                    <form action="${pageContext.request.contextPath}/noticeboard" method="GET">
                        <input type="text" name="search" placeholder="검색어" value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                        <button class="btn" type="submit">검색</button>           

                        <%
    // Member 객체에서 정보 가져오기
    Member loggedInMember = (Member) session.getAttribute("loggedInMember");
    String loggedInNickname = (loggedInMember != null) ? loggedInMember.getNickname() : null;
    
    // 디버깅용 출력
    System.out.println("현재 로그인된 사용자: " + (loggedInMember != null ? loggedInMember.getEmail() : "없음"));
    System.out.println("닉네임: " + loggedInNickname);
    
    if (loggedInMember != null && "운영자".equals(loggedInNickname)) {  
%>
    <button class="btn"><a href="${pageContext.request.contextPath}/noticewrite">글쓰기</a></button>
<% } %>
                    </form>
                </div>
            </div>
            
           <ul class="faq-list px-4"> <!-- 양쪽 패딩 추가 -->
    <%
        List<Notice> notices = (List<Notice>) request.getAttribute("notices");
        
        if (notices != null && !notices.isEmpty()) {
            for (Notice notice : notices) {
    %>
    <li class="text-left py-3 border-b border-gray-200"> <!-- 세로 패딩과 하단 보더 추가 -->
        <a href="${pageContext.request.contextPath}/noticedetail?noticeId=<%= notice.getNoticeId() %>">
            <h3 class="font-bold text-lg mb-1"><%= notice.getNoticeTitle() %></h3>
            <p class="text-gray-500 text-sm"><%= notice.getNoticeRegdate() %></p>
        </a>
        <% 
        if (loggedInMember != null && "운영자".equals(loggedInNickname)) {  
        %>
        <div class="flex space-x-4 mt-2 text-sm">
            <a href="${pageContext.request.contextPath}/editnoticeboard?noticeId=<%= notice.getNoticeId() %>" 
               class="text-blue-500 hover:text-blue-700">수정</a>
            <a href="${pageContext.request.contextPath}/Deletenotice?noticeId=<%= notice.getNoticeId() %>" 
               class="text-red-500 hover:text-red-700">삭제</a>
        </div>
        <% } %>
    </li>
    <%
            }
        } else {
    %>
    <li class="text-center text-gray-500 py-4">
        <p>검색된 공지사항이 없습니다.</p>
    </li>
    <%
        }
    %>
</ul>
        </section>
        
    </main>

    <%-- ✅ footer.jsp 포함 --%>
    <jsp:include page="footer.jsp" />
</body>
</html>
