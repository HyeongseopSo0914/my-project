<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="PSH.model.Meeting" %>
<%@ page import="PSH.dao.ReviewDAO" %>
<%@ page import="PSH.model.Member" %>

<%
    // 로그인 회원 정보를 세션에서 가져오거나 request attribute에서 가져올 수 있습니다.
    // 만약 세션에 저장되어 있다면 아래와 같이 수정할 수 있습니다.
    // Member loggedInMember = (Member) session.getAttribute("loggedInMember");
    Member loggedInMember = (Member) request.getAttribute("loggedInMember");
    Meeting meeting = (Meeting) request.getAttribute("meeting");
    List<Member> participants = (List<Member>) request.getAttribute("participants");
    boolean isJoined = (Boolean) request.getAttribute("isJoined");
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>모임 상세 - <%= meeting.getTitle() %></title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/meetings.css">
    <style>
        .meeting-detail {
            max-width: 800px;
            margin: 2rem auto;
            padding: 2rem;
            background: white;
            border-radius: 1rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        .meeting-header {
            border-bottom: 1px solid #eee;
            padding-bottom: 1rem;
            margin-bottom: 1rem;
        }
        .meeting-title {
            font-size: 1.5rem;
            font-weight: bold;
            margin-bottom: 0.5rem;
        }
        .meeting-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin: 1rem 0;
        }
        .info-item {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }
        .info-label {
            font-weight: 500;
            color: #666;
        }
        .description {
            margin: 1rem 0;
            line-height: 1.6;
        }
        .participants {
            margin-top: 2rem;
        }
        .participants-list {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
            gap: 1rem;
            margin-top: 1rem;
        }
        .participant {
            text-align: center;
        }
        .participant img {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            margin-bottom: 0.5rem;
        }
        .participant-name {
            font-size: 0.9rem;
        }
        .join-button {
            width: 100%;
            padding: 1rem;
            background-color: black;
            color: white;
            border: none;
            border-radius: 0.5rem;
            font-weight: 500;
            cursor: pointer;
            margin-top: 1rem;
            transition: background-color 0.2s;
        }
        .join-button:hover:not(:disabled) {
            background-color: #333;
        }
        .join-button:disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
        .join-button.cancel {
            background-color: #ff4444;
        }
        .join-button.cancel:hover:not(:disabled) {
            background-color: #ff0000;
        }
        .join-button.confirm {
            background-color: #4CAF50;
        }
        .join-button.confirm:hover:not(:disabled) {
            background-color: #45a049;
        }
        .join-button.confirmed {
            background-color: #4CAF50;
            cursor: default;
        }
        .confirmed-badge {
            display: inline-block;
            background-color: #4CAF50;
            color: white;
            padding: 0.25rem 0.75rem;
            border-radius: 1rem;
            font-size: 0.9rem;
            margin-left: 1rem;
            vertical-align: middle;
        }
        .host-badge {
            background-color: #ffd700;
            color: black;
            padding: 0.2rem 0.5rem;
            border-radius: 0.25rem;
            font-size: 0.8rem;
            font-weight: 500;
        }
        .join-button.confirmed {
            background-color: #4CAF50;
            cursor: default;
        }
        .join-button:hover:not(:disabled) {
            background-color: #333;
        }
        .edit-meeting-btn {
            padding: 10px 20px;
            background-color: var(--primary-color);
            color: var(--secondary-color);
            border: none;
            border-radius: 8px;
            cursor: pointer;
            margin-top: 10px;
        }
        .edit-meeting-btn:hover {
            background-color: #333;
        }
        .toggle-button {
            padding: 10px;
            background-color: #f0f0f0; /* 연한 회색 배경 */
            color: #666; /* 어두운 회색 텍스트 */
            border: 1px solid #ddd; /* 얇은 테두리 */
            border-radius: 5px;
            cursor: pointer;
            width: 100%;
            text-align: center;
            font-weight: normal; /* 볼드 제거 */
            margin-bottom: 10px;
            transition: background-color 0.2s, color 0.2s;
        }
        .toggle-button:hover {
            background-color: #e0e0e0; /* 호버 시 약간 더 어두운 회색 */
            color: #333;
        }
        .change-list {
            display: none; /* ✅ 기본적으로 숨김 */
            background-color: #f9fafb;
            padding: 1rem;
            border-radius: 0.5rem;
        }
        .change-item {
            border-bottom: 1px solid #e5e7eb;
            padding: 0.75rem 0;
        }
        .change-item:last-child {
            border-bottom: none;
        }
        .modal {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
  }
  /* 모달 콘텐츠 스타일 */
  .modal-content {
      background: #fff;
      padding: 20px;
      border-radius: 8px;
      max-width: 500px;
      width: 90%;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
      position: relative;
  }
  .close-modal {
      position: absolute;
      top: 10px;
      right: 10px;
      cursor: pointer;
      font-size: 24px;
      font-weight: bold;
  }
  .chat-icon {
    width: 50px; /* 가로 크기 */
    height: 50px; /* 세로 크기 */
    object-fit: cover; /* 이미지 비율 유지 */
	}  
    </style>
    <script>
        var contextPath = '${pageContext.request.contextPath}';
    </script>
