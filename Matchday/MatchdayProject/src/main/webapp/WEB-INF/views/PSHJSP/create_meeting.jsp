<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map, java.util.List" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>모임 만들기 - Matchday</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/create_meeting.css"">
    <script>
    var contextPath = '<%=request.getContextPath()%>';
</script>
</head>
<body>
    <!-- Navigation Bar -->
     <<jsp:include page="/WEB-INF/views/PSWJSP/nav.jsp" />

    <!-- Main Content -->
    <main>
        <section class="header-section">
            <h2>모임 만들기</h2>
            <% if (request.getAttribute("errorMessage") != null) { %>
                <p class="error-message"><%= request.getAttribute("errorMessage") %></p>
            <% } %>
            <form action="${pageContext.request.contextPath}/create_meeting" method="post" class="meeting-form">
                <label for="title">모임 제목</label>
                <input type="text" id="title" name="title" placeholder="모임 제목을 입력하세요" required>

                <label for="description">모임 설명</label>
                <textarea id="description" name="description" rows="4" placeholder="모임에 대한 설명을 입력하세요" required></textarea>

                <!-- 게임 선택 부분을 이렇게 수정 -->
<div class="input-group">
    <label for="gameId">게임 선택</label>
    <input type="hidden" id="gameId" name="gameId" required>
    <div class="game-select-container">
        <span id="selectedGameDisplay">게임을 선택해주세요</span>
        <button type="button" onclick="openGameModal()" class="game-select-button">게임 선택</button>
    </div>
</div>

<!-- 모달 포함 -->
<%@ include file="game_select_modal.jsp" %>

               <label for="regionId">시/도 선택</label>
<select id="regionId" name="regionId" onchange="loadDistricts()" required>
    <option value="">시/도를 선택하세요</option>
    <% 
        List<Map<String, Object>> regions = (List<Map<String, Object>>) request.getAttribute("regions");
        for (Map<String, Object> region : regions) { 
    %>
        <option value="<%= region.get("id") %>"><%= region.get("name") %></option>
    <% 
        } 
    %>
</select>

<label for="districtId">시/군/구 선택</label>
<select id="districtId" name="districtId" required>
    <option value="">시/군/구를 선택하세요</option>
</select>

<script>
    function loadDistricts() {
        const regionId = document.getElementById("regionId").value;
        const districtSelect = document.getElementById("districtId");
        districtSelect.innerHTML = '<option value="">시/군/구를 선택하세요</option>';

        if (regionId) {
            fetch('${pageContext.request.contextPath}/api/regions/districts/' + regionId)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('네트워크 응답이 실패했습니다.');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.length === 0) {
                        const option = document.createElement("option");
                        option.textContent = '해당 시/도에 시/군/구가 없습니다';
                        districtSelect.appendChild(option);
                        return;
                    }

                    data.forEach(district => {
                        const option = document.createElement("option");
                        option.value = district.id;
                        option.textContent = district.name;
                        districtSelect.appendChild(option);
                    });
                })
                .catch(error => console.error('Error fetching districts:', error));
        }
    }
</script>


                <label for="date">모임 날짜</label>
                <input type="date" id="date" name="date" required>

                <label for="time">모임 시간</label>
                <input type="time" id="time" name="time" required>

                <label for="maxParticipants">최대 인원</label>
                <input type="number" id="maxParticipants" name="maxParticipants" min="2" max="20" required>

                <button type="submit" class="button button-primary">모임 만들기</button>
            </form>
        </section>
    </main>
 <%@ include file="/WEB-INF/views/PSWJSP/footer.jsp"  %>
</body>
</html>