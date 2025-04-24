<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="PSH.model.BoardGame" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>관심 게임 선택 - Matchday</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/signup_games.css?v=2">
</head>
<body>
    <div class="container">
        <div class="signup-wrapper">
            <!-- 왼쪽: 로고와 슬로건 -->
            <div class="left-side">
                <div class="logo">
                    <img src="https://res.cloudinary.com/dnjqljait/image/upload/v1736337453/icononly_cytjxl.png" alt="Matchday Logo" class="logo-img">
                    <div class="logo-text">
                        <h1>MATCHDAY</h1>
                        <p>GAMERS ON PLAY</p>
                    </div>
                </div>
                <div class="slogan">
                    <h2>내 관심 게임을<br>선택해보세요.</h2>
                    <p>관심 게임 목록은'마이페이지'에서<br>언제든지 수정 가능합니다.</p>
                </div>
            </div>

            <!-- 오른쪽: 게임 선택 -->
            <div class="right-side">
                <div class="games-container">
                    <h3>관심 게임 선택</h3>
                    <p class="sub-text">하트를 눌러 관심있는 게임을 선택해주세요.<br>목록에 없는 게임도 검색을 통해 찾으실 수 있어요.</p>
                    
                    <div class="search-container">
                        <input type="text" id="gameSearch" class="game-search" 
       placeholder="게임 이름을 입력하세요" 
       value="<%= request.getAttribute("searchKeyword") != null ? request.getAttribute("searchKeyword") : "" %>">

                        <span class="search-icon">🔍</span>
                    </div>

                    <div class="games-grid">
                        <% 
                        List<BoardGame> games = (List<BoardGame>) request.getAttribute("games");
                        if(games != null) {
                            for(BoardGame game : games) {
                        %>
                            <div class="game-card">
                                <div class="game-image">
                                    <img src="<%=game.getImageUrl()%>" alt="<%=game.getName()%>">
                                    <button class="heart-button <%=game.isSelected() ? "selected" : ""%>" 
                                            data-game-id="<%=game.getGameId()%>">
                                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 <%=game.isSelected() ? "filled" : ""%>" 
                                             fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                                                  d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                                        </svg>
                                    </button>
                                </div>
                                <h4><%=game.getName()%></h4>
                            </div>
                        <%
                            }
                        }
                        %>
                    </div>

                    <div class="button-group">
                        <button type="button" onclick="location.href='<%=request.getContextPath()%>/register'" class="back-button">
                            이전 단계로
                        </button>
                        <button type="button" onclick="submitFavoriteGames()" class="complete-button">
                            가입 완료하기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

<script>
//선택된 게임들을 추적하기 위한 Set
let selectedGames = new Set();

// 페이지 로드 시 이미 선택된 게임들을 Set에 추가
document.addEventListener('DOMContentLoaded', function() {
    const selectedButtons = document.querySelectorAll('.heart-button.selected');
    selectedButtons.forEach(button => {
        selectedGames.add(button.dataset.gameId);
    });
    attachHeartButtonListeners();
});

// 하트 버튼 이벤트 리스너 연결 함수
function attachHeartButtonListeners() {
    document.querySelectorAll('.heart-button').forEach(button => {
        button.removeEventListener('click', handleHeartClick);
        button.addEventListener('click', handleHeartClick);
    });
}

// 하트 버튼 클릭 핸들러
function handleHeartClick(event) {
    const button = event.currentTarget;
    const gameId = button.dataset.gameId;
    const svg = button.querySelector('svg');
    const isSelected = !button.classList.contains('selected');
    
    // 서버에 선택 상태 업데이트 요청
    const action = isSelected ? 'add' : 'remove';
    fetch('${pageContext.request.contextPath}/register/games', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        body: 'action=' + action + '&gameId=' + gameId
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('서버 요청에 실패했습니다.');
        }
        return response.json();
    })
    .then(data => {
        // 성공 시 UI 업데이트와 Set 업데이트
        if (isSelected) {
            button.classList.add('selected');
            svg.classList.add('filled');
            selectedGames.add(gameId);
        } else {
            button.classList.remove('selected');
            svg.classList.remove('filled');
            selectedGames.delete(gameId);
        }
    })
    .catch(error => {
        console.error('에러:', error);
        alert('게임 선택 상태를 업데이트하는데 실패했습니다.');
    });
}

// 검색 기능 구현
let searchTimeout = null;
document.getElementById('gameSearch').addEventListener('input', function(e) {
    const searchText = e.target.value.trim();
    
    if (searchTimeout) {
        clearTimeout(searchTimeout);
    }
    
    searchTimeout = setTimeout(() => {
        const url = searchText === '' ? 
            '${pageContext.request.contextPath}/register/games' : 
            '${pageContext.request.contextPath}/register/games?search=' + encodeURIComponent(searchText);
        
        fetch(url)
            .then(response => response.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');
                const newGamesGrid = doc.querySelector('.games-grid');
                const currentGamesGrid = document.querySelector('.games-grid');
                
                if (newGamesGrid && currentGamesGrid) {
                    currentGamesGrid.innerHTML = newGamesGrid.innerHTML;
                    // 기존 선택 상태 복원
                    selectedGames.forEach(gameId => {
                        const button = document.querySelector(`.heart-button[data-game-id="${gameId}"]`);
                        if (button) {
                            button.classList.add('selected');
                            button.querySelector('svg').classList.add('filled');
                        }
                    });
                    attachHeartButtonListeners();
                }
            })
            .catch(error => console.error('Error:', error));
    }, 300);
});

// 선택한 게임 최종 제출
function submitFavoriteGames() {
    if (selectedGames.size === 0) {
        alert('최소 1개 이상의 게임을 선택해주세요.');
        return;
    }

    // FormData 대신 JSON 형식으로 전송
    fetch('${pageContext.request.contextPath}/register/games', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            gameIds: Array.from(selectedGames)
        })
    })
    .then(async response => {
        if (response.ok) {
            window.location.href = '${pageContext.request.contextPath}/register-success';
        } else {
            const errorMessage = await response.text();
            throw new Error(errorMessage || '게임 등록에 실패했습니다.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert(error.message);
    });
}
</script>

</body>
</html>