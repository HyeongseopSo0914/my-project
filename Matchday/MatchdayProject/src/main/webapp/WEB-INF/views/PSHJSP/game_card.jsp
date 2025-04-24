<!-- /WEB-INF/views/components/game_card.jsp -->
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="game-card h-80">
    <div class="game-image relative w-full h-64">
        <img src="${game.imageUrl}" 
             alt="${game.name}"
             class="absolute inset-0 w-full h-full object-cover">
        <c:if test="${showHeartButton}">
            <button class="heart-button ${isFavorite ? 'disabled' : ''}" 
                    data-game-id="${game.gameId}"
                    ${isFavorite ? 'disabled' : ''}
                    data-tooltip="${isFavorite ? '이미 관심 게임으로 등록된 게임입니다' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" 
                     class="h-6 w-6 ${isFavorite ? 'text-gray-400' : ''}" 
                     fill="${isFavorite ? 'currentColor' : 'none'}" 
                     viewBox="0 0 24 24" 
                     stroke="currentColor">
                    <path stroke-linecap="round" 
                          stroke-linejoin="round" 
                          stroke-width="2" 
                          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                <span class="tooltip">이미 관심 게임으로 등록된 게임입니다</span>
            </button>
        </c:if>
        <c:if test="${showDeleteButton}">
            <button class="delete-btn">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-red-500" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
                </svg>
            </button>
        </c:if>
    </div>
    <h4 class="px-3 py-2 text-sm font-semibold truncate">${game.name}</h4>
</div>