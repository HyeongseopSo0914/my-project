<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="PSH.model.Member" %>
<%@ page import="PSH.model.Meeting" %>
<%@ page import="PSW.model.Notice" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Matchday - Gamers on Play</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
    .font-matchday {
        font-family: 'Montserrat', sans-serif;
        letter-spacing: 0.05em;
    }
    .font-slogan {
        font-family: 'Inter', sans-serif;
        letter-spacing: 0.2em;
    }
    .logo-bg {
        background-color: #000000; 
    }
    .custom-gray {
        background-color: #111111;
    }
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
        top: 100%;
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
   .chat-messages {
    display: flex;
    flex-direction: column;
}
.chat-message {
    display: inline-block;
    margin: 5px 0;
    padding: 8px 12px;
    border-radius: 10px;
    max-width: 70%;
    word-break: break-word;
}
.chat-message.sent {
    background-color: #dcf8c6;
    align-self: flex-end;
    margin-left: auto;
    text-align: right;
}
.chat-message.received {
    background-color: #f1f1f1;
    align-self: flex-start;
    text-align: left;
}
#chat-box {
    display: flex;
    flex-direction: column;
}
    </style>
</head>
<body class="bg-gray-100">
    <!-- Navigation -->
<% Member loggedInMember = (Member) session.getAttribute("loggedInMember"); %>
<jsp:include page="/WEB-INF/views/PSWJSP/nav.jsp" />

    <!-- Main Content -->
    <main>
<!-- Hero Section -->
<section class="pt-20 md:pt-32 pb-4 md:pb-8 custom-gray text-white">
    <div class="max-w-6xl mx-auto px-4">
        <div class="flex flex-col md:flex-row items-stretch md:h-96">
            <div class="md:w-1/2 flex flex-col justify-center mb-6 md:mb-0 md:pr-12">
                <% if (loggedInMember != null) { %>
                    <h1 class="font-matchday text-3xl md:text-6xl font-bold mb-4 md:mb-6">
                        환영합니다,<br>
                        <%= loggedInMember.getNickname() %>님!
                    </h1>
                    <p class="font-slogan text-lg md:text-xl text-gray-300 mb-6 md:mb-8">
                        Matchday에서 함께 게임할<br>
                        친구들을 만나보세요.
                    </p>
                    <div class="flex gap-4">
                        <button onclick="location.href='${pageContext.request.contextPath}/meetings'" 
                                class="font-slogan bg-black text-white px-6 md:px-8 py-3 rounded-lg hover:bg-gray-800 text-base md:text-lg w-1/2">
                            모임 찾기
                        </button>
                        <button onclick="location.href='${pageContext.request.contextPath}/mypage'" 
                                class="font-slogan bg-white text-black px-6 md:px-8 py-3 rounded-lg hover:bg-gray-100 text-base md:text-lg w-1/2">
                            내 프로필
                        </button>
                    </div>
                <% } else { %>
                    <h1 class="hidden md:block font-matchday text-4xl md:text-6xl font-bold mb-6">
                        보드게임 친구를<br>
                        찾고 계신가요?
                    </h1>
                    <h1 class="md:hidden font-matchday text-3xl font-bold mb-4">
                        새로운 게임 친구를<br>
                        만나보세요
                    </h1>
                    <p class="hidden md:block font-slogan text-xl text-gray-300 mb-8">
                        Matchday에서 같이 게임할 친구들을 만나고,<br>
                        새로운 보드게임 모임에 참여해보세요.
                    </p>
                    <p class="md:hidden font-slogan text-lg text-gray-300 mb-6">
                        지금 Matchday와 함께하세요
                    </p>
                    <div class="flex gap-4">
                        <button onclick="location.href='${pageContext.request.contextPath}/register'" 
                                class="font-slogan bg-black text-white px-6 md:px-8 py-3 rounded-lg hover:bg-gray-800 text-base md:text-lg w-1/2">
                            회원가입하기
                        </button>
                        <button onclick="location.href='${pageContext.request.contextPath}/login'" 
                                class="font-slogan bg-white text-black px-6 md:px-8 py-3 rounded-lg hover:bg-gray-100 text-base md:text-lg w-1/2">
                            로그인 하기
                        </button>
                    </div>
                <% } %>
            </div>
            <div class="hidden md:block md:w-1/2 h-full">
                <img src="https://res.cloudinary.com/dnjqljait/image/upload/v1736869470/DALL_E_2025-01-15_00.44.08_-_A_black-and-white_anime-style_character_with_spiky_hair_resembling_the_character_from_the_Matchday_logo_sitting_at_a_table_and_playing_a_board_gam_remu9g.webp" 
                     alt="Gaming illustration" 
                     class="w-full h-full object-contain">
            </div>
        </div>
    </div>
