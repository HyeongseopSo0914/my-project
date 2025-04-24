<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="PSW.model.*" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/PSWCSS/board.css?after">
    <title>Matchday - board</title>
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
                <h1>자유 게시판</h1>
                <div class="faq-search">
                    <form action="${pageContext.request.contextPath}/freeboard" method="GET">
                        <input type="text" name="search" placeholder="검색어" value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                        <button class="btn" type="submit">검색</button>           
                    	<button class="btn"><a href="${pageContext.request.contextPath}/freewrite">글쓰기</a></button>
                    </form>
                </div>
            </div>
            
            <ul class="faq-list">
			    <%
			        List<Bulletin> bulletins = (List<Bulletin>) request.getAttribute("bulletins");
			        String loggedInNickname = (String) session.getAttribute("nickname"); // 세션에서 닉네임 가져오기
			
			        if (bulletins != null && !bulletins.isEmpty()) {
			            for (Bulletin bulletin : bulletins) {
			    %>
			    <li>
			    	<span class="post-info">
					    <a href="${pageContext.request.contextPath}/freedetail?bulletinId=<%= bulletin.getId() %>">
					        <h3><%= bulletin.getTitle() %></h3>
					        <p>작성자: <%= bulletin.getNickname() %> | 추천: <%= bulletin.getRecommend() %> | 등록일: <%= bulletin.getRegdate() %></p>					        					        
					    </a>
				    </span>
				    <div class="post-actions">
				    <%-- 로그인한 사용자가 작성자일 경우 수정 버튼과 삭제 버튼 표시 --%>
					        <% if (loggedInNickname != null && loggedInNickname.equals(bulletin.getNickname())) { %>				            
					                <a href="${pageContext.request.contextPath}/editfreeboard?id=<%= bulletin.getId() %>">수정</a>
					                <a href="${pageContext.request.contextPath}/DeletefreeboardServlet?id=<%= bulletin.getId() %>">삭제</a>					            
					        <% } %>
					</div>
				</li>
			    <%
			            }
			        } else {
			    %>
			    <li>
			        <p>게시글이 없습니다.</p>
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
