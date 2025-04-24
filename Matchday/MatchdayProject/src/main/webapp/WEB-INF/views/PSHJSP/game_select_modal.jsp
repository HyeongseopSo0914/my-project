<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!-- 게임 선택 모달 -->
<style>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: none;
    z-index: 50;
    align-items: center;
    justify-content: center;
}

.modal-overlay.show {
    display: flex !important;
}

.modal-container {
    background-color: white;
    border-radius: 0.5rem;
    width: 91.666667%;
    max-width: 56rem;
    max-height: 90vh;
    overflow: hidden;
}

.modal-header {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    font-size: 1.25rem;
    font-weight: bold;
}

.close-button {
    color: #6b7280;
    background: none;
    border: none;
    cursor: pointer;
}

.modal-content {
    padding: 1.5rem;
}

.search-container {
    position: relative;
    margin-bottom: 1.5rem;
}

#modalGameSearch {
    width: 100%;
    padding: 0.75rem 1rem;
    padding-right: 2.5rem;
    border: 2px solid black;
    border-radius: 0.5rem;
    font-size: 0.875rem;
}

.search-icon {
    position: absolute;
    right: 0.75rem;
    top: 50%;
    transform: translateY(-50%);
    color: #9ca3af;
}

.games-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
    overflow-y: auto;
    max-height: 50vh;
    padding-right: 0.5rem;
}

.game-card {
    border: 1px solid #e5e7eb;
    border-radius: 0.5rem;
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.2s;
}

.game-card:hover {
    transform: translateY(-0.25rem);
}

.game-card.selected {
    border-color: #000;
    box-shadow: 0 0 0 2px #000;
}

.game-image {
    position: relative;
    width: 100%;
    height: 150px;
}

.game-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.game-title {
    padding: 0.75rem;
    font-size: 0.875rem;
    font-weight: 500;
}

.modal-footer {
    padding: 1rem 1.5rem;
    border-top: 1px solid #e5e7eb;
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
}

.modal-button {
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    font-weight: 500;
    cursor: pointer;
}

.cancel-button {
    background-color: white;
    border: 1px solid #e5e7eb;
    color: #374151;
}

.select-button {
    background-color: black;
    border: none;
    color: white;
}

.game-description {
    padding: 0 0.75rem 0.75rem;
    font-size: 0.75rem;
    color: #6b7280;
}

/* 스크롤바 스타일링 */
.games-grid::-webkit-scrollbar {
    width: 6px;
}

.games-grid::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.games-grid::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 3px;
}

.games-grid::-webkit-scrollbar-thumb:hover {
    background: #555;
}

.checkbox-wrapper {
    position: absolute;
    top: 10px;
    right: 10px;
    width: 24px;
    height: 24px;
    background: white;
    border: 2px solid #e5e7eb;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
}

.checkbox-wrapper .checkbox-icon {
    width: 16px;
    height: 16px;
    fill: white;
    opacity: 0;
    transition: opacity 0.2s ease;
}

.checkbox-wrapper.selected {
    background: #000;
    border-color: #000;
}

.checkbox-wrapper.selected .checkbox-icon {
    opacity: 1;
}

.game-card.selected {
    border-color: #000;
    box-shadow: 0 0 0 2px #000;
}

.game-card:hover .checkbox-wrapper {
    border-color: #000;
}
</style>

<!-- 게임 선택 모달 -->
<div id="gameSelectModal" class="modal-overlay">
    <div class="modal-container">
        <div class="modal-header">
            <h3>게임 선택</h3>
            <button onclick="closeGameModal()" class="close-button">×</button>
        </div>
        <div class="modal-content">
            <div class="search-container">
                <input type="text" id="modalGameSearch" placeholder="게임 검색..." autocomplete="off">
                <span class="search-icon">🔍</span>
            </div>
            <div class="games-grid">
                <!-- 게임 카드들이 여기에 동적으로 추가됨 -->
            </div>
        </div>
        <div class="modal-footer">
            <button onclick="closeGameModal()" class="modal-button cancel-button">취소</button>
            <button onclick="selectGame()" class="modal-button select-button">선택</button>
        </div>
    </div>
</div>

<script>
let selectedGameId = null;
let selectedGameName = null;

function openGameModal() {
    const modal = document.getElementById('gameSelectModal');
    modal.classList.add('show');
    loadGames();

    // 현재 선택된 게임 ID 설정
    selectedGameId = document.getElementById('gameId').value;
}

function closeGameModal() {
    const modal = document.getElementById('gameSelectModal');
    modal.classList.remove('show');
}

function loadGames(searchQuery = '') {
    const gamesGrid = document.querySelector('.games-grid');
    
    let url = '${pageContext.request.contextPath}/api/games/select';
    if (searchQuery) {
        url = url + '?search=' + encodeURIComponent(searchQuery);
    }

    fetch(url)
        .then(response => response.json())
        .then(games => {
            gamesGrid.innerHTML = ''; // 기존 내용 초기화
            
            games.forEach(game => {
                const newCard = document.createElement('div');
                newCard.className = 'game-card';
                newCard.dataset.gameId = game.gameId;
                
                // innerHTML 대신 createElement 사용
                const gameImageDiv = document.createElement('div');
                gameImageDiv.className = 'game-image';

                const img = document.createElement('img');
                img.src = game.imageUrl;
                img.alt = game.name;

                const checkboxWrapper = document.createElement('div');
                checkboxWrapper.className = 'checkbox-wrapper';

                const checkboxSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
                checkboxSvg.setAttribute('class', 'checkbox-icon');
                checkboxSvg.setAttribute('viewBox', '0 0 24 24');

                const checkboxPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
                checkboxPath.setAttribute('d', 'M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z');

                checkboxSvg.appendChild(checkboxPath);
                checkboxWrapper.appendChild(checkboxSvg);

                gameImageDiv.appendChild(img);
                gameImageDiv.appendChild(checkboxWrapper);

                const gameTitleDiv = document.createElement('div');
                gameTitleDiv.className = 'game-title';
                gameTitleDiv.textContent = game.name;

                newCard.appendChild(gameImageDiv);
                newCard.appendChild(gameTitleDiv);
                
                // 게임 선택 이벤트 리스너 추가
                newCard.addEventListener('click', function() {
                    document.querySelectorAll('.game-card').forEach(card => {
                        card.classList.remove('selected');
                        card.querySelector('.checkbox-wrapper').classList.remove('selected');
                    });
                    
                    this.classList.add('selected');
                    this.querySelector('.checkbox-wrapper').classList.add('selected');
                    selectedGameId = game.gameId;
                    selectedGameName = game.name;
                });
                
                gamesGrid.appendChild(newCard);
            });
        })
        .catch(error => {
            console.error('Error loading games:', error);
            gamesGrid.innerHTML = '<div class="error-message">게임 목록을 불러오는데 실패했습니다.</div>';
        });
}
// 검색 기능 구현
document.getElementById('modalGameSearch').addEventListener('input', function(e) {
    const searchQuery = e.target.value.trim();
    if (window.searchTimeout) {
        clearTimeout(window.searchTimeout);
    }
    window.searchTimeout = setTimeout(() => {
        loadGames(searchQuery);
    }, 300);
});

function selectGame() {
    if (!selectedGameId || !selectedGameName) {
        alert('게임을 선택해주세요.');
        return;
    }
    
    // hidden input 업데이트
    document.getElementById('gameId').value = selectedGameId;
    
    // 선택된 게임 표시 영역 업데이트
    const selectedGameDisplay = document.getElementById('selectedGameDisplay');
    selectedGameDisplay.textContent = selectedGameName;
    
    closeGameModal();
}
</script>