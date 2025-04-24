<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="PSH.model.Meeting" %>
<%@ page import="PSH.model.Member" %>

<%
    Member loggedInMember = (Member) request.getAttribute("loggedInMember");
    List<Meeting> meetings = (List<Meeting>) request.getAttribute("meetings");
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>모임 찾기 - Matchday</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/meetings.css?v=<%= System.currentTimeMillis() %>" />
    <script>
    var contextPath = '${pageContext.request.contextPath}';
</script>
</head>
<body>
        <!-- Navigation Bar -->
    <!-- Navigation -->
<jsp:include page="/WEB-INF/views/PSWJSP/nav.jsp" />

    <!-- Main Content -->
    <main>
        <!-- Header Section -->
        <section class="header-section">
            <div class="filters">
    <form action="${pageContext.request.contextPath}/meetings" method="get" id="searchForm">
        <!-- 시/도 선택 -->
        <select name="region" class="filter-select" onchange="loadDistricts(this.value)">
            <option value="">시/도 선택</option>
            <% 
                List<Map<String, Object>> regions = (List<Map<String, Object>>) request.getAttribute("regions");
                int selectedRegion = (Integer)request.getAttribute("selectedRegion");
                
                for(Map<String, Object> region : regions) {
                    int regionId = (Integer)region.get("id");
                    String selected = (regionId == selectedRegion) ? "selected" : "";
            %>
                <option value="<%= regionId %>" <%= selected %>><%= region.get("name") %></option>
            <% } %>
        </select>

        <!-- 시/군/구 선택 -->
        <select name="district" class="filter-select" id="districtSelect">
            <option value="">시/군/구 선택</option>
            <% 
                List<Map<String, Object>> districts = (List<Map<String, Object>>) request.getAttribute("districts");
                int selectedDistrict = (Integer)request.getAttribute("selectedDistrict");
                
                if(districts != null) {
                    for(Map<String, Object> district : districts) {
                        int districtId = (Integer)district.get("id");
                        String selected = (districtId == selectedDistrict) ? "selected" : "";
            %>
                <option value="<%= districtId %>" <%= selected %>><%= district.get("name") %></option>
            <% 
                    }
                } 
            %>
        </select>

        <!-- 게임 검색 -->
        <div class="search-container">
            <input type="text" 
                   name="gameSearch" 
                   class="game-search" 
                   placeholder="게임 검색..."
                   value="<%= request.getAttribute("searchKeyword") != null ? request.getAttribute("searchKeyword") : "" %>">
            <button type="submit" class="search-button">검색</button>
        </div>
        
         <!-- 모임 생성 버튼 추가 -->
        <% if (loggedInMember != null) { %>
            <button type="button" onclick="location.href='${pageContext.request.contextPath}/create_meeting'" class="create-meeting">모임 만들기</button>
        <% } %>
    </form>
</div>
    </form>
</div>

<!-- "내가 참가한 모임" 체크박스 추가 -->
        <% if (loggedInMember != null) { %>
        <div class="filter-options">
    <label>
        <input type="checkbox" id="myMeetingsFilter" onchange="filterMyMeetings()"
            ${param.myMeetings == 'on' ? 'checked' : ''}>
        내가 참가한 모임만 보기
    </label>
</div>
 <% } %>           

