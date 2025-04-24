<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.Collections" %>
<%@ page import="PSH.model.BoardGame" %>

<style>
    /* 모달 기본 스타일 */
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

    /* 게임 카드 스타일 */
    .game-card {
        position: relative;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        overflow: hidden;
        background: white;
        transition: transform 0.2s;
        height: 320px;
        cursor: pointer;
    }

    .game-card:hover {
        transform: translateY(-5px);
    }

    .game-image {
        position: relative;
        width: 100%;
        height: 240px;
    }

    .game-image img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    /* 하트 버튼 스타일 */
    .heart-button {
        position: absolute;
        top: 10px;
        right: 10px;
        background: rgba(255, 255, 255, 0.9);
        border: none;
        border-radius: 50%;
        width: 36px;
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.2s;
    }

    .heart-button.selected {
        background: rgba(255, 255, 255, 0.9);
    }

    .heart-button.selected svg,
    .heart-button svg.filled {
        fill: #FF69B4;
        stroke: #FF69B4;
    }

    .heart-button.disabled {
        cursor: not-allowed;
        background: rgba(255, 255, 255, 0.7);
    }

    /* 게임 타이틀 스타일 */
    .game-title {
        padding: 8px 10px;
        font-size: 0.9rem;
        font-weight: 600;
        text-align: center;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        background: #f9fafb;
    }

    /* 툴팁 스타일 */
    .tooltip {
        visibility: hidden;
        position: absolute;
        background-color: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 8px 12px;
        border-radius: 6px;
        font-size: 14px;
        white-space: nowrap;
        z-index: 1000;
        bottom: 100%;
        left: 50%;
        transform: translateX(-50%);
        margin-bottom: 8px;
        opacity: 0;
        transition: opacity 0.2s, visibility 0.2s;
    }

    .heart-button.disabled:hover .tooltip {
        visibility: visible;
        opacity: 1;
    }
</style>

<script>
    var contextPath = '<%= request.getContextPath() %>';
</script>

<% 
List<BoardGame> games = (List<BoardGame>) request.getAttribute("games");
List<Integer> favoriteGameIds = (List<Integer>) request.getAttribute("favoriteGameIds");
Set<Integer> favoriteGameIdSet = new HashSet<>(favoriteGameIds != null ? favoriteGameIds : Collections.emptyList());

if(games != null) {
    for(BoardGame game : games) { 
        boolean isFavorite = favoriteGameIdSet.contains(game.getGameId());
%>
    <div class="game-card">
        <div class="game-image">
            <img src="<%=game.getImageUrl()%>" alt="<%=game.getName()%>">
            <button class="heart-button <%=isFavorite ? "disabled" : ""%>" 
                    data-game-id="<%=game.getGameId()%>"
                    <%=isFavorite ? "disabled" : ""%>
                    data-tooltip="<%=isFavorite ? "이미 관심 게임으로 등록된 게임입니다" : ""%>">
                <svg xmlns="http://www.w3.org/2000/svg" 
                     class="h-6 w-6 <%=isFavorite ? "text-gray-400" : ""%>" 
                     fill="<%=isFavorite ? "currentColor" : "none"%>" 
                     viewBox="0 0 24 24" 
                     stroke="currentColor">
                    <path stroke-linecap="round" 
                          stroke-linejoin="round" 
                          stroke-width="2" 
                          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                <span class="tooltip">이미 관심 게임으로 등록된 게임입니다</span>
            </button>
        </div>
        <div class="game-title"><%=game.getName()%></div>
    </div>
<%
    }
} else {
%>
    <div class="no-games">검색 결과가 없습니다.</div>
<% 
} 
%>