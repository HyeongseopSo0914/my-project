<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Collections" %>
<%@ page import="PSH.model.BoardGame" %>

<style>
    /* 모달 기본 스타일 */
/* 모달 기본 스타일 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: none;
    z-index: 1050;
    align-items: center;
    justify-content: center;
    height: 100%;
}

.modal-container {
    background-color: white;
    border-radius: 0.5rem;
    width: 95%;
    max-width: 56rem;
    height: auto; /* 변경 */
    min-height: 60vh; /* 추가 */
    max-height: 90vh; /* 변경 */
    display: flex;
    flex-direction: column;
    position: relative; /* 추가 */
}

/* 모달 헤더/푸터 */
.modal-header, .modal-footer {
    flex-shrink: 0;
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    font-size: 1.25rem;
    font-weight: 600;
}

.close-button {
    font-size: 1.5rem;
    color: #666;
    background: none;
    border: none;
    cursor: pointer;
    padding: 0.5rem;
}

.modal-footer {
    border-top: 1px solid #e5e7eb;
    border-bottom: none;
    justify-content: flex-end;
    gap: 1rem;
}

/* 버튼 스타일 */
.save-button {
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    background-color: black;
    color: white;
    font-weight: 500;
    border: none;
}

.cancel-button {
    padding: 0.5rem 1rem;
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    background-color: white;
    color: #374151;
    font-weight: 500;
}

/* 모달 콘텐츠 */
.modal-content {
    flex: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    position: relative;
    min-height: 200px; /* 추가 */
    height: 100%; /* 추가 */
    -webkit-overflow-scrolling: touch; /* 추가 */
}

/* 게임 그리드 */
.games-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
    padding: 1.5rem;
    flex-grow: 1;
    overflow-y: auto;
}

/* 스크롤바 스타일링 */
.modal-content::-webkit-scrollbar, 
.games-grid::-webkit-scrollbar {
    width: 8px;
}

.modal-content::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
}

.modal-content::-webkit-scrollbar-thumb {
    background-color: rgba(0,0,0,0.2);
    border-radius: 4px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0,0,0,0.3);
}

/* 검색 컨테이너 */
.search-container {
    position: sticky;
    top: 0;
    z-index: 11;
    background: white;
    padding: 1rem;
    border-bottom: 1px solid #e5e7eb;
    width: 100%; /* 추가 */
}
#modalGameSearch {
    width: 100%;
    padding: 0.75rem 1rem;
    padding-right: 2.5rem;
    border: 2px solid black;
    border-radius: 0.5rem;
    font-size: 0.875rem;
    height: 44px; /* 추가 */
    visibility: visible; /* 추가 */
    opacity: 1; /* 추가 */
}

.search-icon {
    position: absolute;
    right: 2.25rem;
    top: 50%;
    transform: translateY(-50%);
    color: #9ca3af;
    pointer-events: none;
}

/* 모달 표시 클래스 */
.modal-overlay.show {
    display: flex !important;
}

/* 모바일 최적화 */
@media (max-width: 768px) {
    /* 모달이 표시될 때 body 스크롤 방지 */
    body.modal-open {
        overflow: hidden;
        position: fixed;
        width: 100%;
    }

    .modal-overlay {
        padding: 0;
        background-color: white;
    }

    .modal-container {
        width: 100%;
        height: 100%;
        max-height: none;
        border-radius: 0;
        position: fixed;
        top: 0;
        left: 0;
        margin: 0;
    }


    /* 모달 헤더 */
    .modal-header {
        padding: 1rem;
        position: sticky;
        top: 0;
        background: white;
        z-index: 10;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .modal-header h3 {
        font-size: 1.25rem;
    }

    .close-button {
        padding: 0.5rem;
    }

    /* 검색 영역 */
    .search-container {
        position: sticky;
        top: 0;
        background: white;
        z-index: 9;
        padding: 0.75rem 1rem;
        margin: 0;
        border-bottom: 1px solid #e5e7eb;
    }

    #modalGameSearch {
        height: 40px;
        font-size: 14px;
    }

    /* 게임 그리드 */
    .games-grid {
        height: auto;
        min-height: 300px;
        padding-bottom: 100px; /* 푸터 높이만큼 여백 */
    }

    .game-card {
        height: auto;
    }

    .game-image {
        height: 120px;
    }

    /* 하트 버튼 */
    .heart-button {
        width: 32px;
        height: 32px;
    }

    .heart-button svg {
        width: 18px;
        height: 18px;
    }

    /* 모달 푸터 */
    .modal-footer {
        position: fixed;
        bottom: 0;
        left: 0;
        right: 0;
        background: white;
        padding: 1rem;
        z-index: 12;
    }

    .cancel-button,
    .save-button {
        padding: 0.75rem 1rem;
        font-size: 14px;
        flex: 1;
    }

    /* 모달 콘텐츠 영역 */
     .modal-content {
        height: calc(100% - 120px); /* 헤더와 푸터 높이 고려 */
    }
}