<!-- 시/군/구 동적 로딩을 위한 JavaScript -->
<script>
function loadDistricts(regionId) {
    const districtSelect = document.getElementById('districtSelect');
    districtSelect.innerHTML = '<option value="">시/군/구 선택</option>';
    
    if (!regionId) {
        return;
    }

    fetch('${pageContext.request.contextPath}/api/regions/districts/' + regionId)
        .then(response => response.json())
        .then(data => {
            data.forEach(district => {
                const option = document.createElement('option');
                option.value = district.id;
                option.textContent = district.name;
                districtSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error:', error));
        
    // 지역이 변경되면 폼 제출
    document.getElementById('searchForm').submit();
}

// 시/군/구 선택 시 자동 제출
document.getElementById('districtSelect').addEventListener('change', function() {
    document.getElementById('searchForm').submit();
});
function filterMyMeetings() {
    let checkbox = document.getElementById("myMeetingsFilter");
    let url = new URL(window.location.href);
    
    if (checkbox.checked) {
        url.searchParams.set("myMeetings", "on");
    } else {
        url.searchParams.delete("myMeetings");
    }
    
    window.location.href = url.toString();
}

</script>
        </section>

        <!-- Meetings Grid -->
        <section class="meetings-grid">
            <% if (meetings != null && !meetings.isEmpty()) { %>
                <% for (Meeting meeting : meetings) { %>
                    <div class="meeting-card <%= meeting.isConfirmed() ? "confirmed" : "" %>">
                        <div class="card-header">
                            <div class="title-wrapper">
                            <div class="meeting-badges">
        <% if (meeting.isUserParticipating()) { %>
            <span class="participating-badge">참가중</span>
        <% } %>
         <% String status = meeting.getStatus();
        if (status != null) {
        	if (status.equals("CONFIRMED")) { %>
            <span class="confirmed-badge">확정</span>
        <% } else if (status.equals("EXPIRED")) { %>
            <span class="expired-badge">기간만료</span>
        <% } else if (status.equals("COMPLETED")) { %>
            <span class="completed-badge">종료</span>
        <% }
    } else if (meeting.isConfirmed()) { %>
        <span class="confirmed-badge">확정</span>
    <% } %>
</div>
    <h3>
        <%= meeting.getTitle() %> 
    </h3>
</div>
                            <span class="participants-count <%= meeting.getCurrentParticipants() == meeting.getMaxParticipants() ? "full-participants" : "" %>"><%= meeting.getCurrentParticipants() %>/<%= meeting.getMaxParticipants() %>명</span>
                        </div>
                        <div class="card-content">
                            <div class="game-info">
                                <span class="game-name">🎲 <%= meeting.getGameName() %></span>
                            </div>
                            <div class="meeting-info">
                                <div class="location">🗺️ <%= meeting.getRegionName() %> <%= meeting.getDistrictName() %></div>
                                <div class="time">📅 <%= meeting.getDate() %> <%= meeting.getTime() %></div>
                            </div>
                            <div class="progress-bar">
                                <div class="progress" style="width: <%= (meeting.getCurrentParticipants() * 100 / meeting.getMaxParticipants()) %>%"></div>
                            </div>
                            <div class="host-info">
                                <span class="host-name">👤 모임장: <%= meeting.getHostName() %></span>
                                <span class="host-rating">⭐ <%= meeting.getHostRating() %></span>
                            </div>
                            <div class="actions">
                              <button onclick="saveCurrentUrlAndNavigate('${pageContext.request.contextPath}/meeting/<%= meeting.getMeetingId() %>')">
    상세 보기
</button>
                            </div>
                        </div>
                    </div>
                <% } %>
            <% } else { %>
                <p>등록된 모임이 없습니다.</p>
            <% } %>
        </section>
        <div class="pagination">
    <%
        int currentPage = (Integer)request.getAttribute("currentPage");
        int totalPages = (Integer)request.getAttribute("totalPages");
        int startPage = Math.max(1, currentPage - 4);
        int endPage = Math.min(totalPages, currentPage + 4);
        
        // 현재 URL 파라미터 유지를 위한 쿼리스트링 생성
        String queryString = request.getQueryString();
        if (queryString == null) queryString = "";
        if (queryString.contains("page=")) {
            queryString = queryString.replaceAll("page=\\d+", "");
        }
        if (!queryString.isEmpty() && !queryString.endsWith("&")) {
            queryString += "&";
        }
    %>
    
    <div class="pagination-container">
        <!-- 처음 페이지로 -->
        <% if (currentPage > 1) { %>
            <a href="?<%= queryString %>page=1" class="page-link first">
                &lt;&lt;
            </a>
        <% } %>

        <!-- 이전 페이지로 -->
        <% if (currentPage > 1) { %>
            <a href="?<%= queryString %>page=<%= currentPage - 1 %>" class="page-link prev">
                &lt;
            </a>
        <% } %>

        <!-- 페이지 번호 -->
        <% for (int i = startPage; i <= endPage; i++) { %>
            <a href="?<%= queryString %>page=<%= i %>" 
               class="page-link <%= (i == currentPage) ? "active" : "" %>">
                <%= i %>
            </a>
        <% } %>

        <!-- 다음 페이지로 -->
        <% if (currentPage < totalPages) { %>
            <a href="?<%= queryString %>page=<%= currentPage + 1 %>" class="page-link next">
                &gt;
            </a>
        <% } %>

        <!-- 마지막 페이지로 -->
        <% if (currentPage < totalPages) { %>
            <a href="?<%= queryString %>page=<%= totalPages %>" class="page-link last">
                &gt;&gt;
            </a>
        <% } %>
    </div>
</div>
        
    </main>
    
    <%@ include file="/WEB-INF/views/PSWJSP/footer.jsp"  %>
    
</body>
<script>
function saveCurrentUrlAndNavigate(targetUrl) {
    // 현재 페이지의 URL을 세션 스토리지에 저장
    sessionStorage.setItem('previousUrl', window.location.href);
    // 상세 페이지로 이동
    window.location.href = targetUrl;
}
</script>
</html>