</section>

        <!-- Contents Section -->
        <section class="pt-2 md:pt-10 bg-gray-50">
    <div class="max-w-6xl mx-auto px-4">
                <!-- Notice Section -->
                <section id="notice-board" class="text-base text-gray-800 font-medium p-4 border border-gray-300 bg-white rounded-lg shadow-md mb-4 md:mb-8">
            <div id="notice-title"></div>
        </section>
                <!-- Content Grid -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
                    <!-- Meeting List Section -->
                    <div class="p-6 rounded-lg border border-gray-300 bg-white shadow-md">
                        <div class="flex justify-between items-center mb-4">
                            <h2 class="text-xl font-semibold text-gray-800">모임 리스트</h2>
                            <a href="${pageContext.request.contextPath}/meetings" class="text-sm text-blue-500 hover:underline">+더보기</a>
                        </div>
                        <hr class="mb-4 border-gray-300">
                        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
    <% 
    List<Meeting> upcomingMeetings = (List<Meeting>) request.getAttribute("upcomingMeetings");
    if (upcomingMeetings != null) {
        for (Meeting meeting : upcomingMeetings) {
    %>
    <div class="p-3 border rounded-lg">
        <div class="flex justify-between items-start mb-2">
            <h3 class="font-semibold text-sm truncate pr-2"><%= meeting.getTitle() %></h3>
            <span class="text-xs text-blue-600 whitespace-nowrap"><%= meeting.getCurrentParticipants() %>/<%= meeting.getMaxParticipants() %> 명</span>
        </div>
        <div class="space-y-1.5">
            <div class="text-xs text-gray-600">
                <div>🎲 <%= meeting.getGameName() %></div>
                <div>🗺️ <%= meeting.getRegionName() %> <%= meeting.getDistrictName() %></div>
                <div>📅 <%= meeting.getDate() %> <%= meeting.getTime() %></div>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-1">
                <div class="bg-blue-600 h-1 rounded-full" style="width: <%= (meeting.getCurrentParticipants() * 100 / meeting.getMaxParticipants()) %>%"></div>
            </div>
            <div class="flex justify-between items-center text-xs pt-1.5 border-t border-gray-200 mt-1">
                <span class="text-gray-600">👤 모임장: <%= meeting.getHostName() %></span>
                <span class="text-yellow-500">⭐ <%= String.format("%.1f", meeting.getHostRating()) %></span>
            </div>
        </div>
    </div>
    <% 
        }
    } 
    %>
