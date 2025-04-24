<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="PSH.model.Member" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>친구 목록 - Matchday</title>
      <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/friends_list.css?v=2">
    <script>
    var contextPath = '${pageContext.request.contextPath}';
</script>
</head>
<body>
    <!-- Navigation Bar -->
     <<jsp:include page="/WEB-INF/views/PSWJSP/nav.jsp" />

    <!-- Main Content -->
    <main class="friends-container">
        <!-- Navigation Tabs -->
        <div class="nav-tabs">
            <a href="${pageContext.request.contextPath}/friends" class="nav-tab">친구 찾기</a>
            <a href="${pageContext.request.contextPath}/friends/requests" class="nav-tab">친구 요청</a>
            <a href="${pageContext.request.contextPath}/friends/list" class="nav-tab active">친구 목록</a>
        </div>

        <!-- Friends Grid -->
        <div class="friends-grid">
            <% 
            List<Member> friends = (List<Member>) request.getAttribute("friends");
            if(friends != null && !friends.isEmpty()) {
                for(Member friend : friends) {
            %>
                <div class="friend-card">
                    <img src="<%= friend.getProfileImageUrl() != null ? friend.getProfileImageUrl() : "https://placehold.it/60" %>" 
                         alt="Profile" 
                         class="profile-image">
                    <div class="friend-info">
                        <div class="friend-name"><%= friend.getNickname() %></div>
                        <% if (friend.getFavoriteGames() != null && !friend.getFavoriteGames().isEmpty()) { %>
                            <div class="favorite-games">
                                <span class="info-label">관심게임:</span>
                                <%= friend.getFavoriteGames() %>
                            </div>
                        <% } %>
                    </div>
                    <div class="friend-actions">
                        <button class="delete-btn" onclick="deleteFriend(<%= friend.getId() %>)">
                            친구 삭제
                        </button>
                    </div>
                </div>
            <%
                }
            } else {
            %>
                <div class="empty-message">아직 친구가 없습니다.</div>
            <%
            }
            %>
        </div>
    </main>
    
    <%@ include file="/WEB-INF/views/PSWJSP/footer.jsp"  %>

    <script>
    function deleteFriend(friendId) {
        if (!confirm('정말 이 친구를 삭제하시겠습니까?')) {
            return;
        }

        fetch('${pageContext.request.contextPath}/friends/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'friendId=' + friendId
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
            alert('친구 삭제 중 오류가 발생했습니다.');
        });
    }
    </script>
</body>
</html>