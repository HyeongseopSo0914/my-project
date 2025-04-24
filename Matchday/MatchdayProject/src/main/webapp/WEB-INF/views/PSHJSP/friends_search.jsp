<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="PSH.model.Member" %>
<%@ page import="PSH.model.BoardGame" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>친구 찾기 - Matchday</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/friends_search.css">
    <script>
        var contextPath = '${pageContext.request.contextPath}';
    </script>
</head>
<body>
    <!-- Navigation Bar -->
    <jsp:include page="/WEB-INF/views/PSWJSP/nav.jsp" />
    <!-- Main Content -->

    <main class="friends-container">
        <!-- 탭 메뉴 -->
        <div class="nav-tabs">
            <a href="${pageContext.request.contextPath}/friends" class="nav-tab active">친구 찾기</a>
            <a href="${pageContext.request.contextPath}/friends/requests" class="nav-tab">친구 요청</a>
            <a href="${pageContext.request.contextPath}/friends/list" class="nav-tab">친구 목록</a>
        </div>

        <!-- 검색 필터 영역 -->
        <div class="search-filters">
            <div class="filter-group">
                <!-- 시/도 선택 -->
                <select id="regionSelect" class="filter-select">
                    <option value="">시/도 선택</option>
                    <%
                        List<Map<String, Object>> regions = (List<Map<String, Object>>) request.getAttribute("regions");
                        String selectedRegion = request.getParameter("region");
                        if(regions != null) {
                            for(Map<String, Object> region : regions) {
                                String selected = region.get("id").toString().equals(selectedRegion) ? "selected" : "";
                    %>
                    <option value="<%= region.get("id") %>" <%= selected %>><%= region.get("name") %></option>
                    <%
                            }
                        }
                    %>
                </select>

                <!-- 시/군/구 선택 -->
                <select id="districtSelect" class="filter-select" onchange="searchUsers()" disabled>
                    <option value="">시/군/구 선택</option>
                    <%
                        List<Map<String, Object>> districts = (List<Map<String, Object>>) request.getAttribute("districts");
                        String selectedDistrict = request.getParameter("district");
                        if(districts != null) {
                            for(Map<String, Object> district : districts) {
                                String selected = district.get("id").toString().equals(selectedDistrict) ? "selected" : "";
                    %>
                    <option value="<%= district.get("id") %>" <%= selected %>><%= district.get("name") %></option>
                    <%
                            }
                        }
                    %>
                </select>

                <!-- 게임 검색 -->
                <input type="search" 
                       id="gameSearch" 
                       class="filter-select" 
                       placeholder="게임 검색..."
                       value="<%= request.getParameter("game") != null ? request.getParameter("game") : "" %>">
            </div>
        </div>

        <!-- 검색 결과 영역 -->
        <div class="search-results">
            <%
                List<Member> searchResults = (List<Member>) request.getAttribute("searchResults");
                Member loggedInMember = (Member) session.getAttribute("loggedInMember");

                if(searchResults != null && !searchResults.isEmpty()) {
                    for(Member member : searchResults) {
                        // 자기 자신은 건너뛰기
                        if (member.getId() == loggedInMember.getId()) {
                            continue;
                        }
            %>
                <!-- 사용자 카드 -->
                <div class="user-card">
                    <img src="<%= member.getProfileImageUrl() != null ? member.getProfileImageUrl() : "https://via.placeholder.com/100" %>" 
                         alt="Profile" 
                         class="profile-image">
                    <div class="user-info">
                        <div class="username"><%= member.getNickname() %></div>
                    </div>
                    <button onclick="showUserDetailModal('<%= member.getId() %>')" class="detail-button">
    상세보기
</button>

                </div>
            <%
                    } // end for
                } else if (searchResults != null) {
            %>
                <div class="empty-results">
                    검색 결과가 없습니다.
                </div>
            <%
                } // end if
            %>
        </div>
    </main>
    
    <%@ include file="/WEB-INF/views/PSWJSP/footer.jsp"  %>

    <!-- ====================== 모달은 반복문 밖, 단 한 번만 선언 ====================== -->
    <div id="userDetailModal" class="modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header">
                <h3>사용자 정보</h3>
                <span class="close-modal" onclick="closeUserDetailModal()">&times;</span>
            </div>
            <div class="modal-body">
                <div class="user-profile">
                    <img id="modalProfileImage" src="" alt="Profile" class="modal-profile-image">
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
                    <div id="modalTags" class="tags-container flex flex-wrap gap-2"></div>
                </div>

                <div class="section">
                    <h5 class="text-md font-semibold mb-2">활동 지역</h5>
                    <div id="modalRegions" class="regions-container flex flex-wrap gap-2"></div>
                </div>

                <div class="section">
                    <h5 class="text-md font-semibold mb-2">관심 게임</h5>
                    <div id="modalGames" class="games-container flex flex-wrap gap-2"></div>
                </div>

                <div class="modal-actions mt-4 text-center">
                    <button id="friendRequestBtn" class="friend-request-btn"></button>
                </div>
            </div>
        </div>
    </div>

    <!-- ====================== 스크립트 영역 ====================== -->
    <script>
    // DOMContentLoaded 이벤트
    document.addEventListener('DOMContentLoaded', function() {
        // 선택된 지역 셋팅
        const selectedRegion = document.getElementById('regionSelect').value;
        if (selectedRegion) {
            loadDistricts(selectedRegion);
        }

        // 게임 검색에서 Enter 키 이벤트
        document.getElementById('gameSearch').addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                searchUsers();
            }
        });

        // 클릭 시 모달 닫기 (바깥 영역)
        const modal = document.getElementById('userDetailModal');
        window.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeUserDetailModal();
            }
        });
    });

    // 시/도 선택 시 시/군/구 목록 로드
    // loadDistricts 함수 수정
