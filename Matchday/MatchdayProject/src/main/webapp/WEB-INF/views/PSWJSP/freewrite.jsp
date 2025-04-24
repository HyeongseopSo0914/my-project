<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/PSWCSS/board.css">
    <title>Matchday - borad</title>
    <script>
        window.onload = function() {
            const errorMsg = "<%= request.getParameter("error") != null ? request.getParameter("error") : "" %>";
            if (errorMsg === "not_logged_in") {
                alert("로그인이 필요합니다.");
                window.location.href = "<%= request.getContextPath() %>/login";
            } else if (errorMsg === "db_error") {
                alert("글 작성 중 오류가 발생했습니다. 다시 시도해주세요.");
            }
        };
    </script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
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
	    margin: 0 0 10px;
	    font-size: 14px;
	    color: #333333;
	}
	#rpform textarea {
	width: 500px;
    height: 200px;/* 크기 조절 비활성화 */
    margin-left:20px;
    margin-bottom: 50px;
    color: black;
	}


</style>
</head>
<body class="bg-gray-100">
	<header>
	    <jsp:include page="nav.jsp" />
	 </header>
	  <main class="faq-container">
        <section class="faq-content">
        	<div id="top">
	            <h1>글쓰기</h1>
	            <p style = "margin: auto;padding: 10px;color: grey;margin-left: 20px;">
	            게시판 이용 매너를 준수하여 작성바랍니다.</p> 
            </div>          
			   
			    <form id="rpform" action="${pageContext.request.contextPath}/freewrite" method="post">
				    <p>제목 <input type="text" name="title" required></p>
				    <p>내용</p>
				    <textarea name="content" placeholder="상세 내용을 입력해 주세요..." required></textarea>
				    <button type="submit">글쓰기</button>
				</form>			
        </section>
    </main>
    
    <jsp:include page="footer.jsp" />
    
</body>
</html>