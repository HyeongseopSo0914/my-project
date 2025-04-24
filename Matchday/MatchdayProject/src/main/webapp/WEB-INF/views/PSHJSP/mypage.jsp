<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="PSH.model.Member" %>
<%@ page import="PSH.model.BoardGame" %>
<%@ page import="PSH.model.Review" %>
<%@ page import="PSH.model.Tag" %>
<%@ page import="PSH.model.UserReviewSummary" %>
<%@ page import="PSH.model.Meeting" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - Matchday</title>
    
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">

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

        /* 네비게이션 스타일 */
        .navbar {
            position: fixed;
            width: 100%;
            top: 0;
            z-index: 10;
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        /* 메인 콘텐츠 영역 */
        main {
            padding-top: 80px;
            background-color: #f3f4f6;
        }

        /* 프로필 이미지 섹션 */
        .profile-image-section {
            position: relative;
            width: 128px;
            height: 128px;
            margin-bottom: 20px;
        }

        .profile-image-section img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            border-radius: 50%;
        }

        .profile-upload-button {
            position: absolute;
            bottom: 0;
            right: 0;
            background-color: black;
            color: white;
            padding: 8px;
            border-radius: 50%;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .profile-upload-button:hover {
            background-color: #374151;
        }

        /* 폼 입력 스타일 */
        .form-input {
            width: 100%;
            padding: 0.5rem 1rem;
            border: 1px solid #e2e8f0;
            border-radius: 0.375rem;
            transition: all 0.2s;
        }

        .form-input:focus {
            outline: none;
            border-color: black;
            box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
        }

        /* 게임 그리드 스타일 */
        .game-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 1rem;
            padding: 1rem 0;
        }

        .game-card {
            position: relative;
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 0.5rem;
            overflow: hidden;
            transition: transform 0.2s;
        }

        .game-card:hover {
            transform: translateY(-4px);
        }

        .game-img {
            position: relative;
            padding-top: 75%;
            overflow: hidden;
        }

        .game-img img {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .delete-btn {
            position: absolute;
            top: 0.5rem;
            right: 0.5rem;
            background: white;
            border-radius: 50%;
            padding: 0.5rem;
            opacity: 0;
            transition: opacity 0.2s;
        }

        .game-card:hover .delete-btn {
            opacity: 1;
        }

        .game-title {
            padding: 0.75rem;
            text-align: center;
            font-weight: 500;
            background: #f9fafb;
        }

        /* 태그 스타일 */
        .tag {
            display: inline-flex;
            align-items: center;
            background: #f3f4f6;
            padding: 0.5rem 1rem;
            border-radius: 9999px;
            font-size: 0.875rem;
            margin: 0.25rem;
        }

        .tag-count {
            background: black;
            color: white;
            padding: 0.25rem 0.5rem;
            border-radius: 9999px;
            margin-left: 0.5rem;
            font-size: 0.75rem;
        }

        /* 회원탈퇴 버튼 */
        .delete-account-btn {
        display: inline-block;
        font-size: 0.875rem;
        color: #ef4444;
        transition: color 0.2s;
        text-decoration: underline;
        margin-top: 0.5rem;
    }

    .delete-account-btn:hover {
        color: #dc2626;
    }

        /* 지역 선택 스타일 */
        .district-tag {
            display: inline-flex;
            align-items: center;
            background: #e5e7eb;
            padding: 0.375rem 0.75rem;
            border-radius: 9999px;
            margin: 0.25rem;
            font-size: 0.875rem;
        }

        .district-tag button {
            margin-left: 0.5rem;
            color: #ef4444;
            font-weight: bold;
        }

        /* 별점 스타일 */
        .star-rating {
            color: #fbbf24;
            font-size: 1.5rem;
        }
        
         /* 반응형 스타일 추가 */
        @media (max-width: 768px) {
            /* 메인 컨텐츠 패딩 조정 */
            main {
                padding-top: 60px;
                padding-bottom: 40px;
            }

            /* 컨테이너 패딩 조정 */
            .container-padding {
                padding-left: 1rem;
                padding-right: 1rem;
            }
            
            .profile-image-section {
            margin-bottom: 1rem;
        }

            /* 프로필 섹션 모바일 레이아웃 */
            .profile-section {
                flex-direction: column;
                gap: 1.5rem;
            }

            .profile-info {
                flex-direction: column;
                width: 100%;
            }

            .profile-column {
                width: 100%;
                margin-bottom: 1.5rem;
            }

            /* 활동 지역 선택 UI 조정 */
            .district-selector {
                flex-direction: column;
                gap: 0.5rem;
            }

            .district-selector select {
                width: 100%;
            }

            .district-selector button {
                width: 100%;
            }

            /* 게임 그리드 조정 */
            .game-grid {
                grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
                gap: 0.75rem;
            }

            /* 리뷰 정보 섹션 조정 */
            .review-section {
                padding: 1rem;
            }

            .rating-display {
                flex-direction: column;
                align-items: flex-start;
                gap: 0.5rem;
            }

            /* 태그 디스플레이 조정 */
            .tags-container {
                display: flex;
                flex-wrap: wrap;
                gap: 0.5rem;
            }

            .tag {
                font-size: 0.75rem;
                padding: 0.25rem 0.75rem;
            }
        }

        /* 태블릿 미디어 쿼리 추가 */
        @media (min-width: 769px) and (max-width: 1024px) {
            .game-grid {
                grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            }

            .container-padding {
                padding-left: 2rem;
                padding-right: 2rem;
            }
        }
        
    </style>

    <script>
        var contextPath = '<%= request.getContextPath() %>';
    </script>
</head>
<body class="bg-gray-100">
    <!-- Navigation -->
    <!-- Navigation -->
<jsp:include page="/WEB-INF/views/PSWJSP/nav.jsp" />

    <!-- Main Content -->
```html
<!-- Main Content -->
<main class="pt-24 pb-12">
    <div class="container mx-auto px-4">
        <div class="max-w-5xl mx-auto">
            <!-- ▼ 프로필 / 회원 기본 정보 섹션 ▼ -->
            <div class="bg-white rounded-lg shadow-lg p-4 md:p-6 mb-8">
                <div class="flex flex-col md:flex-row items-start space-y-6 md:space-y-0 md:space-x-8 profile-section">
                <%
                    Member member = (Member)request.getAttribute("member");
                    UserReviewSummary reviewSummary = (UserReviewSummary) request.getAttribute("reviewSummary");
                %>
                    <!-- 프로필 이미지 섹션 + 회원탈퇴 -->
                    <div class="w-full md:w-auto flex flex-col items-center">
                        <div class="profile-image-section mb-4">
                            <div class="relative">
                                <img src="<%= (member.getProfileImageUrl() == null || member.getProfileImageUrl().isEmpty()) ? 
                                    "https://res.cloudinary.com/dnjqljait/image/upload/v1736337453/icononly_cytjxl.png" : 
                                    member.getProfileImageUrl() %>"
                                     alt="프로필 이미지"
                                     class="w-32 h-32 rounded-full object-cover"
                                     id="profileImage">
                                <label for="profileImageInput" class="absolute bottom-0 right-0 bg-black text-white p-2 rounded-full cursor-pointer hover:bg-gray-800">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                        <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                                    </svg>
                                </label>
                                <input type="file"
                                       id="profileImageInput"
                                       accept="image/*"
                                       class="hidden"
                                       onchange="updateProfileImage(this)">
                            </div>
                        </div>
                        
                        <form action="delete_account" method="post" onsubmit="return confirm('정말로 회원을 탈퇴하시겠습니까? 모든 데이터가 삭제됩니다.');">
                            <button type="submit" class="text-sm text-red-500 hover:text-red-700 transition-colors">
                                회원탈퇴
                            </button>
                        </form>
                    </div>

                    <div class="flex flex-col md:flex-row gap-6 md:gap-8 w-full profile-info">
                        <!-- 왼쪽: 닉네임, 활동 지역 -->
                        <div class="w-full md:w-1/2 profile-column">
                            <div class="mb-6">
                                <label class="block text-sm font-semibold text-gray-700 mb-2">닉네임</label>
                                <input type="text" value="<%=member.getNickname()%>"
                                       class="form-input rounded-md border-gray-300" readonly>
                            </div>
                            <div class="space-y-4">
                                <label class="block text-sm font-semibold text-gray-700">활동 지역</label>
                                <div class="district-selector">
                                    <select id="regionSelect" class="form-input w-full md:w-1/3 mb-2 md:mb-0">
                                        <option value="">시/도 선택</option>
                                    </select>
                                    <select id="districtSelect" class="form-input w-full md:w-1/3 mb-2 md:mb-0" disabled>
                                        <option value="">시/군/구 선택</option>
                                    </select>
                                    <button id="addDistrictBtn" type="button"
                                            class="w-full md:w-auto px-4 py-2 bg-black text-white rounded hover:bg-gray-800 text-sm">
                                        추가
                                    </button>
                                </div>
                                <ul id="myDistricts" class="flex flex-wrap gap-2 pt-2">
                                    <!-- 동적으로 추가되는 지역 태그들 -->
                                </ul>
                            </div>
                        </div>

                        <!-- 오른쪽: 리뷰 정보 (평점/태그) -->
                        <div class="w-full md:w-1/2 profile-column">
                            <div class="bg-white rounded-lg p-4 md:p-6 review-section">
                                <%
                                    if (reviewSummary != null) {
                                %>
                                <div class="mb-8">
                                    <h3 class="text-lg font-semibold mb-4">평균 평점</h3>
                                    <div class="flex items-center gap-4">
                                        <span class="text-3xl font-bold"><%= String.format("%.1f", reviewSummary.getAverageRating()) %></span>
                                        <div class="flex text-2xl text-yellow-400">
                                            <%
                                                double avgRating = reviewSummary.getAverageRating();
                                                for (int i = 1; i <= 5; i++) {
                                                    if (i <= avgRating) {
                                                        out.print("★");
                                                    } else if (i - 0.5 <= avgRating) {
                                                        out.print("★");
                                                    } else {
                                                        out.print("☆");
                                                    }
                                                }
                                            %>
                                        </div>
                                        <span class="text-gray-500">(<%= reviewSummary.getTotalReviews() %>개의 리뷰)</span>
                                    </div>
                                </div>

                                <div>
                                    <h3 class="text-lg font-semibold mb-4">받은 태그</h3>
                                    <div class="flex flex-wrap gap-2">
                                        <% for (Tag tag : reviewSummary.getTopTags()) { %>
                                        <div class="inline-flex items-center bg-gray-100 rounded-full px-4 py-2">
                                            <span><%= tag.getTagName() %></span>
                                            <span class="ml-2 bg-black text-white rounded-full px-2 py-1 text-xs">
                                                <%= tag.getCount() %>
                                            </span>
                                        </div>
                                        <% } %>
                                    </div>
                                </div>
                                <%
                                    } else {
                                %>
                                <div class="text-center text-gray-500 py-8">
                                    아직 받은 리뷰가 없습니다.
                                </div>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ▼ 관심 게임 섹션 ▼ -->
            <div class="bg-white rounded-lg shadow-lg p-4 md:p-6">
                <div class="flex justify-between items-center mb-6 flex-wrap gap-2">
                    <h2 class="text-xl font-bold">관심 게임</h2>
                    <button onclick="openModal()" 
                            class="w-full md:w-auto py-2 px-4 bg-black text-white rounded hover:bg-gray-800 text-sm">
                        게임 추가
                    </button>
                </div>

                <!-- 카드들을 담을 컨테이너 -->
                <div class="game-grid">
                    <%
                        List<BoardGame> favoriteGames = (List<BoardGame>)request.getAttribute("favoriteGames");
                        if (favoriteGames != null) {
                            for (BoardGame game : favoriteGames) {
                    %>
                    <!-- 개별 카드 -->
                    <div class="game-card group" data-game-id="<%= game.getGameId() %>">
                        <div class="game-img">
                            <img src="<%= game.getImageUrl() %>" alt="<%= game.getName() %>">
                        </div>
                        <button class="delete-btn">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-red-500" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd"
                                      d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 
                                      0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 
                                      4.293 5.707a1 1 0 010-1.414z"
                                      clip-rule="evenodd"/>
                            </svg>
                        </button>
                        <p class="game-title"><%= game.getName() %></p>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
</main>
    
     <%@ include file="/WEB-INF/views/PSWJSP/footer.jsp"  %>

    <!-- ▼ 게임 추가 모달 include ▼ -->
    <%@ include file="add_games_modal.jsp" %>
    <!-- ▲ 모달 include 끝 ▲ -->

    <!-- ▼ 스크립트 영역 ▼ -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            loadRegions();
            setupEventListeners();
            loadMemberDistricts(); // 이미 등록된 활동 지역 목록 불러오기
        });

        // 2) 광역시/도 목록 불러오기
        function loadRegions() {
            fetch(contextPath + '/api/regions') // GET /api/regions
                .then(res => {
                    if (!res.ok) throw new Error('Failed to fetch regions');
                    return res.json();
                })
                .then(data => {
                    const regionSelect = document.getElementById('regionSelect');
                    data.forEach(region => {
                        const opt = document.createElement('option');
                        opt.value = region.id;
                        opt.textContent = region.name;
                        regionSelect.appendChild(opt);
                    });
                })
                .catch(err => console.error(err));
        }

        // 3) 시/군/구 목록 불러오기
        function loadDistricts(regionId) {
            if (!regionId) {
                return;
            }
            const districtSelect = document.getElementById('districtSelect');
            districtSelect.disabled = true;
            districtSelect.innerHTML = '<option value="">- 로딩중 -</option>';

            fetch(contextPath + '/api/regions/districts/' + regionId)
                .then(res => {
                    if (!res.ok) throw new Error('Failed to fetch districts');
                    return res.json();
                })
                .then(data => {
                    districtSelect.innerHTML = '<option value="">- 시/군/구 -</option>';
                    data.forEach(d => {
                        const opt = document.createElement('option');
                        opt.value = d.id;
                        opt.textContent = d.name;
                        districtSelect.appendChild(opt);
                    });
                    districtSelect.disabled = false;
                })
                .catch(err => {
                    console.error(err);
                    districtSelect.innerHTML = '<option value="">- 불러오기 실패 -</option>';
                });
        }

        // 4) 이벤트 리스너 (시/도 변경, 추가 버튼 클릭)
        function setupEventListeners() {
            const regionSelect = document.getElementById('regionSelect');
            const addDistrictBtn = document.getElementById('addDistrictBtn');

            regionSelect.addEventListener('change', (e) => {
                const regionId = e.target.value;
                loadDistricts(regionId);
            });

            addDistrictBtn.addEventListener('click', () => {
                const districtSelect = document.getElementById('districtSelect');
                const districtId = districtSelect.value;
                if (!districtId) {
                    alert('시/군/구를 선택하세요');
                    return;
                }

                // 활동 지역 추가 POST /api/regions
                fetch(contextPath + '/api/regions', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ districtId: parseInt(districtId) })
                })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        alert('활동 지역 추가 완료!');
                        loadMemberDistricts(); // 다시 목록 불러오기
                    } else {
                        alert('추가 실패: ' + data.message);
                    }
                })
                .catch(err => console.error(err));
            });
        }

        function loadMemberDistricts() {
            console.log("🔍 loadMemberDistricts() 실행됨");
            const myDistricts = document.getElementById('myDistricts');
            myDistricts.innerHTML = '';

            fetch(contextPath + '/api/regions/member')
            .then(res => res.json())
            .then(data => {
                console.log("📌 서버 응답 데이터 확인:", data);

                if (!Array.isArray(data) || data.length === 0) {
                    myDistricts.innerHTML = '<li>등록된 활동 지역이 없습니다.</li>';
                    return;
                }

                data.forEach(item => {
                    const li = document.createElement('li');
                    li.className = 'inline-flex items-center bg-gray-200 rounded-full px-3 py-1 text-sm font-semibold text-gray-700 mr-2 mb-2';

                    const regionText = document.createElement('span');
                    regionText.className = 'region-text mr-1';
                    const textNode = document.createTextNode(item.regionName + ' ' + item.districtName);
                    regionText.appendChild(textNode);
                    li.appendChild(regionText);

                    const deleteBtn = document.createElement('button');
                    deleteBtn.className = 'ml-2 text-red-500 hover:text-red-700';
                    deleteBtn.textContent = '×';
                    // 삭제 버튼 클릭 시
                    const districtId = item.districtId;
                    deleteBtn.addEventListener('click', function() {
                        removeDistrict(districtId);
                    });
                    
                    li.appendChild(deleteBtn);
                    myDistricts.appendChild(li);

                    console.log("생성된 li 텍스트:", li.textContent);
                });
            })
            .catch(err => {
                console.error("❌ 활동 지역 불러오기 실패:", err);
                myDistricts.innerHTML = '<li class="text-red-500">활동 지역 불러오기 실패</li>';
            });
        }

        // 활동 지역 삭제
        window.removeDistrict = function(districtId) {
            if (!confirm('정말 삭제하시겠습니까?')) {
                return;
            }
            fetch(contextPath + '/api/regions/' + districtId, {
                method: 'DELETE'
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert('삭제되었습니다.');
                    loadMemberDistricts();
                } else {
                    alert('삭제 실패: ' + data.message);
                }
            })
            .catch(err => console.error(err));
        };

        // 게임 관리 객체
        const gameManager = {
            init: function() {
                document.addEventListener("click", (e) => {
                    if (e.target.closest(".delete-btn")) {
                        this.handleDelete(e);
                    }
                });
            },
            handleDelete: function(e) {
                const button = e.target.closest(".delete-btn");
                const gameCard = button.closest(".game-card");
                const gameId = gameCard?.getAttribute("data-game-id");

                if (!gameId) {
                    console.error('❌ Game ID not found');
                    return;
                }

                console.log(`🛠️ 삭제 요청: gameId=${gameId}`);

                fetch(contextPath + "/mypage", {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: "action=removeGame&gameId=" + encodeURIComponent(gameId)
                })
                .then(response => response.json())
                .then(data => {
                    console.log("✅ 서버 응답:", data);
                    if (data.success) {
                        gameCard.remove();
                        console.log("🗑️ 게임 삭제 완료");
                    } else {
                        alert(data.message);
                    }
                })
                .catch(error => console.error("❌ 게임 삭제 실패:", error));
            }
        };
        
        

        // 초기화
        document.addEventListener("DOMContentLoaded", function() {
            console.log("🚀 Initializing gameManager...");
            gameManager.init();
        });

        // 모달 관련 함수들
        function openModal() {
    const modal = document.getElementById('addGamesModal');
    if (modal) {
        modal.classList.add('show');
        document.body.classList.add('modal-open');
    }
}

function closeModal() {
    const modal = document.getElementById('addGamesModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.classList.remove('modal-open');
    }
}

        // 프로필 이미지 업데이트
        function updateProfileImage(input) {
            if (input.files && input.files[0]) {
                const formData = new FormData();
                formData.append('profileImage', input.files[0]);

                fetch(contextPath + '/update-profile-image', {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        document.getElementById('profileImage').src = data.imageUrl;
                        alert(data.message);
                    } else {
                        alert(data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('프로필 이미지 업로드 중 오류가 발생했습니다.');
                });
            }
        }

        // 별점 관련 스타일 추가
        const style = document.createElement('style');
        style.textContent = `
            .star-label {
                float: right;
            }
            .star-label:hover,
            .star-label:hover ~ .star-label,
            input[name="rating"]:checked ~ .star-label {
                color: #FFD700;
            }
        `;
        document.head.appendChild(style);


    </script>
</body>
</html>
