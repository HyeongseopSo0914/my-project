<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 완료 - Matchday</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/register_success.css">
</head>
<body>
    <div class="container">
        <div class="success-wrapper">
            <!-- 로고 섹션 -->
            <div class="logo">
                <img src="https://res.cloudinary.com/dnjqljait/image/upload/v1736337453/icononly_cytjxl.png" alt="Matchday Logo" class="logo-img">
                <div class="logo-text">
                    <h1>MATCHDAY</h1>
                    <p>GAMERS ON PLAY</p>
                </div>
            </div>

            <!-- 환영 메시지 -->
            <div class="welcome-message">
                <h2>환영합니다!</h2>
                <p>Matchday의 회원이 되신 것을 축하드립니다.<br>
                이제 함께 게임을 즐겨보세요.</p>
            </div>

            <!-- 버튼 그룹 -->
            <div class="button-group">
                <a href="${pageContext.request.contextPath}/" class="primary-button">메인으로</a>
                <a href="${pageContext.request.contextPath}/login" class="secondary-button">로그인</a>
            </div>
        </div>
    </div>
</body>
</html>