/* 더 작은 화면 대응 */
@media (max-width: 380px) {
    .games-grid {
        grid-template-columns: 1fr;
        gap: 0.5rem;
    }

    .game-image {
        height: 140px;
    }
}
@supports (-webkit-touch-callout: none) {
    .modal-container {
        height: -webkit-fill-available;
    }
    
    .modal-content {
        height: calc(-webkit-fill-available - 120px);
    }
}
</style>

<div id="addGamesModal" class="modal-overlay">
    <div class="modal-container">
        <div class="modal-header">
            <h3>관심 게임 추가</h3>
            <button onclick="closeModal()" class="close-button">×</button>
        </div>

        <div class="modal-content">
            <div class="search-container">
                <input type="text" id="modalGameSearch" class="game-search" placeholder="게임 검색" />
                <span class="search-icon">🔍</span>
            </div>

            <div class="games-grid">
                <!-- 게임 목록이 여기에 들어갑니다 -->
            </div>
        </div>

        <div class="modal-footer">
            <button onclick="closeModal()" class="cancel-button">취소</button>
            <button onclick="saveSelectedGames()" class="save-button">추가하기</button>
        </div>
    </div>
</div>

<script>
var selectedGames = new Set();
var searchTimeout = null;

//게임 검색 함수
function searchGames(searchText) {
    var baseUrl = contextPath + '/mypage/add-games';
    var url = baseUrl;
    if (searchText) {
        url = baseUrl + '?search=' + encodeURIComponent(searchText);
    }
    
    fetch(url, {
        method: 'GET',
        headers: {
            'Accept': 'text/html',
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(function(response) {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.text();
    })
    .then(function(html) {
        var gamesGrid = document.querySelector('.games-grid');
        if (gamesGrid) {
            gamesGrid.innerHTML = html;
            
            // 기존에 선택된 게임들을 다시 표시
            var heartButtons = document.querySelectorAll('.heart-button');
            heartButtons.forEach(function(heartButton) {
                var gameId = heartButton.dataset.gameId;
                if (selectedGames.has(gameId)) {
                    var svg = heartButton.querySelector('svg');
                    svg.classList.add('filled');
                    heartButton.classList.add('selected');
                }
            });
            
            attachHeartButtonListeners();
        }
    })
    .catch(function(error) {
        console.error('Search error:', error);
        var gamesGrid = document.querySelector('.games-grid');
        if (gamesGrid) {
            gamesGrid.innerHTML = '<div class="p-4 text-center text-red-500">검색 중 오류가 발생했습니다.</div>';
        }
    });
}

// 검색 입력 이벤트 리스너
document.getElementById('modalGameSearch').addEventListener('input', function(e) {
    var searchText = e.target.value.trim();
    
    if (searchTimeout) {
        clearTimeout(searchTimeout);
    }
    
    searchTimeout = setTimeout(function() {
        searchGames(searchText);
    }, 300);
});

// 하트 버튼 클릭 이벤트 핸들러
function handleHeartClick(event) {
    if (this.classList.contains('disabled')) {
        event.preventDefault();
        return;
    }
    
    var gameId = this.dataset.gameId;
    var svg = this.querySelector('svg');
    
    if (selectedGames.has(gameId)) {
        selectedGames.delete(gameId);
        svg.classList.remove('filled');
        this.classList.remove('selected');
    } else {
        selectedGames.add(gameId);
        svg.classList.add('filled');
        this.classList.add('selected');
    }
}

// 하트 버튼 이벤트 리스너 연결
function attachHeartButtonListeners() {
    var buttons = document.querySelectorAll('.heart-button');
    buttons.forEach(function(button) {
        button.removeEventListener('click', handleHeartClick);
        button.addEventListener('click', handleHeartClick);
        
        if (button.classList.contains('disabled')) {
            var tooltip = button.querySelector('.tooltip');
            if (tooltip) {
                button.addEventListener('mouseenter', function() {
                    adjustTooltipPosition(tooltip);
                });
            }
        }
    });
}

// 툴팁 위치 조정
function adjustTooltipPosition(tooltip) {
    var rect = tooltip.getBoundingClientRect();
    var modalRect = document.querySelector('.modal-container').getBoundingClientRect();
    
    if (rect.left < modalRect.left) {
        tooltip.style.left = '0';
        tooltip.style.transform = 'translateX(0)';
    }
    
    if (rect.right > modalRect.right) {
        tooltip.style.left = 'auto';
        tooltip.style.right = '0';
        tooltip.style.transform = 'translateX(0)';
    }
}

// 선택한 게임 저장
function saveSelectedGames() {
    if (selectedGames.size === 0) {
        alert('게임을 선택해주세요.');
        return;
    }

    fetch(contextPath + '/mypage/add-games', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ gameIds: Array.from(selectedGames) })
    })
    .then(function(response) {
        return response.json();
    })
    .then(function(result) {
        if (result.success) {
            location.reload();
        } else {
            alert('게임 추가에 실패했습니다.');
        }
    })
    .catch(function(error) {
        console.error('Error:', error);
        alert('오류가 발생했습니다.');
    });
}

// 초기화
document.addEventListener('DOMContentLoaded', function() {
    attachHeartButtonListeners();
});
</script>