</div>
</div>

                    <!-- Chat Section -->
                    <div class="p-6 rounded-lg border border-gray-300 bg-white shadow-md">
                        <div class="flex justify-between mb-4">
                            <span class="text-sm text-gray-600">현재 접속자 수: <span id="online-users">로딩중...</span></span>
                            <button class="text-xs text-gray-500 hover:text-gray-700">새로고침</button>
                        </div>
                        <div class="space-y-4 mb-4 h-80 overflow-y-auto bg-gray-100 p-4 rounded-lg shadow-inner" id="chat-box">
                            <!-- 채팅 메시지가 여기에 표시됩니다 -->
                        </div>
                        <div class="flex">
                            <input type="text" id="chat-input" 
                                   class="w-full p-2 rounded-l-lg border border-gray-300 text-gray-700" 
                                   placeholder="메시지를 입력하세요..."
                                   <%= loggedInMember == null ? "disabled" : "" %>>
                            <button onclick="sendMessage()" id="send-chat" 
                                    class="bg-black text-white p-2 rounded-r-lg hover:bg-gray-800"
                                    <%= loggedInMember == null ? "disabled" : "" %>>
                                전송
                            </button>
                        </div>
                    </div>
                </div>

            </div>
        </section>
    </main>
    
     <%@ include file="/WEB-INF/views/PSWJSP/footer.jsp"  %>

    <!-- WebSocket Chat Script -->
    <script>
        // 공지사항 출력
        let notices = [
            <% 
    List<Notice> notices = (List<Notice>) request.getAttribute("recentNotices");
    if (notices != null) {
        for (int i = 0; i < notices.size(); i++) { 
            Notice notice = notices.get(i);
%>
    "<a href='${pageContext.request.contextPath}/noticedetail?noticeId=<%= notice.getNoticeId() %>'><%= notice.getNoticeTitle() %></a>"<%= (i < notices.size() - 1) ? "," : "" %>
<%
        }
    }
%>
        ];

        let noticeIndex = 0;

        function updateNotice() {
            if (notices.length > 0) {
                document.getElementById('notice-title').innerHTML = notices[noticeIndex]; // textContent를 innerHTML로 변경
                noticeIndex = (noticeIndex + 1) % notices.length;
            }
        }

        setInterval(updateNotice, 3000); // 3초마다 공지 변경
        updateNotice(); // 초기 로드

        var isLocalhost = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1";
        var wsProtocol = isLocalhost ? "ws://" : (window.location.protocol === "https:" ? "wss://" : "ws://");

        // WebSocket 경로 확인
        var socketUrl = wsProtocol + window.location.host + "/MatchdayProject/chat";  // 프로젝트명이 포함된 경우
        // var socketUrl = wsProtocol + window.location.host + "/chat"; // 프로젝트명이 포함되지 않는 경우

        console.log("🔗 WebSocket 연결 시도: ", socketUrl);

        var socket = new WebSocket(socketUrl);        socket.onopen = function () {
            console.log("✅ WebSocket 연결됨");
        };

        socket.onmessage = function (event) {
            // 전체 메시지 데이터
            const message = event.data;
            
            // 채팅 박스 요소 찾기
            const chatBox = document.getElementById("chat-box");
            
            // 메시지 엘리먼트 생성
            const msgElement = document.createElement("div");
            
            // 메시지를 ":" 기준으로 분리 
            const messageParts = message.split(" : ");
            
            // 첫 번째 부분은 발신자 닉네임
            const senderNickname = messageParts[0];
            
            // 나머지 부분은 메시지 내용 (여러 ":"가 있을 수 있으므로 join)
            const messageContent = messageParts.slice(1).join(" : ");
            
            // 메시지 내용 설정
            msgElement.textContent = messageContent;
            msgElement.classList.add("chat-message");

            <% if (loggedInMember != null) { %>
                if (senderNickname === "<%= loggedInMember.getNickname() %>") {
                    msgElement.classList.add("sent");
                } else {
                    msgElement.classList.add("received");

                    // 발신자 닉네임 추가
                    const nicknameElement = document.createElement("div");
                    nicknameElement.textContent = senderNickname;
                    nicknameElement.style.fontSize = "0.7rem";
                    nicknameElement.style.color = "#666";
                    msgElement.insertBefore(nicknameElement, msgElement.firstChild);
                }
            <% } else { %>
                // 비로그인 상태에서는 모든 메시지를 received로 처리
                msgElement.classList.add("received");

                // 발신자 닉네임 추가
                const nicknameElement = document.createElement("div");
                nicknameElement.textContent = senderNickname;
                nicknameElement.style.fontSize = "0.7rem";
                nicknameElement.style.color = "#666";
                msgElement.insertBefore(nicknameElement, msgElement.firstChild);
            <% } %>

            // 메시지 박스에 메시지 추가
            chatBox.appendChild(msgElement);
            
            // 스크롤을 가장 아래로 이동
            chatBox.scrollTop = chatBox.scrollHeight;
        };

        function sendMessage() {
            const input = document.getElementById("chat-input");
            if (input.value.trim() !== "") {
                socket.send(input.value); // 여기서 `ws` → `socket`으로 수정
                input.value = "";
            }
        }


        // 현재 접속자 수 업데이트
        function updateOnlineCount() {
    // 현재 URL이 HTTPS인지 확인하고, HTTP 요청을 자동으로 HTTPS로 변경
    var isLocalhost = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1";
    var httpProtocol = isLocalhost ? "http://" : (window.location.protocol === "https:" ? "https://" : "http://");

    var concurrentUrl = httpProtocol + window.location.host + "${pageContext.request.contextPath}/Concurrent";

    console.log("📡 접속자 수 요청 URL: ", concurrentUrl);

    fetch(concurrentUrl)
        .then(response => response.text())
        .then(count => {
            document.getElementById('online-users').innerText = count;
        })
        .catch(error => {
            console.error('❌ 접속자 수를 가져오는데 실패했습니다:', error);
            document.getElementById('online-users').innerText = "오류 발생";
        });
}

// 5초마다 접속자 수 갱신
setInterval(updateOnlineCount, 5000);
document.addEventListener("DOMContentLoaded", updateOnlineCount);

    </script>
</body>
</html>