</head>
<body>
    <!-- Navigation Bar -->
    <<jsp:include page="/WEB-INF/views/PSWJSP/nav.jsp" />

    <!-- Main Content -->
    <main>
        <div class="meeting-detail">
            <div class="meeting-header">
                <% if (isJoined) { %>
                    <span class="participating-badge">참가중</span>
                <% } %>
                <% 
                    String status = meeting.getStatus();
                    if (status != null) {
                        String badgeClass = "";
                        String statusText = "";
                        switch (status) {
                            case "CONFIRMED":
                                badgeClass = "confirmed-badge";
                                statusText = "확정된 모임";
                                break;
                            case "EXPIRED":
                                badgeClass = "expired-badge";
                                statusText = "기간 만료";
                                break;
                            case "COMPLETED":
                                badgeClass = "completed-badge";
                                statusText = "종료된 모임";
                                break;
                        }
                        if (!badgeClass.isEmpty()) {
                %>
                            <span class="<%= badgeClass %>"><%= statusText %></span>
                <%      }
                    } 
                %>
                
                <h1 class="meeting-title"><%= meeting.getTitle() %></h1>
                <div class="game-info">
                    <span class="game-name">🎲 <%= meeting.getGameName() %></span>
                </div>
            </div>

            <div class="meeting-info">
    <div class="info-item">
        <span class="info-label">모임 장소</span>
        <span>🗺️ <%= meeting.getRegionName() %> <%= meeting.getDistrictName() %></span>
    </div>
    <div class="info-item">
        <span class="info-label">모임 일시</span>
        <span>📅 <%= meeting.getDate() %> <%= meeting.getTime() %></span>
    </div>
    <div class="info-item">
        <span class="info-label">참가 인원</span>
        <span><%= meeting.getCurrentParticipants() %>/<%= meeting.getMaxParticipants() %>명</span>
    </div>
    <div class="info-item">
        <span class="info-label">모임장</span>
        <span>👤 <%= meeting.getHostName() %></span>
    </div>
    
    <% if (isJoined && 
       !"EXPIRED".equals(meeting.getStatus()) && 
       !"COMPLETED".equals(meeting.getStatus())) { %>
    <div class="info-item">
        <img src="<%= request.getContextPath() %>/uploads/chat.JPG" 
             alt="채팅 이미지" 
             class="chat-icon cursor-pointer" 
             onclick="openChatPopup()">
        <span class="info-label">채팅 하기</span>
    </div>
<% } %>
</div>

