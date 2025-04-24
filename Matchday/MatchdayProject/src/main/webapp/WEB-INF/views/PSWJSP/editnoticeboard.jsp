<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="PSW.model.Notice" %>
<%@ page import="PSW.dao.NoticeDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/PSWCSS/board.css">
    <title>공지사항 수정</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
</head>
<style>
	h1{
		margin:auto;
		padding: 20px;
		color:black;
		font-size: 25px;
	}	
	p{
		margin:auto;
		padding: 20px;
		color:black;
		margin-left:20px;
	}
	#rpform {
    background-color: #ffffff;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    text-align: left;
    width: auto;
	}
	
	/* 입력 필드 및 셀렉트 박스 스타일 */
	#rpform input[type="text"] {
    width: 500px;  /* 제목 입력창 너비 확장 */
    padding: 10px;
    margin-top: 5px;
    margin-bottom: 15px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 14px;
    box-sizing: border-box;
}

/* 입력 필드 전체 컨테이너 스타일 수정 */
.input-container {
    margin-bottom: 20px;
    width: 100%;
}

/* 제목 레이블과 입력창 정렬 */
.input-title {
    display: flex;
    align-items: center;
    gap: 10px;  /* 레이블과 입력창 사이 간격 */
    margin-bottom: 15px;
}
	
	#rpform input[type="email"],
	#rpform select {
	    width: 100%;
	    padding: 10px;
	    margin-top: 5px;
	    margin-bottom: 15px;
	    border: 1px solid #ccc;
	    border-radius: 4px;
	    font-size: 14px;
	    box-sizing: border-box;
	}
	#rpform button {
    width: 100%;  	
    padding: 10px;
    background-color: black;
    color: white;
    font-size: 16px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
	}
	
	#rpform button:hover {
	    background-color: grey;
	}
	
	/* 텍스트 스타일 */
	#rpform p {
    margin: 0;
    padding: 10px 20px;
    color: black;
}
	#rpform textarea {
	width: 500px;
    height: 200px;/* 크기 조절 비활성화 */
    margin-left:20px;
    margin-bottom: 50px;
    color: black;
	}
	

</style>
<body>
    <%-- ✅ nav.jsp 포함 --%>
    <jsp:include page="nav.jsp" />

    <main class="faq-container">
        <section class="faq-content">
            <div id="top">
                <h1>공지사항 수정</h1>
            </div>
            
            <%
                String noticeId = request.getParameter("noticeId");
                Notice notice = null;

                if (noticeId != null && !noticeId.isEmpty()) {
                    // noticeId로 해당 공지사항 데이터를 가져옴
                    NoticeDAO dao = new NoticeDAO();
                    notice = dao.getNoticeById(Integer.parseInt(noticeId)); // 해당 ID로 공지사항을 가져옴
                }

                if (notice != null) {
            %>

            <form id="rpform" action="${pageContext.request.contextPath}/EditNoticeWriteServlet" method="post">
    <input type="hidden" name="noticeId" value="<%= notice.getNoticeId() %>">
    <div class="input-container">
        <div class="input-title">
            <p>제목:</p>
            <input type="text" id="title" name="title" value="<%= notice.getNoticeTitle() %>" required>
        </div>
        <p>내용:</p>
        <textarea id="content" name="content" rows="5" required><%= notice.getNoticeContent() %></textarea>
    </div>
    <button class="btn" type="submit">공지사항 수정</button>
</form>
			
			
            <% 
                } else {
            %>
            <p>해당 공지사항을 찾을 수 없습니다.</p>
            <% } %>
        </section>
    </main>

    <%-- ✅ footer.jsp 포함 --%>
    <jsp:include page="footer.jsp" />
</body>
</html>
