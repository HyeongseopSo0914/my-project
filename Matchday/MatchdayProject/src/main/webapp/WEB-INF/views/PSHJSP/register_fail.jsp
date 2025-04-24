<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 실패</title>
    <link rel="stylesheet" href="../css/signup.css">
</head>
<body>
    <div class="container">
        <h1>회원가입에 실패했습니다.</h1>
        <p><%= request.getAttribute("errorMessage") != null 
                ? request.getAttribute("errorMessage") 
                : "알 수 없는 오류가 발생했습니다. 다시 시도해주세요." %></p>
        <a href="signup.jsp" class="button">회원가입 페이지로 돌아가기</a>
    </div>
</body>
</html>
