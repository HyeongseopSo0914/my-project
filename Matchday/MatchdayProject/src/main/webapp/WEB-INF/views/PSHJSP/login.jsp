<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인 - Matchday</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/login.css?v=2">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
</head>
<body>
    <div class="container">
        <div class="login-wrapper">
            <!-- 왼쪽: 로고와 슬로건 -->
            <div class="left-side">
                <div class="logo">
                <a href="${pageContext.request.contextPath}/" style="display: flex; align-items: center; gap: 15px; text-decoration: none; color: inherit;">
                    <img src="https://res.cloudinary.com/dnjqljait/image/upload/v1736337453/icononly_cytjxl.png" 
                         alt="Matchday Logo" class="logo-img">
                    <div class="logo-text">
                        <h1>MATCHDAY</h1>
                        <p>GAMERS ON PLAY</p>
                    </div>
                    </a>
                </div>
                <div class="slogan">
                    <h2>어서오세요!<br>다시 만나 반가워요.</h2>
                    <p>함께 게임할 친구들을 만나보세요.</p>
                    <div class="image-container">
                        <img src="https://res.cloudinary.com/dnjqljait/image/upload/v1737095523/DALL_E_2025-01-17_15.31.49_-_A_stylized_anime_character_with_spiky_hair_similar_to_the_uploaded_image_enjoying_a_board_game._The_setting_is_a_cozy_indoor_space_with_a_table_a_b_rqzbo5.webp" 
                             alt="Gaming Character" 
                             class="slogan-image">
                    </div>
                </div>
            </div>

            <!-- 오른쪽: 로그인 폼 -->
            <div class="right-side">
                <div class="form-container">
                    <h3>로그인</h3>
                    <% if (request.getAttribute("errorMessage") != null) { %>
                        <div class="error-message">
                            <%= request.getAttribute("errorMessage") %>
                        </div>
                    <% } %>
                    <form action="login" method="post" id="loginForm">
                        <!-- 이메일 입력 -->
                        <div class="input-group">
                            <label>이메일</label>
                            <input type="email" name="email" placeholder="example@email.com" required>
                        </div>

                        <!-- 비밀번호 입력 -->
                        <div class="input-group">
                            <label>비밀번호</label>
                            <input type="password" name="password" placeholder="비밀번호를 입력하세요" required>
                        </div>

                        <!-- 자동 로그인 & 비밀번호 찾기 -->
                        <div class="login-options">
                            <label class="checkbox-label">
                                <input type="checkbox" name="autoLogin">
                                <span>자동 로그인</span>
                            </label>
                            <a href="${pageContext.request.contextPath}/forgot-password" class="forgot-password">
                                비밀번호 찾기
                            </a>
                        </div>

                        <!-- 로그인 버튼 -->
                        <button type="submit" class="login-button">로그인</button>

                        <!-- 회원가입 링크 -->
                        <div class="signup-link">
                            <p>아직 계정이 없으신가요? 
                               <a href="${pageContext.request.contextPath}/register">회원가입</a>
                            </p>
                        </div>
                        <!-- 뒤로가기 버튼 추가 -->
<button type="button" onclick="history.back()" class="back-button">
    뒤로가기
</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>