<script>
function openChatPopup() {
    // meetingId를 쿼리 파라미터로 추가
    window.open("<%= request.getContextPath() %>/chat?meetingId=<%= meeting.getMeetingId() %>", 
        "ChatWindow", 
        "width=400,height=600");
}
</script>


            

            <div class="description">
                <h3>모임 설명</h3>
                <p style="white-space: pre-wrap;"><%= meeting.getDescription() %></p>
            </div>
            
            <!-- 모임 설명 아래에 추가 -->
            <div class="meeting-changes">
                <!-- 변경 이력 보기 버튼 -->
                <button id="toggleChangesBtn" class="toggle-button">
                    모임 정보 변경 이력 보기
                </button>

                <!-- 변경 이력 리스트 (초기 상태: 숨김) -->
                <div id="meetingChangesList" class="change-list">
                    <h3>모임 변경 이력</h3>
                    <% 
                        List<Map<String, Object>> meetingChanges = (List<Map<String, Object>>) request.getAttribute("meetingChanges");
                        if (meetingChanges != null && !meetingChanges.isEmpty()) { 
                            for (Map<String, Object> change : meetingChanges) { 
                    %>
                                <div class="change-item">
                                    <p><%= change.get("changeDescription") %></p>
                                    <small><%= change.get("changeDate") %></small>
                                </div>
                    <%      } 
                        } else { 
                    %>
                        <p>아직 변경 이력이 없습니다.</p>
                    <% } %>
                </div>
            </div>

            <div class="participants">
    <h3>참가자 목록</h3>
    <div class="participants-list">
        <% for(Member participant : participants) { %>
            <div class="participant">
                <img src="<%= participant.getProfileImageUrl() != null ? participant.getProfileImageUrl() : "https://via.placeholder.com/60" %>"
                     alt="<%= participant.getNickname() %>"
                     class="cursor-pointer"
                     onclick="showUserDetailModal(<%= participant.getId() %>)">
                <div class="participant-name flex flex-col items-center">
                    <div class="flex items-center">
                        <%= participant.getNickname() %>
                        <% if(participant.getId() == meeting.getCreatedBy()) { %>
                            <span class="host-badge ml-1">👑</span>
                        <% } %>
                    </div>
                    
                    <%-- 모임장 퇴장 버튼 --%>
<% if (loggedInMember != null && 
      loggedInMember.getId() == meeting.getCreatedBy() && 
      participant.getId() != meeting.getCreatedBy() &&
      !meeting.isConfirmed() && 
      !"COMPLETED".equals(meeting.getStatus()) && 
      !"EXPIRED".equals(meeting.getStatus())) { %>
    <button onclick="kickParticipant(<%= participant.getId() %>)"
            class="mt-2 px-2 py-1 bg-red-500 text-white text-xs rounded hover:bg-red-600">
        퇴장
    </button>
<% } %>
                    
                    <%-- 리뷰 관련 로직 --%>
                    <% 
                        boolean hasReviewed = false;
                        if (loggedInMember != null) {
                            ReviewDAO reviewDAO = new ReviewDAO();
                            hasReviewed = reviewDAO.hasReviewed(loggedInMember.getId(), participant.getId(), meeting.getMeetingId());
                        }
                    %>
                    
                    <%-- 리뷰 버튼 표시 --%>
                    <% 
                    // 현재 로그인한 사용자가 모임 참가자인지 확인
                    boolean isCurrentUserParticipant = false;
                    for (Member p : participants) {
                        if (loggedInMember != null && p.getId() == loggedInMember.getId()) {
                            isCurrentUserParticipant = true;
                            break;
                        }
                    }
                    
                    if ("COMPLETED".equals(meeting.getStatus()) && 
                          loggedInMember != null && 
                          participant.getId() != loggedInMember.getId() &&
                          isCurrentUserParticipant) { %>
                        <button 
                            <% if (hasReviewed) { %>
                                disabled style="background-color: gray; cursor: default;"
                            <% } else { %>
                                onclick="openReviewModal(<%= participant.getId() %>, '<%= participant.getNickname() %>')"
                                class="mt-1 px-2 py-1 bg-black text-white text-xs rounded"
                            <% } %>
                        >
                            <%= hasReviewed ? "리뷰 완료" : "리뷰하기" %>
                        </button>
                    <% } %>
                </div>
            </div>
        <% } %>
        
    </div>
      
