<%@ page import="PSH.model.Member" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
<style>
   /* 폰트 스타일 */
   .font-matchday { 
       font-family: 'Montserrat', sans-serif; 
       letter-spacing: 0.05em; 
   }
   .font-slogan { 
       font-family: 'Inter', sans-serif; 
       letter-spacing: 0.2em; 
   }
   
   /* 기본 색상 */
   .custom-gray { 
       background-color: #111111; 
   }
   
   /* 드롭다운 메뉴 스타일 */
   .dropdown-menu {
       position: relative;
       display: inline-block;
   }
   
   .dropdown-content {
       display: none;
       position: absolute;
       background-color: #fff;
       min-width: 160px;
       box-shadow: 0 8px 16px rgba(0,0,0,0.1);
       z-index: 1;
       border-radius: 0.5rem;
       left: 0;
       top: calc(100% + 10px);
       padding-top: 0.5rem;
   }
   
   .dropdown-content::before {
       content: '';
       position: absolute;
       top: -10px;
       left: 0;
       right: 0;
       height: 10px;
   }
   
   .dropdown-menu:hover .dropdown-content {
       display: block;
   }
   
   .dropdown-content a {
       display: block;
       padding: 0.75rem 1rem;
       white-space: nowrap;
   }
   
   .dropdown-content a:hover {
       background-color: #f3f4f6;
   }
   
   /* 모바일 메뉴 스타일 */
   .mobile-menu {
       transform: translateX(100%);
       transition: transform 0.3s ease-in-out;
   }
   
   .mobile-menu.active {
       transform: translateX(0);
   }
   
   .mobile-nav-buttons {
       display: none;
   }
   
   /* 알림 드롭다운 스타일(데스크톱) */
   #notificationDropdown {
       position: absolute;
       right: 0;
       margin-top: 0.5rem;
       width: 20rem;
       background-color: white;
       border-radius: 0.5rem;
       box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
       z-index: 50;
   }
   
   /* 모바일 알림 드롭다운 스타일 */
   /* 기본적으로 display: none; 상태 */
   #mobile-notification-dropdown {
       position: fixed;
       top: 4rem;
       right: 1rem;
       width: calc(100% - 2rem);
       max-width: 20rem;
       background-color: white;
       border-radius: 0.5rem;
       box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
       z-index: 9999;
       display: none; /* hidden 클래스 대신 직접 style로 숨김 */
   }

   /* .show 클래스가 붙으면 display: block 으로 표시 */
   /* 모바일 구간에서만 보이도록 media query 설정 */
   @media (max-width: 768px) {
       #mobile-notification-dropdown.show {
           display: block !important;
       }
   }
   
   /* 알림 배지 스타일 */
   .notification-badge {
       position: absolute;
       top: -5px;
       right: -5px;
       background-color: #EF4444;
       color: white;
       border-radius: 9999px;
       padding: 2px 6px;
       font-size: 0.75rem;
       min-width: 20px;
       text-align: center;
       display: none;
   }
   
   /* 반응형 스타일 */
   @media (max-width: 768px) {
       .mobile-nav-buttons {
           display: flex;
           align-items: center;
           gap: 1rem;
       }
   }
</style>

