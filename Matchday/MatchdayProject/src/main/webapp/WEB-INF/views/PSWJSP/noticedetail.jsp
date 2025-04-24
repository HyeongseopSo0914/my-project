<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="PSW.model.Notice" %>
<%@ page import="util.DBUtil" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/PSWCSS/board.css">
    <title>공지사항 상세</title>
</head>
<style>
.bulletin-detail {
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    padding: 20px;
    margin: 20px auto;
    width: 80%;
    max-width: 800px;
    text-align: center;
}

.bulletin-detail h2, .bulletin-detail p {
    color: #000;
    font-family: 'Arial', sans-serif;
    margin-bottom: 20px;
}

.bulletin-content p {
    color: #333;
    font-size: 1.1rem;
    line-height: 1.6;
}

.button-container {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;
}

.w-btn {
    position: relative;
    border: none;
    display: inline-block;
    padding: 15px 30px;
    border-radius: 15px;
    font-family: "paybooc-Light", sans-serif;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
    text-decoration: none;
    font-weight: 600;
    transition: 0.25s;
}

.w-btn-indigo {
    background-color: black;
    color: white;
}

.w-btn-indigo:hover {
    background-color: #333;
    transform: scale(1.02);
}
</style>
<body>
    <jsp:include page="nav.jsp" />

    <main class="faq-container">
        <section class="faq-content">
            <%
                Notice notice = (Notice) request.getAttribute("notice");
                if (notice != null) {
            %>
            <div class="bulletin-detail">
                <h1>공지사항 상세</h1>
                <br>
                <h2><%= notice.getNoticeTitle() %></h2>
                <p>등록일: <%= notice.getNoticeRegdate() %></p>
                <div class="bulletin-content">
                    <p><%= notice.getNoticeContent().replace("\n", "<br>") %></p>
                </div>
                <div class="button-container">
                    <button class="w-btn w-btn-indigo" onclick="history.back()">뒤로가기</button>
                    
                    <% 
                        String loggedInNickname = (String) session.getAttribute("nickname");
                        if (loggedInNickname != null && loggedInNickname.contains("운영자")) { 
                    %>
                        <button class="w-btn w-btn-indigo" onclick="location.href='${pageContext.request.contextPath}/editnoticeboard?noticeId=<%= notice.getNoticeId() %>'">수정</button>
                        <button class="w-btn w-btn-indigo" onclick="location.href='${pageContext.request.contextPath}/Deletenotice?noticeId=<%= notice.getNoticeId() %>'">삭제</button>
                    <% } %>
                </div>
            </div>
            <%
                } else {
            %>
            <p>해당 공지사항을 찾을 수 없습니다.</p>
            <%
                }
            %>
        </section>
    </main>

    <jsp:include page="footer.jsp" />
</body>
</html>