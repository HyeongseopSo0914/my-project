<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="PSH.model.Member" %>
<%@ page import="PSH.model.Meeting" %>
<%@ page import="PSH.dao.MeetingDAO" %>
<%@ page import="java.util.List" %>

<%
    Member loggedInMember = (Member) session.getAttribute("loggedInMember");
    String userNickname = loggedInMember != null ? loggedInMember.getNickname() : null;
    Integer meetingId = request.getParameter("meetingId") != null ? Integer.parseInt(request.getParameter("meetingId")) : null;
    
    if (meetingId == null) {
        response.sendRedirect(request.getContextPath() + "/meetings");
        return;
    }

    MeetingDAO meetingDAO = new MeetingDAO();
    Meeting meeting = meetingDAO.getMeetingById(meetingId);
    String meetingTitle = meeting != null ? meeting.getTitle() : "모임 채팅방";
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>모임 채팅 - <%= meetingTitle %></title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <style>
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
            height: calc(100vh - 180px);
            overflow-y: auto;
            padding: 1rem;
            background-color: #f8f9fa;
        }
        .message-input-container {
            position: fixed;
            bottom: 0;
            left: 0;
            right: 0;
            padding: 1rem;
            background-color: white;
            border-top: 1px solid #e5e7eb;
        }
        .nickname {
            font-size: 0.8rem;
            color: #666;
            margin-bottom: 2px;
        }
        .timestamp {
            font-size: 0.7rem;
            color: #999;
            margin-top: 2px;
        }
    </style>
</head>
<body class="bg-gray-100">
    <div class="max-w-2xl mx-auto bg-white shadow-lg h-screen">
        <!-- Header -->
        <div class="bg-black text-white p-4">
            <h1 class="text-xl font-bold"><%= meetingTitle %></h1>
            <p class="text-sm">참여자: <%= userNickname %></p>
        </div>

        <!-- Chat Messages -->
        <div id="chat-box" class="chat-messages"></div>

        <!-- Input Area -->
        <div class="message-input-container">
            <div class="flex gap-2">
                <input id="textMessage" type="text" 
                       class="flex-1 p-2 border rounded-lg focus:outline-none focus:border-black"
                       placeholder="메시지를 입력하세요">
                <button onclick="sendMessage()" 
                        class="px-4 py-2 bg-black text-white rounded-lg hover:bg-gray-800">
                    전송
                </button>
            </div>
        </div>
    </div>

    <script>
    const meetingId = <%= meetingId %>;
    const userNickname = "<%= userNickname %>";
    const writerId = <%= loggedInMember.getId() %>;
    const chatBox = document.getElementById("chat-box");
    let socket;

    function initWebSocket() {
        const isLocalhost = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1";
        const wsProtocol = isLocalhost ? "ws://" : (window.location.protocol === "https:" ? "wss://" : "ws://");
        
        // contextPath를 동적으로 얻기
        const pathArray = window.location.pathname.split('/');
        const contextPath = '/' + pathArray[1];  // 예: '/MatchdayProject'
        
        const socketUrl = wsProtocol + window.location.host + contextPath + '/websocket/' + meetingId;
        console.log("WebSocket URL:", socketUrl);
        
        socket = new WebSocket(socketUrl);
        
        socket.onopen = function(event) {
            console.log("WebSocket Connected:", event);
        };
        socket.onmessage = function(event) {
            console.log("WebSocket message received:", event.data);
            let messageData = JSON.parse(event.data);
            addMessageToChat(messageData); // ✅ 기존 displayMessage() 대신 addMessageToChat() 사용
        };

        socket.onerror = function(error) {
            console.error("WebSocket Error:", error);
            // 에러 발생 시 재시도 로직
            setTimeout(() => {
                if (socket.readyState === WebSocket.CLOSED) {
                    console.log("Attempting to reconnect...");
                    initWebSocket();
                }
            }, 5000);
        };

        socket.onclose = function(event) {
            console.warn("⚠️ WebSocket Closed:", event);
            setTimeout(initWebSocket, 3000); // 3초 후 재연결 시도
        };
    }

    function addMessageToChat(messageData) {
        const messageDiv = document.createElement("div");
        messageDiv.className = messageData.nickname === userNickname 
            ? "chat-message sent" 
            : "chat-message received";

        if (messageData.nickname !== userNickname) {
            const nicknameDiv = document.createElement("div");
            nicknameDiv.className = "nickname";
            nicknameDiv.textContent = messageData.nickname;
            messageDiv.appendChild(nicknameDiv);
        }
        
        const contentDiv = document.createElement("div");
        contentDiv.textContent = messageData.message;
        messageDiv.appendChild(contentDiv);

        const timeDiv = document.createElement("div");
        timeDiv.className = "timestamp";
        timeDiv.textContent = new Date(messageData.sentTime || Date.now()).toLocaleTimeString();
        messageDiv.appendChild(timeDiv);

        chatBox.appendChild(messageDiv);
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    function sendMessage() {
        const input = document.getElementById("textMessage");
        const message = input.value.trim();
        
        if (message === "" || !socket || socket.readyState !== WebSocket.OPEN) {
            console.log("Cannot send message. Socket state:", socket ? socket.readyState : "no socket");
            return;
        }

        const messageObj = {
            nickname: userNickname,
            message: message,
            meetingId: meetingId,
            writerId: writerId,
            sentTime: new Date().toISOString()
        };

        try {
            console.log("Sending message:", messageObj);
            socket.send(JSON.stringify(messageObj));
            input.value = "";
        } catch (error) {
            console.error("Error sending message:", error);
        }
    }

    document.getElementById("textMessage").addEventListener("keypress", function(e) {
        if (e.key === "Enter") {
            sendMessage();
        }
    });

    function loadRecentMessages() {
        console.log("Loading recent messages for meeting: " + meetingId);
        fetch(contextPath + "/getChatMessages?meetingId=" + meetingId)
            .then(response => {
                console.log("Response status:", response.status);
                return response.json();
            })
            .then(messages => {
                console.log("Received messages:", messages);
                messages.forEach(addMessageToChat);
                chatBox.scrollTop = chatBox.scrollHeight;
            })
            .catch(error => {
                console.error("Error loading messages:", error);
            });
    }
    // Initialize WebSocket on page load
    initWebSocket();
</script>
</body>
</html>
