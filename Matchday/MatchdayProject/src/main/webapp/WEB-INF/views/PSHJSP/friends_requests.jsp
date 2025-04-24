<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="PSH.model.FriendRequest" %>
<%@ page import="PSH.model.Member" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>친구 요청 - Matchday</title>
  <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/friends_requests.css?v=2">
  <style>
    /* 스타일은 그대로 사용 */
    .requests-container {
      max-width: 1200px;
      margin: 80px auto 0;
      padding: 20px;
    }
    .nav-tabs {
    display: flex;
    gap: 1rem;
    margin-bottom: 2rem;
    border-bottom: 1px solid var(--border-color);
    padding-bottom: 1rem;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none; /* Firefox */
    -ms-overflow-style: none; /* IE and Edge */
}

.nav-tabs::-webkit-scrollbar {
    display: none; /* Chrome, Safari, Opera */
}

.nav-tab {
    padding: 0.75rem 1.5rem;
    text-decoration: none;
    color: #666;
    font-weight: 500;
    border-radius: 6px;
    white-space: nowrap;
    transition: all 0.2s ease;
    font-size: 1rem;
    flex-shrink: 0;
}

.nav-tab:hover {
    background-color: #f3f4f6;
    color: #333;
}

.nav-tab.active {
    background-color: black;
    color: white;
}
    .request-tabs {
      display: flex;
      gap: 1rem;
      margin-bottom: 1rem;
    }
    .request-tab {
      padding: 0.5rem 1rem;
      cursor: pointer;
      border: none;
      background: none;
      font-weight: 500;
      color: #666;
      border-bottom: 2px solid transparent;
    }
    .request-tab.active {
      color: black;
      border-bottom-color: black;
    }
    .request-section {
      display: none;
    }
    .request-section.active {
      display: block;
    }
    .request-card {
      background: white;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 1rem;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      display: flex;
      align-items: center;
      gap: 20px;
    }
    .profile-image {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      object-fit: cover;
    }
    .request-info {
      flex: 1;
    }
    .request-user {
      font-weight: bold;
      font-size: 1.1em;
      margin-bottom: 0.5rem;
    }
    .request-message {
      color: #666;
      margin-bottom: 0.5rem;
    }
    .request-date {
      font-size: 0.9em;
      color: #999;
    }
    .request-actions {
      display: flex;
      gap: 10px;
    }
    .action-btn {
      padding: 8px 16px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 500;
      transition: background-color 0.2s;
    }
    .accept-btn {
      background-color: black;
      color: white;
    }
    .accept-btn:hover {
      background-color: #333;
    }
    .reject-btn {
      background-color: #ff4444;
      color: white;
    }
    .reject-btn:hover {
      background-color: #cc0000;
    }
    .empty-message {
      text-align: center;
      padding: 40px;
      background: white;
      border-radius: 8px;
      color: #666;
      margin-top: 1rem;
    }
    .pending-status {
      padding: 4px 8px;
      background-color: #f0f0f0;
      border-radius: 4px;
      font-size: 0.9em;
      color: #666;
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
  <main class="requests-container">
    <!-- Navigation Tabs -->
    <div class="nav-tabs">
      <a href="${pageContext.request.contextPath}/friends" class="nav-tab">친구 찾기</a>
      <a href="${pageContext.request.contextPath}/friends/requests" class="nav-tab active">친구 요청</a>
      <a href="${pageContext.request.contextPath}/friends/list" class="nav-tab">친구 목록</a>
    </div>

    <!-- Request Tabs: 인라인 이벤트 제거 및 data-tab 속성 사용 -->
    <div class="request-tabs">
      <button class="request-tab active" data-tab="received">받은 요청</button>
      <button class="request-tab" data-tab="sent">보낸 요청</button>
    </div>

    <!-- Received Requests -->
    <div id="received-requests" class="request-section active">
      <%
      List<FriendRequest> receivedRequests = (List<FriendRequest>) request.getAttribute("receivedRequests");
      if(receivedRequests != null && !receivedRequests.isEmpty()) {
          for(FriendRequest friendRequest : receivedRequests) {
      %>
     <div class="request-card">
    <img src="<%= friendRequest.getSenderProfileUrl() != null ? friendRequest.getSenderProfileUrl() : "https://placehold.it/60" %>" 
         alt="Profile" 
         class="profile-image">
    <div class="request-info">
        <div class="request-user"><%= friendRequest.getSenderName() %></div>
        <% if (friendRequest.getSenderRegions() != null && !friendRequest.getSenderRegions().isEmpty()) { %>
            <div class="user-regions">
                <span class="info-label">활동지역:</span>
                <%= friendRequest.getSenderRegions() %>
            </div>
        <% } %>
        <% if (friendRequest.getSenderGames() != null && !friendRequest.getSenderGames().isEmpty()) { %>
            <div class="user-games">
                <span class="info-label">관심게임:</span>
                <%= friendRequest.getSenderGames() %>
            </div>
        <% } %>
        <div class="request-date"><%= friendRequest.getCreatedAt() %></div>
    </div>
    <div class="request-actions">
        <button class="action-btn accept-btn" onclick="respondToRequest(<%= friendRequest.getId() %>, 'accept')">수락</button>
        <button class="action-btn reject-btn" onclick="respondToRequest(<%= friendRequest.getId() %>, 'reject')">거절</button>
    </div>
</div>
      <%
          }
      } else {
      %>
      <div class="empty-message">받은 친구 요청이 없습니다.</div>
      <%
      }
      %>
    </div>

    <!-- Sent Requests -->
    <div id="sent-requests" class="request-section">
      <%
      List<FriendRequest> sentRequests = (List<FriendRequest>) request.getAttribute("sentRequests");
      if(sentRequests != null && !sentRequests.isEmpty()) {
          for(FriendRequest friendRequest : sentRequests) {
      %>
      <div class="request-card">
        <img src="<%= friendRequest.getReceiverProfileUrl() != null ? friendRequest.getReceiverProfileUrl() : "https://placehold.it/60" %>" 
             alt="Profile" 
             class="profile-image">
        <div class="request-info">
          <div class="request-user"><%= friendRequest.getReceiverName() %></div>
          <div class="request-date"><%= friendRequest.getCreatedAt() %></div>
        </div>
        <div class="request-actions">
          <span class="pending-status">대기중</span>
        </div>
      </div>
      <%
          }
      } else {
      %>
      <div class="empty-message">보낸 친구 요청이 없습니다.</div>
      <%
      }
      %>
    </div>
  </main>
  
  <%@ include file="/WEB-INF/views/PSWJSP/footer.jsp"  %>

  <script>
  function respondToRequest(requestId, action) {
	    if (!confirm(action === 'accept' ? '친구 요청을 수락하시겠습니까?' : '친구 요청을 거절하시겠습니까?')) {
	        return;
	    }

	    const formData = new URLSearchParams();
	    formData.append('requestId', requestId);
	    formData.append('action', action);

	    // 웹뷰 환경 감지
	    const isWebView = /wv|WebView/.test(navigator.userAgent);
	    
	    // 기본 헤더 설정
	    const headers = {
	        'Content-Type': 'application/x-www-form-urlencoded'
	    };

	    // 웹뷰일 경우 추가 헤더
	    if (isWebView) {
	        headers['Accept'] = 'application/json';
	        headers['Cache-Control'] = 'no-cache';
	    }

	    fetch(contextPath + '/friends/respond-request', {
	        method: 'POST',
	        headers: headers,
	        body: formData.toString()
	    })
	    .then(response => {
	        console.log('Response status:', response.status);
	        if (!response.ok) {
	            return response.text().then(text => {
	                throw new Error('서버 응답 오류: ' + response.status);
	            });
	        }
	        return response.json();
	    })
	    .then(data => {
	        if (data.success) {
	            alert(data.message || '처리가 완료되었습니다.');
	            if (isWebView) {
	                // 웹뷰에서는 강제 새로고침
	                window.location.href = window.location.href;
	            } else {
	                location.reload();
	            }
	        } else {
	            alert(data.message || '처리 중 오류가 발생했습니다.');
	        }
	    })
	    .catch(error => {
	        console.error('Error:', error);
	        alert('요청 처리 중 오류가 발생했습니다.');
	    });
	}
    // JSP의 EL 충돌을 피하기 위해 템플릿 리터럴 대신 문자열 결합 사용
    function showTab(tabType) {
      tabType = tabType || 'received';
      console.log('Showing tab:', tabType);
      
      // 탭 버튼 active 클래스 업데이트
      document.querySelectorAll('.request-tab').forEach(function(tab) {
        tab.classList.toggle('active', tab.getAttribute('data-tab') === tabType);
      });
      
      // 모든 섹션 숨김
      document.querySelectorAll('.request-section').forEach(function(section) {
        section.classList.remove('active');
      });
      
      // 문자열 결합으로 targetId 생성
      var targetId = tabType + '-requests';
      console.log('Target section id:', targetId);
      var targetSection = document.getElementById(targetId);
      if (targetSection) {
        targetSection.classList.add('active');
      } else {
        console.error('Target section not found:', targetId);
      }
    }

    document.addEventListener('DOMContentLoaded', function() {
      // data-tab 속성을 사용해 이벤트 리스너 등록
      document.querySelectorAll('.request-tab').forEach(function(tab) {
        tab.addEventListener('click', function() {
          var tabType = this.getAttribute('data-tab');
          console.log('Clicked tab type:', tabType, this.outerHTML);
          showTab(tabType);
        });
      });
      
      // 페이지 로드 시 기본 'received' 탭 활성화
      showTab('received');
    });
  </script>
</body>
</html>

