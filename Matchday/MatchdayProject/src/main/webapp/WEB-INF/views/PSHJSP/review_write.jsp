<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="PSH.model.Member" %>
<%@ page import="PSH.model.Tag" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>리뷰 작성 - Matchday</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <style>
        .rating-group {
            display: flex;
            flex-direction: row-reverse;
            justify-content: flex-end;
        }
        
        .rating-group input {
            display: none;
        }
        
        .rating-group label {
            cursor: pointer;
            font-size: 2rem;
            color: #ddd;
            padding: 0 0.2em;
        }
        
        .rating-group label:hover,
        .rating-group label:hover ~ label,
        .rating-group input:checked ~ label {
            color: #FFD700;
        }
        
        .tag-checkbox {
            display: none;
        }
        
        .tag-label {
            display: inline-block;
            padding: 0.5rem 1rem;
            margin: 0.25rem;
            border: 2px solid #000;
            border-radius: 9999px;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .tag-checkbox:checked + .tag-label {
            background-color: #000;
            color: #fff;
        }
    </style>
</head>
<body class="bg-gray-100">
    <div class="container mx-auto px-4 py-8">
        <div class="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-8">
            <h1 class="text-2xl font-bold mb-6">리뷰 작성</h1>
            
            <%
            List<Member> reviewableMembers = (List<Member>) request.getAttribute("reviewableMembers");
            List<Tag> tags = (List<Tag>) request.getAttribute("tags");
            int meetingId = (Integer) request.getAttribute("meetingId");
            %>
            
            <form id="reviewForm" class="space-y-6">
                <input type="hidden" name="meetingId" value="<%= meetingId %>">
                
                <!-- 리뷰 대상자 선택 -->
                <div>
                    <label for="toUserId" class="block text-sm font-medium text-gray-700 mb-2">리뷰 대상</label>
                    <select id="toUserId" name="toUserId" class="w-full p-2 border border-gray-300 rounded-md shadow-sm" required>
                        <option value="">리뷰할 참가자를 선택하세요</option>
                        <% for (Member member : reviewableMembers) { %>
                            <option value="<%= member.getId() %>"><%= member.getNickname() %></option>
                        <% } %>
                    </select>
                </div>

                <!-- 별점 선택 -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">별점</label>
                    <div class="rating-group">
                        <% for (int i = 5; i >= 1; i--) { %>
                            <input type="radio" id="rating<%= i %>" name="rating" value="<%= i %>" required>
                            <label for="rating<%= i %>">★</label>
                        <% } %>
                    </div>
                </div>

                <!-- 태그 선택 -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">태그 선택</label>
                    <div class="flex flex-wrap gap-2">
                        <% for (Tag tag : tags) { %>
                            <div>
                                <input type="checkbox" 
                                       id="tag<%= tag.getTagId() %>" 
                                       name="tagIds" 
                                       value="<%= tag.getTagId() %>" 
                                       class="tag-checkbox">
                                <label for="tag<%= tag.getTagId() %>" 
                                       class="tag-label">
                                    <%= tag.getTagName() %>
                                </label>
                            </div>
                        <% } %>
                    </div>
                </div>

                <!-- 제출 버튼 -->
                <div class="flex justify-end">
                    <button type="submit" 
                            class="bg-black text-white px-6 py-2 rounded-lg hover:bg-gray-800 transition-colors">
                        리뷰 작성 완료
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        document.getElementById('reviewForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            // 선택된 태그들 가져오기
            const selectedTags = Array.from(document.querySelectorAll('input[name="tagIds"]:checked'))
                                    .map(input => input.value);
            
            // 폼 데이터 수집
            const formData = new URLSearchParams();
            formData.append('meetingId', document.querySelector('input[name="meetingId"]').value);
            formData.append('toUserId', document.querySelector('select[name="toUserId"]').value);
            formData.append('rating', document.querySelector('input[name="rating"]:checked').value);
            selectedTags.forEach(tagId => formData.append('tagIds', tagId));

            // 서버에 데이터 전송
            fetch('${pageContext.request.contextPath}/reviews', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData.toString()
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    window.location.href = '${pageContext.request.contextPath}/meeting/detail?meetingId=' + 
                                         document.querySelector('input[name="meetingId"]').value;
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('리뷰 저장 중 오류가 발생했습니다.');
            });
        });
    </script>
</body>
</html>