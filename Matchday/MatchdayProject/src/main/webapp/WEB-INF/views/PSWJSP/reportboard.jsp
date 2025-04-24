<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="PSW.model.Report" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/PSWCSS/board.css?after">
    <title>Matchday - 신고 게시판</title>
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

    <%-- ✅ 네비게이션 포함 --%>
    <jsp:include page="nav.jsp" />

    <main class="faq-container">
        <section class="faq-content">
            <div id="top">
                <h1>🚨 신고 게시판</h1>
                <div class="faq-search">
                    <form action="${pageContext.request.contextPath}/reportboard" method="GET">
                        <input type="text" name="search" placeholder="검색어" value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                        <button class="btn" type="submit">검색</button>
                        <button class="btn"><a href="${pageContext.request.contextPath}/reportwrite">신고하기</a></button>
                    </form>
                   
                </div>
            </div>
            
			<ul class="faq-list">
			    <%
			        List<Report> reports = (List<Report>) request.getAttribute("reports");
			    String loggedInNickname = (String) session.getAttribute("nickname"); // 세션에서 닉네임 가져오기
			
			        if (reports != null && !reports.isEmpty()) {
			            for (Report report : reports) {
			    %>
			    <li>
			    	<span class="post-info">
			        <a href="${pageContext.request.contextPath}/reportdetail?reportId=<%= report.getId() %>">
			            <h3><%= report.getTitle() %></h3>
			            <p>작성자: <%= report.getNickname() %> | 등록일: <%= report.getRegdate() %></p>
			            
			        </a>
			        </span>
			        <div class="post-actions">
			        	<% if (loggedInNickname != null && loggedInNickname.equals(report.getNickname())) { %>
		                    <a href="${pageContext.request.contextPath}/editreportboard?id=<%= report.getId() %>" style="text-align: center">수정</a>
		                    <a href="${pageContext.request.contextPath}/DeletereportboardServlet?id=<%= report.getId() %>" style="text-align: center">삭제</a>
		                <% } %>
		            </div>
			    </li>
			    <%
			            }
			        } else {
			    %>
			    <li>
			        <p>🚨 신고된 게시글이 없습니다.</p>
			    </li>
			    <%
			        }
			    %>
			</ul>

        </section>
    </main>

    <%-- ✅ 푸터 포함 --%>
    <jsp:include page="footer.jsp" />

</body>
</html>