</div>

            <% if (loggedInMember != null) { %>
                <% if (loggedInMember.getId() == meeting.getCreatedBy()) { %>
                    <% if (meeting.isConfirmed()) { %>
                        <!-- 모임이 확정된 경우 -->
                        <% 
                            String dateTimeStr = meeting.getDate() + " " + meeting.getTime();
                            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            java.time.LocalDateTime meetingDateTime = java.time.LocalDateTime.parse(dateTimeStr, formatter);
                            boolean isAfterMeetingTime = java.time.LocalDateTime.now().isAfter(meetingDateTime);
                        %>
                        <% if ("COMPLETED".equals(meeting.getStatus())) { %>
                            <button disabled class="join-button completed">종료된 모임입니다</button>
                        <% } else if (isAfterMeetingTime) { %>
                            <button onclick="completeMeeting()" class="join-button confirm">모임 종료하기</button>
                        <% } else { %>
                            <button disabled class="join-button confirmed">확정된 모임입니다</button>
                        <% } %>
                    <% } else if ("EXPIRED".equals(meeting.getStatus())) { %>
                        <!-- 만료된 모임이고 모임장인 경우 삭제 버튼만 표시 -->
                        <form action="${pageContext.request.contextPath}/meeting/delete" method="post" 
                              onsubmit="return confirm('정말 이 모임을 삭제하시겠습니까?');" style="width:100%;">
                            <input type="hidden" name="meetingId" value="<%= meeting.getMeetingId() %>" />
                            <button type="submit" class="join-button cancel" style="width:100%;">모임 삭제</button>
                        </form>
                    <% } else { %>
                        <!-- 모임 확정 전 버튼들 -->
                        <button onclick="confirmMeeting()" class="join-button confirm">모임 확정하기</button>
                        
                        <form action="<%= request.getContextPath() %>/edit_meeting" method="get" style="display:inline;">
                            <input type="hidden" name="meetingId" value="<%= meeting.getMeetingId() %>">
                            <button type="submit" class="join-button edit">모임 정보 변경하기</button>
                        </form>
                        
                        <form action="${pageContext.request.contextPath}/meeting/delete" method="post" 
                              onsubmit="return confirm('정말 이 모임을 삭제하시겠습니까?');" style="display:inline;">
                            <input type="hidden" name="meetingId" value="<%= meeting.getMeetingId() %>" />
                            <button type="submit" class="join-button cancel">모임 삭제</button>
                        </form>
                    <% } %>
                <% } else { %>
                    <% if ("EXPIRED".equals(meeting.getStatus())) { %>
                        <!-- 모임이 만료되고 모임장이 아닌 경우 아무 버튼도 표시하지 않음 -->
                        <div class="text-center text-gray-500 mt-4">
                            이 모임은 기간이 만료되었습니다.
                        </div>
                    <% } else if (!isJoined && meeting.getCurrentParticipants() < meeting.getMaxParticipants() && !meeting.isConfirmed()) { %>
                        <!-- 참가 신청 버튼 (모임 확정 전) -->
                        <button onclick="joinMeeting()" class="join-button">참가 신청</button>
                    <% } else if (isJoined && !meeting.isConfirmed()) { %>
                        <!-- 참가 취소 버튼 (모임 확정 전) -->
                        <button onclick="cancelMeeting()" class="join-button cancel">참가 취소</button>
                    <% } else if (meeting.isCompleted()) { %>
                        <!-- 모임이 종료된 경우 버튼 비활성화 -->
                        <button disabled class="join-button completed">종료된 모임</button>
                    <% } else if (meeting.isConfirmed()) { %>
                        <!-- 모임 확정 후 모든 버튼 비활성화 -->
                        <button disabled class="join-button">확정된 모임</button>
                    <% } else { %>
                        <!-- 정원 마감 시 비활성화 -->
                        <button disabled class="join-button">정원 마감</button>
                    <% } %>
                <% } %>
            <% } %>
            <div class="text-center mt-6">
        <button onclick="goBack()" class="px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 inline-block">
            목록으로
        </button>
    </div>
        </div>
        
        <!-- 리뷰 작성 모달 -->
        <div id="reviewModal" class="fixed inset-0 bg-black bg-opacity-50 hidden items-center justify-center z-50">
            <div class="bg-white rounded-lg w-full max-w-lg p-6">
                <div class="flex justify-between items-center mb-4">
                    <h3 class="text-xl font-bold">
                        <span id="reviewTargetName"></span> 리뷰 작성
                    </h3>
                    <button onclick="closeReviewModal()" class="text-gray-500 hover:text-gray-700">
                        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                        </svg>
                    </button>
                </div>

                <form id="reviewForm" class="space-y-4">
                    <input type="hidden" id="reviewMeetingId" name="meetingId">
                    <input type="hidden" id="reviewToUserId" name="toUserId">

                    <!-- 별점 선택 -->
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">별점</label>
                        <div class="flex flex-row-reverse justify-end">
                            <% for (int i = 5; i >= 1; i--) { %>
                                <input type="radio" id="star<%= i %>" name="rating" value="<%= i %>" class="hidden" required>
                                <label for="star<%= i %>" class="text-3xl cursor-pointer text-gray-300 hover:text-yellow-400 star-label">★</label>
                            <% } %>
                        </div>
                    </div>

                    <!-- 태그 선택 -->
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">태그 선택</label>
                        <div class="flex flex-wrap gap-2" id="tagContainer">
                            <!-- 태그들이 여기에 동적으로 추가됩니다 -->
                        </div>
                    </div>

                    <div class="flex justify-end pt-4">
                        <button type="button" onclick="closeReviewModal()" class="mr-2 px-4 py-2 text-gray-600 hover:text-gray-800">
                            취소
                        </button>
                        <button type="submit" class="px-4 py-2 bg-black text-white rounded hover:bg-gray-800">
                            리뷰 작성
                        </button>
                    </div>
                </form>
            </div>
        </div>
        
    </main>
     <%@ include file="/WEB-INF/views/PSWJSP/footer.jsp"  %>
    <script>
    var loggedInMemberId = <%= (loggedInMember != null) ? loggedInMember.getId() : -1 %>;
    var meetingCreatorId = <%= meeting.getCreatedBy() %>;
    var meetingHostId = <%= meeting.getCreatedBy() %>;

    console.log("로그인한 사용자 ID:", loggedInMemberId);
    console.log("모임장 ID:", meetingCreatorId);
        function joinMeeting() {
            if (!confirm('이 모임에 참가하시겠습니까?')) {
                return;
            }

            fetch('${pageContext.request.contextPath}/meeting/join', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'meetingId=<%= meeting.getMeetingId() %>'
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.success) {
                    location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('참가 신청 중 오류가 발생했습니다.');
            });
        }

        function cancelMeeting() {
            if (!confirm('모임 참가를 취소하시겠습니까?')) {
                return;
            }

            fetch('${pageContext.request.contextPath}/meeting/cancel', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'meetingId=<%= meeting.getMeetingId() %>'
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.success) {
                    location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('참가 취소 중 오류가 발생했습니다.');
            });
        }

        function confirmMeeting() {
            if (!confirm('모임을 확정하시겠습니까?\n확정된 모임은 참가 취소가 불가능합니다.')) {
                return;
            }

            fetch('${pageContext.request.contextPath}/meeting/confirm', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'meetingId=<%= meeting.getMeetingId() %>'
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.success) {
                    location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('모임 확정 중 오류가 발생했습니다.');
            });
        }

        document.addEventListener("DOMContentLoaded", function() {
            var toggleButton = document.getElementById("toggleChangesBtn");
            var changeList = document.getElementById("meetingChangesList");

            // 초기에는 숨김 상태로 설정
            changeList.style.display = "none";

            toggleButton.addEventListener("click", function() {
                if (changeList.style.display === "none") {
                    changeList.style.display = "block";
                    toggleButton.textContent = "모임 정보 변경 이력 닫기";
                } else {
                    changeList.style.display = "none";
                    toggleButton.textContent = "모임 정보 변경 이력 보기";
                }
            });
        });

        function completeMeeting() {
            if (!confirm('모임을 종료하시겠습니까?\n종료된 모임은 다시 되돌릴 수 없습니다.')) {
                return;
            }

            fetch('${pageContext.request.contextPath}/meeting/complete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'meetingId=<%= meeting.getMeetingId() %>'
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.success) {
                    location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('모임 종료 중 오류가 발생했습니다.');
            });
        }

        // 리뷰 모달 관련 스크립트
        function openReviewModal(participantId, nickname) {
            document.getElementById('reviewMeetingId').value = '<%= meeting.getMeetingId() %>';
            document.getElementById('reviewToUserId').value = participantId;
            document.getElementById('reviewTargetName').textContent = nickname + '님';

            // 모달 표시
            document.getElementById('reviewModal').classList.remove('hidden');
            document.getElementById('reviewModal').classList.add('flex');

            // 별점 라벨 클릭 이벤트
            const starLabels = document.querySelectorAll('label[for^="star"]');
            starLabels.forEach(label => {
                label.addEventListener('click', function(e) {
                    const forAttr = e.target.getAttribute('for');
                    const starInput = document.getElementById(forAttr);
                    if (starInput) {
                        starInput.checked = true;
                        
                        // 모든 별 초기화
                        starLabels.forEach(l => l.classList.remove('text-yellow-400'));
                        // 선택한 별까지 색칠
                        let current = e.target;
                        while (current) {
                            current.classList.add('text-yellow-400');
                            current = current.nextElementSibling;
                        }
                    }
                });
            });

            // 태그 로드
            fetch(contextPath + '/reviews/tags')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('태그 로드 실패');
                    }
                    return response.json();
                })
                .then(tags => {
                    const tagContainer = document.getElementById('tagContainer');
                    tagContainer.innerHTML = '';

                    tags.forEach(tag => {
                        const tagDiv = document.createElement('div');
                        tagDiv.className = 'inline-block';

                        const input = document.createElement('input');
                        input.type = 'checkbox';
                        input.name = 'tagIds';
                        input.value = tag.tagId;
                        input.id = `checkbox-${tag.tagId}`;
                        input.className = 'hidden';

                        const label = document.createElement('label');
                        label.className = 'inline-block px-3 py-1 m-1 rounded-full border-2 border-black cursor-pointer hover:bg-gray-100 transition-colors';
                        label.textContent = tag.tagName;

                        // 라벨 클릭 시 체크 토글
                        label.addEventListener('click', function(e) {
                            e.preventDefault();
                            input.checked = !input.checked;
                            if (input.checked) {
                                label.classList.add('bg-black', 'text-white');
                                label.classList.remove('hover:bg-gray-100');
                            } else {
                                label.classList.remove('bg-black', 'text-white');
                                label.classList.add('hover:bg-gray-100');
                            }
                        });

                        tagDiv.appendChild(input);
                        tagDiv.appendChild(label);
                        tagContainer.appendChild(tagDiv);
                    });
                })
                .catch(error => {
                    console.error('태그 로드 중 오류 발생:', error);
                    document.getElementById('tagContainer').innerHTML =
                        '<p class="text-red-500">태그를 불러오는데 실패했습니다.</p>';
                });
        }

        // 리뷰 폼 제출
        document.getElementById('reviewForm').addEventListener('submit', function(e) {
            e.preventDefault();

            const meetingId = document.getElementById('reviewMeetingId').value;
            const toUserId = document.getElementById('reviewToUserId').value;
            const rating = document.querySelector('input[name="rating"]:checked')?.value;
            const selectedTags = Array.from(document.querySelectorAll('input[name="tagIds"]:checked'))
                                      .map(input => input.value);

            if (!rating) {
                alert('별점을 선택해주세요.');
                return;
            }

            const requestData = new URLSearchParams();
            requestData.append('meetingId', meetingId);
            requestData.append('toUserId', toUserId);
            requestData.append('rating', rating);
            selectedTags.forEach(tagId => {
                requestData.append('tagIds', tagId);
            });

            fetch(contextPath + '/reviews', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: requestData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('리뷰가 성공적으로 저장되었습니다.');
                    closeReviewModal();
                    location.reload();
                } else {
                    alert(data.message || '리뷰 저장에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('리뷰 저장 중 오류가 발생했습니다.');
            });
        });

        function closeReviewModal() {
            document.getElementById('reviewModal').classList.add('hidden');
            document.getElementById('reviewModal').classList.remove('flex');
            
            // 폼 리셋
            document.getElementById('reviewForm').reset();

            // 별점 스타일 초기화
            document.querySelectorAll('label[for^="star"]').forEach(label => {
                label.classList.remove('text-yellow-400');
                label.classList.add('text-gray-300');
            });

            // 태그 선택 초기화
            document.querySelectorAll('input[name="tagIds"]').forEach(input => {
                input.checked = false;
                const label = document.querySelector(`label[for="${input.id}"]`);
                if (label) {
                    label.classList.remove('bg-black', 'text-white');
                    label.classList.add('hover:bg-gray-100');
                }
            });
        }
     // 참가자 퇴장 처리
        function kickParticipant(participantId) {
        	
            if (!confirm('이 참가자를 모임에서 퇴장시키시겠습니까?')) {
                return;
            }
            console.log("퇴장 요청 실행");
            console.log("로그인한 사용자 ID:", loggedInMemberId);
            console.log("모임장 ID:", meetingCreatorId);
            console.log("퇴장할 참가자 ID:", participantId);
            console.log("모임 ID:", <%= meeting.getMeetingId() %>);

            fetch('${pageContext.request.contextPath}/meeting/kick', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `meetingId=<%= meeting.getMeetingId() %>&participantId=\${participantId}`
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                if (data.success) {
                    location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('퇴장 처리 중 오류가 발생했습니다.');
            });
        }
     // 사용자 상세 정보 모달 표시