function loadDistricts(regionId) {
    const districtSelect = document.getElementById('districtSelect');
    const currentSelectedDistrict = new URLSearchParams(window.location.search).get('district'); // 현재 선택된 district 값 가져오기
    
    if (!regionId) {
        districtSelect.innerHTML = '<option value="">시/군/구 선택</option>';
        districtSelect.disabled = true;
        return;
    }
    
    fetch(contextPath + '/api/regions/districts/' + regionId)
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            districtSelect.innerHTML = '<option value="">시/군/구 선택</option>';
            
            data.forEach(district => {
                const option = document.createElement('option');
                option.value = district.id;
                option.textContent = district.name;
                // 현재 URL의 district 파라미터와 일치하면 selected 속성 추가
                if (currentSelectedDistrict && district.id.toString() === currentSelectedDistrict) {
                    option.selected = true;
                }
                districtSelect.appendChild(option);
            });
            
            districtSelect.disabled = false;
        })
        .catch(error => {
            console.error('District loading error:', error);
            districtSelect.innerHTML = '<option value="">시/군/구 선택</option>';
            districtSelect.disabled = true;
        });
}

    // 시/도 선택 이벤트
    document.getElementById('regionSelect').addEventListener('change', function() {
        const regionId = this.value;
        const districtSelect = document.getElementById('districtSelect');
        
        if (regionId) {
            loadDistricts(regionId);
            window.location.href = contextPath + '/friends?region=' + regionId;
        } else {
            districtSelect.innerHTML = '<option value="">시/군/구 선택</option>';
            districtSelect.disabled = true;
            window.location.href = contextPath + '/friends';
        }
    });

    // 시/군/구 선택 시 즉시 검색
    document.getElementById('districtSelect').addEventListener('change', function() {
        const regionId = document.getElementById('regionSelect').value;
        const districtId = this.value;
        
        if (districtId && regionId) {
            window.location.href = contextPath + '/friends?region=' + regionId + '&district=' + districtId;
        }
    });

    // 검색(시/도, 시/군/구, 게임)
    function searchUsers() {
        const regionId = document.getElementById('regionSelect').value;
        const districtId = document.getElementById('districtSelect').value;
        const gameSearch = document.getElementById('gameSearch').value.trim();
        
        let url = contextPath + '/friends?';
        if (regionId) {
            url += 'region=' + regionId;
        }
        if (districtId) {
            url += (url.endsWith('?') ? '' : '&') + 'district=' + districtId;
        }
        if (gameSearch) {
            url += (url.endsWith('?') ? '' : '&') + 'game=' + encodeURIComponent(gameSearch);
        }
        
        window.location.href = url;
    }

    // 모달 표시 함수
    let currentUserId = null;

    async function showUserDetailModal(userId) {
    	console.log('1) showUserDetailModal 호출됨, userId=', userId);
        try {
            console.log('Fetching user details for:', userId);
            currentUserId = userId;

            // 기존 모달 오버레이 제거(있다면)
            let modalOverlay = document.querySelector('.modal-overlay');
            if (modalOverlay) {
                modalOverlay.remove();
            }
            
            // 새 모달 오버레이 생성
            modalOverlay = document.createElement('div');
            modalOverlay.className = 'modal-overlay';
            document.body.appendChild(modalOverlay);

            // 서버에서 사용자 정보 가져오기
            const response = await fetch(contextPath + '/friends/details/' + userId, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('Response error:', errorText);
                throw new Error('HTTP error! status: ' + response.status);
            }

            // JSON 데이터 파싱
            const userData = await response.json();
            console.log('Complete User Data:', JSON.stringify(userData, null, 2));
            console.log(userData.averageRating, userData.totalReviews, userData.tags, userData.regions, userData.games);
            console.log('2) fetch 완료, userData=', userData);

            // 모달 DOM 요소들 가져오기
            const modal = document.getElementById('userDetailModal');
            console.log('3) modal=', modal);
            const modalProfileImage = document.getElementById('modalProfileImage');
            console.log('4) modalProfileImage=', modalProfileImage);

            const modalNickname = document.getElementById('modalNickname');
            const modalRating = document.getElementById('modalRating');
            const modalReviewCount = document.getElementById('modalReviewCount');
            const tagsContainer = document.getElementById('modalTags');
            const regionsContainer = document.getElementById('modalRegions');
            const gamesContainer = document.getElementById('modalGames');
            const friendRequestBtn = document.getElementById('friendRequestBtn');
            
            console.log('modalNickname=', modalNickname);
            console.log('modalRating=', modalRating);
            console.log('modalReviewCount=', modalReviewCount);
            console.log('tagsContainer=', tagsContainer);
            console.log('regionsContainer=', regionsContainer);
            console.log('gamesContainer=', gamesContainer);

            // 모달 내용 채우기
            modalProfileImage.src = userData.profileImageUrl || 'https://via.placeholder.com/100';
            modalNickname.textContent = userData.nickname;
            modalRating.innerHTML = `⭐ \${userData.averageRating.toFixed(1)}`;
            modalReviewCount.innerHTML = `(\${userData.totalReviews}개의 리뷰)`;
            
            if (userData.tags && userData.tags.length > 0) {
            	  userData.tags.forEach(tag => {
            	    console.log('tag.name:', tag.name);
            	  });
            	} else {
            	  console.log('태그가 없습니다.');
            	}


            // 태그
            tagsContainer.innerHTML = 
                userData.tags && userData.tags.length > 0
                ? userData.tags.map(tag => 
                    `<div class="flex items-center bg-gray-100 text-gray-800 rounded-full py-1 px-3 mr-2 mb-2">
                        <span class="text-sm font-medium">\${tag.name}</span>
                        <span class="ml-2 bg-gray-700 text-white text-xs rounded-full px-2 py-0.5">\${tag.count}</span>
                    </div>`).join('')
                : '<span class="text-gray-500">받은 태그가 없습니다</span>';

            // 활동 지역
            regionsContainer.innerHTML = 
                userData.regions && userData.regions.length > 0
                ? userData.regions.map(region => 
                    `<span class="bg-blue-100 text-blue-800 text-sm rounded px-2 py-1 mr-2 mb-2">
                        \${region}
                    </span>`).join('')
                : '<span class="text-gray-500">등록된 활동 지역이 없습니다</span>';

            // 관심 게임
            gamesContainer.innerHTML = 
                userData.games && userData.games.length > 0
                ? userData.games.map(game => 
                    `<span class="bg-green-100 text-green-800 text-sm rounded px-2 py-1 mr-2 mb-2">
                        \${game}
                    </span>`).join('')
                : '<span class="text-gray-500">관심 게임이 없습니다</span>';

            // 친구 상태 처리
            friendRequestBtn.className = 'friend-request-btn px-4 py-2 rounded text-white font-medium';
            friendRequestBtn.disabled = false;
            friendRequestBtn.onclick = null; // 이전 이벤트 제거

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

            // 모달 표시
            modal.style.display = 'block';
            console.log('6) set modal.style.display=', modal.style.display);

        } catch (error) {
            console.error('Error loading user details:', error);
            alert('사용자 정보를 불러오는데 실패했습니다.');
        }
    }

    // 모달 닫기
    function closeUserDetailModal() {
        const modal = document.getElementById('userDetailModal');
        const modalOverlay = document.querySelector('.modal-overlay');
        
        if (modal) {
            modal.style.display = 'none';
        }
        if (modalOverlay) {
            modalOverlay.remove();
        }
        currentUserId = null;
    }

    // 친구 요청 전송
   async function sendFriendRequest(receiverId) {
    console.log('In sendFriendRequest, contextPath:', window.contextPath);
    let requestUrl = window.contextPath + '/friends/send-request';
    console.log('Request URL:', requestUrl);

    if (!confirm('친구 요청을 보내시겠습니까?')) {
        return;
    }

    try {
        // URLSearchParams를 사용하여 파라미터 전송
        const params = new URLSearchParams();
        params.append('receiverId', receiverId);

        const response = await fetch(requestUrl, {
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


    </script>
</body>
</html>
