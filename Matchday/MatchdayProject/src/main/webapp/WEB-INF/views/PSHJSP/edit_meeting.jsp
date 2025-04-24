<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="PSH.model.Meeting" %>
<%@ page import="PSH.model.BoardGame" %>

<%
    Meeting meeting = (Meeting) request.getAttribute("meeting");
    List<Map<String, Object>> regions = (List<Map<String, Object>>) request.getAttribute("regions");
    List<Map<String, Object>> districts = (List<Map<String, Object>>) request.getAttribute("districts");
    List<BoardGame> games = (List<BoardGame>) request.getAttribute("games");
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>모임 수정</title>
    <link rel="stylesheet" href="/css/PSHCSS/edit_meeting.css">
</head>
<body>
    <<jsp:include page="/WEB-INF/views/PSWJSP/nav.jsp" />

    <main>
        <div class="edit-meeting-wrapper">
            <div class="left-side">
                <div class="slogan">
                    <h2>모임 수정</h2>
                    <p>원하는 대로 모임 정보를 변경하세요.</p>
                </div>
            </div>
            <div class="right-side">
                <div class="form-container">
                    <h3>모임 정보 수정</h3>
                    <form action="${pageContext.request.contextPath}/edit_meeting" method="post">
                        <input type="hidden" name="meetingId" value="<%= meeting.getMeetingId() %>">

                        <div class="input-group">
    					<label for="title">모임 제목</label>
    					<input type="text" 
           					id="title" 
           					name="title" 
           					value="<%= meeting.getTitle() %>" 
           					required>
						</div>
                        <div class="input-group">
    					<label for="description">모임 설명</label>
    					<textarea id="description" name="description" rows="4" required><%= meeting.getDescription() %></textarea>
						</div>

                        <!-- 게임 선택 -->
                        <div class="input-group">
                            <label for="gameId">게임 선택</label>
                            <input type="hidden" id="gameId" name="gameId" required>
                            <div class="game-select-container">
                                <span id="selectedGameDisplay"><%= meeting.getGameName() %></span>
                                <button type="button" onclick="openGameModal()" class="game-select-button">게임 변경</button>
                            </div>
                        </div>

                        <!-- 지역 선택 -->
                        <div class="input-group">
                            <label for="regionId">시/도 선택</label>
                            <select id="regionId" name="regionId" onchange="loadDistricts()" required>
                                <option value="">시/도를 선택하세요</option>
                                <% for (Map<String, Object> region : regions) { 
                                    boolean isSelected = ((Integer)region.get("id")).equals(meeting.getRegionId());
                                %>
                                    <option value="<%= region.get("id") %>" <%= isSelected ? "selected" : "" %>>
                                        <%= region.get("name") %>
                                    </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="input-group">
                            <label for="districtId">시/군/구 선택</label>
                            <select id="districtId" name="districtId" required>
                                <option value="">시/군/구를 선택하세요</option>
                                <% for (Map<String, Object> district : districts) { 
                                    boolean isSelected = ((Integer)district.get("id")).equals(meeting.getDistrictId());
                                %>
                                    <option value="<%= district.get("id") %>" <%= isSelected ? "selected" : "" %>>
                                        <%= district.get("name") %>
                                    </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="input-group">
                            <label for="date">날짜</label>
                            <input type="date" id="date" name="date" value="<%= meeting.getDate() %>" required>
                        </div>

                        <div class="input-group">
                            <label for="time">시간</label>
                            <input type="time" id="time" name="time" value="<%= meeting.getTime() %>" required>
                        </div>
                        
                        <div class="input-group">
    <label for="maxParticipants">최대 인원</label>
    <input type="number" 
           id="maxParticipants" 
           name="maxParticipants" 
           value="<%= meeting.getMaxParticipants() %>" 
           min="<%= meeting.getCurrentParticipants() %>" 
           required />
    <small>현재 참가자 수: <%= meeting.getCurrentParticipants() %>명</small>
</div>

                        <button type="submit" class="button">수정 완료</button>
                    </form>
                </div>
            </div>
        </div>
    </main>

    <!-- 게임 선택 모달 포함 -->
    <%@ include file="game_select_modal.jsp" %>

  <script>
function loadDistricts() {
    const regionId = document.getElementById("regionId").value;
    const districtSelect = document.getElementById("districtId");
    districtSelect.innerHTML = '<option value="">시/군/구를 선택하세요</option>';

    if (regionId) {
        fetch('${pageContext.request.contextPath}/api/regions/districts/' + regionId)
            .then(response => response.json())
            .then(data => {
                data.forEach(district => {
                    const option = document.createElement("option");
                    option.value = district.id;
                    option.textContent = district.name;
                    districtSelect.appendChild(option);
                });
                // 현재 선택된 시/군/구가 있다면 선택
                const currentDistrictId = '<%= meeting.getDistrictId() %>';
                if (currentDistrictId) {
                    districtSelect.value = currentDistrictId;
                }
            })
            .catch(error => console.error('Error:', error));
    }
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 현재 선택된 게임 ID 설정
    document.getElementById('gameId').value = '<%= meeting.getGameId() %>';
    
    // 현재 선택된 지역 ID 설정
    const currentRegionId = '<%= meeting.getRegionId() %>';
    const regionSelect = document.getElementById('regionId');
    regionSelect.value = currentRegionId;
    
    // 시/군/구 목록 로드 및 현재 선택된 시/군/구 설정
    loadDistricts();
});
</script>
</body>
</html>