async function showUserDetailModal(userId) {
    console.log('Fetching user details for:', userId);
    try {
        const fullUrl = contextPath + '/friends/details/' + userId;
        console.log('Final URL:', fullUrl);

        const response = await fetch(fullUrl, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Response error:', errorText);
            throw new Error('HTTP error! status: ' + response.status);
        }
        
        const userData = await response.json();
        console.log('Complete User Data:', userData);
        
        // 모달 요소 가져오기; 없으면 동적으로 생성
        let modal = document.getElementById('userDetailModal');
        if (!modal) {
            modal = document.createElement('div');
            modal.id = 'userDetailModal';
            modal.className = 'modal';
            modal.innerHTML = `
                <div class="modal-content">
                    <span class="close-modal" onclick="closeUserDetailModal()">&times;</span>
                    <div class="modal-body">
                        <div class="user-profile">
                            <img id="modalProfileImage" src="" alt="Profile" class="modal-profile-image" style="width:100px;height:100px;">
                            <div class="user-basic-info">
                                <h4 id="modalNickname" class="text-lg font-bold"></h4>
                                <div class="rating-info flex items-center gap-2">
                                    <span id="modalRating" class="text-yellow-500 font-semibold"></span>
                                    <span id="modalReviewCount" class="text-gray-600"></span>
                                </div>
                            </div>
                        </div>
                        <div class="section">
                            <h5 class="text-md font-semibold mb-2">받은 태그</h5>
                            <div id="modalTags" class="tags-container"></div>
                        </div>
                        <div class="section">
                            <h5 class="text-md font-semibold mb-2">활동 지역</h5>
                            <div id="modalRegions" class="regions-container"></div>
                        </div>
                        <div class="section">
                            <h5 class="text-md font-semibold mb-2">관심 게임</h5>
                            <div id="modalGames" class="games-container"></div>
                        </div>
                        <div class="modal-actions mt-4 text-center">
                            <button id="friendRequestBtn" class="friend-request-btn"></button>
                        </div>
                    </div>
                </div>
            `;
            document.body.appendChild(modal);
        }
        
        // 모달 내 요소들 가져오기
        const modalProfileImage = document.getElementById('modalProfileImage');
        const modalNickname = document.getElementById('modalNickname');
        const modalRating = document.getElementById('modalRating');
        const modalReviewCount = document.getElementById('modalReviewCount');
        const tagsContainer = document.getElementById('modalTags');
        const regionsContainer = document.getElementById('modalRegions');
        const gamesContainer = document.getElementById('modalGames');
        const friendRequestBtn = document.getElementById('friendRequestBtn');
        
        // 모달 내용 채우기
        modalProfileImage.src = userData.profileImageUrl || 'https://via.placeholder.com/100';
        modalNickname.textContent = userData.nickname;
        modalRating.innerHTML = `⭐ \${userData.averageRating.toFixed(1)}`;
        modalReviewCount.innerHTML = `(\${userData.totalReviews}개의 리뷰)`;
        
        // 태그 처리
        tagsContainer.innerHTML = userData.tags && userData.tags.length > 0
            ? userData.tags.map(tag => 
                `<div class="flex items-center bg-gray-100 text-gray-800 rounded-full py-1 px-3 mr-2 mb-2">
                    <span class="text-sm font-medium">\${tag.name}</span>
                    <span class="ml-2 bg-gray-700 text-white text-xs rounded-full px-2 py-0.5">\${tag.count}</span>
                </div>`
              ).join('')
            : '<span class="text-gray-500">받은 태그가 없습니다</span>';

        // 활동 지역 처리
        regionsContainer.innerHTML = userData.regions && userData.regions.length > 0
            ? userData.regions.map(region => 
                `<span class="bg-blue-100 text-blue-800 text-sm rounded px-2 py-1 mr-2 mb-2">\${region}</span>`
              ).join('')
            : '<span class="text-gray-500">등록된 활동 지역이 없습니다</span>';

        // 관심 게임 처리
        gamesContainer.innerHTML = userData.games && userData.games.length > 0
            ? userData.games.map(game => 
                `<span class="bg-green-100 text-green-800 text-sm rounded px-2 py-1 mr-2 mb-2">\${game}</span>`
              ).join('')
            : '<span class="text-gray-500">관심 게임이 없습니다</span>';

        // 친구 상태 처리
        friendRequestBtn.className = 'friend-request-btn px-4 py-2 rounded text-white font-medium';
        friendRequestBtn.disabled = false;
        friendRequestBtn.onclick = null; // 이전 이벤트 제거

        // 로그인한 사용자와 프로필을 보고 있는 사용자가 같은 경우
        if (userId === loggedInMemberId) {
            friendRequestBtn.textContent = '내 프로필';
            friendRequestBtn.classList.add('bg-gray-500');
            friendRequestBtn.disabled = true;
        } else {
            switch(userData.friendStatus) {
                case 'FRIENDS':
                    friendRequestBtn.textContent = '현재 친구입니다';
                    friendRequestBtn.classList.add('bg-gray-500');
                    friendRequestBtn.disabled = true;
                    break;
                case 'PENDING':
                    friendRequestBtn.textContent = '친구 요청 중';
                    friendRequestBtn.classList.add('bg-yellow-500');
                    friendRequestBtn.disabled = true;
                    break;
                default:
                    friendRequestBtn.textContent = '친구 추가';
                    friendRequestBtn.classList.add('bg-blue-500', 'hover:bg-blue-600');
                    friendRequestBtn.onclick = () => sendFriendRequest(userId);
            }
        }

        // 모달 표시
        modal.style.display = 'flex';
        console.log('모달 표시 완료:', modal);
    } catch (error) {
        console.error('Error in showUserDetailModal:', error);
        alert('사용자 정보를 불러오는데 실패했습니다.');
    }
}
async function sendFriendRequest(receiverId) {
    if (!confirm('친구 요청을 보내시겠습니까?')) {
        return;
    }

    try {
        const params = new URLSearchParams();
        params.append('receiverId', receiverId);

        const response = await fetch(contextPath + '/friends/send-request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
            },
            body: params
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        
        if (data.success) {
            alert(data.message);
            const friendRequestBtn = document.getElementById('friendRequestBtn');
            friendRequestBtn.textContent = '친구 요청 중';
            friendRequestBtn.classList.remove('bg-blue-500', 'hover:bg-blue-600');
            friendRequestBtn.classList.add('bg-yellow-500');
            friendRequestBtn.disabled = true;
        } else {
            alert(data.message || '요청 처리 중 오류가 발생했습니다.');
        }
    } catch (error) {
        console.error('Friend request error:', error);
        alert('친구 요청 전송 중 오류가 발생했습니다.');
    }
}

function closeUserDetailModal() {
    const modal = document.getElementById('userDetailModal');
    if (modal) {
        modal.style.display = 'none';
    }
}
function goBack() {
    // 세션 스토리지에서 이전 URL 가져오기
    const previousUrl = sessionStorage.getItem('previousUrl');
    if (previousUrl) {
        window.location.href = previousUrl;
    } else {
        // 저장된 URL이 없는 경우 기본 목록 페이지로 이동
        window.location.href = '${pageContext.request.contextPath}/meetings';
    }
}
    </script>
</body>
</html>