<header>
    <!-- contextPath 변수 설정 -->
    <script>
        var contextPath = '${pageContext.request.contextPath}';
    </script>
    <!-- 수정된 notification.js 불러오기 -->
    <script src="${pageContext.request.contextPath}/js/notification.js"></script>

    <nav class="bg-white shadow-lg fixed top-0 w-full z-10">
        <div class="max-w-6xl mx-auto px-4">
            <div class="flex justify-between items-center py-4">
                <div class="flex items-center">
                    <a href="${pageContext.request.contextPath}/" class="flex items-center">                     
                        <img src="https://res.cloudinary.com/dnjqljait/image/upload/v1736337453/icononly_cytjxl.png" alt="Matchday Logo" class="h-10">
                        <div class="ml-3 flex flex-col">
                            <span class="font-matchday text-xl font-bold tracking-wider">MATCHDAY</span>
                            <span class="font-slogan text-xs text-gray-600">GAMERS ON PLAY</span>
                        </div>
                    </a>
                </div>

                <% Member loggedInMember = (Member) session.getAttribute("loggedInMember");
                if (loggedInMember != null) { %>
                    <!-- Mobile Navigation Buttons (Logged In) -->
                    <div class="md:hidden mobile-nav-buttons">
                        <div class="relative">
                            <button id="mobile-notification-button" class="relative p-2">
                                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                                </svg>
                                <span id="mobile-unreadCount" class="notification-badge" style="display: none;">0</span>
                            </button>
                        </div>
                        <button id="mobile-menu-button" class="p-2">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6h7"/>
                            </svg>
                        </button>
                    </div>

                    <!-- Desktop Navigation (Logged In) -->
                    <div class="hidden md:flex items-center space-x-8">
                        <a href="${pageContext.request.contextPath}/noticeboard" class="font-slogan text-sm text-gray-700 hover:text-gray-900">공지사항</a>
                        <a href="${pageContext.request.contextPath}/meetings" class="font-slogan text-sm text-gray-700 hover:text-gray-900">모임 찾기</a>
                        <a href="${pageContext.request.contextPath}/friends" class="font-slogan text-sm text-gray-700 hover:text-gray-900">친구찾기</a>
                        <a href="${pageContext.request.contextPath}/mypage" class="font-slogan bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800">마이페이지</a>
                        <a href="${pageContext.request.contextPath}/logout" class="font-slogan border border-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-black hover:text-white hover:border-black">로그아웃</a>
                        <div class="relative">
                            <button id="notificationButton" class="relative">
                                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                                </svg>
                                <span id="unreadCount" class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center" style="display: none;">0</span>
                            </button>
                            <div id="notificationDropdown" class="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg hidden">
                                <div class="p-4 max-h-96 overflow-y-auto">
                                    <div id="notificationList"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                <% } else { %>
                    <!-- Mobile Navigation Button (Logged Out) -->
                    <div class="md:hidden">
                        <button id="mobile-menu-button" class="p-2">
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6h7"/>
                            </svg>
                        </button>
                    </div>

                    <!-- Desktop Navigation (Logged Out) -->
                    <div class="hidden md:flex items-center space-x-8">
                        <a href="${pageContext.request.contextPath}/noticeboard" class="font-slogan text-sm text-gray-700 hover:text-gray-900">공지사항</a>
                        <a href="${pageContext.request.contextPath}/meetings" class="font-slogan text-sm text-gray-700 hover:text-gray-900">모임 찾기</a>
                        <a href="${pageContext.request.contextPath}/friends" class="font-slogan text-sm text-gray-700 hover:text-gray-900">친구찾기</a>
                        <a href="${pageContext.request.contextPath}/login" class="font-slogan border border-gray-300 text-gray-700 px-4 py-2 rounded-lg hover:bg-black hover:text-white hover:border-black">로그인</a>
                        <a href="${pageContext.request.contextPath}/register" class="font-slogan bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800">회원가입</a>
                    </div>
                <% } %>
            </div>
        </div>
    </nav>
</header>

<!-- Mobile Menu -->
<div class="fixed top-0 right-0 w-64 h-full bg-white shadow-lg mobile-menu p-6" id="mobile-menu" style="z-index: 9999;">
    <button class="text-right w-full text-gray-700 text-xl" id="close-menu">&times;</button>
    <nav class="flex flex-col space-y-4 mt-6" style="position: relative; z-index: 9999;">
        <% if (loggedInMember != null) { %>
            <a href="${pageContext.request.contextPath}/mypage" class="text-lg text-white bg-black px-4 py-2 rounded-lg text-center">마이페이지</a>
            <a href="${pageContext.request.contextPath}/logout" class="text-lg text-white bg-gray-700 px-4 py-2 rounded-lg text-center">로그아웃</a>
        <% } else { %>
            <a href="${pageContext.request.contextPath}/login" class="text-lg text-white bg-black px-4 py-2 rounded-lg text-center">로그인</a>
            <a href="${pageContext.request.contextPath}/register" class="text-lg text-white bg-gray-700 px-4 py-2 rounded-lg text-center">회원가입</a>
        <% } %>
        <a href="${pageContext.request.contextPath}/noticeboard" class="text-lg text-gray-700">공지사항</a>
        <a href="${pageContext.request.contextPath}/meetings" class="text-lg text-gray-700">모임 찾기</a>
        <a href="${pageContext.request.contextPath}/friends" class="text-lg text-gray-700">친구찾기</a>
    </nav>
</div>

<!-- Mobile Notification Dropdown -->
<!-- hidden 대신 style="display:none" -> 자바스크립트가 .show 추가 시 보이도록 -->
<div id="mobile-notification-dropdown" class="fixed top-16 right-4 bg-white rounded-lg shadow-lg md:hidden"
     style="z-index: 9999; width: calc(100% - 2rem); max-width: 20rem; display: none;">
    <div class="p-4 max-h-96 overflow-y-auto">
        <div id="mobile-notificationList"></div>
    </div>
</div>

<script>
// Mobile menu toggle
document.getElementById('mobile-menu-button').addEventListener('click', function(event) {
   event.stopPropagation();
   const menu = document.getElementById('mobile-menu');
   menu.classList.toggle('active');
});

document.getElementById('close-menu').addEventListener('click', function(event) {
   event.stopPropagation();
   document.getElementById('mobile-menu').classList.remove('active');
});

// Close menu when clicking outside
document.addEventListener('click', function(event) {
   const menu = document.getElementById('mobile-menu');
   const menuButton = document.getElementById('mobile-menu-button');

   if (!menu.contains(event.target) && !menuButton.contains(event.target)) {
       menu.classList.remove('active');
   }
});
</script>
