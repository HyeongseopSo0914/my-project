<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="PSH.model.UserReviewSummary" %>
<%@ page import="PSH.model.Review" %>
<%@ page import="PSH.model.Tag" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>내 리뷰 요약 - Matchday</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
    <style>
        .star-rating {
            color: #FFD700;
        }
        
        .tag-badge {
            display: inline-block;
            padding: 0.5rem 1rem;
            margin: 0.25rem;
            background-color: #f3f4f6;
            border-radius: 9999px;
            font-size: 0.875rem;
        }
        
        .tag-count {
            display: inline-block;
            padding: 0.25rem 0.5rem;
            margin-left: 0.5rem;
            background-color: #000;
            color: #fff;
            border-radius: 9999px;
            font-size: 0.75rem;
        }
    </style>
</head>
<body class="bg-gray-100">
    <%
    UserReviewSummary summary = (UserReviewSummary) request.getAttribute("summary");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    %>

    <div class="container mx-auto px-4 py-8">
        <div class="max-w-4xl mx-auto">
            <!-- 평균 평점 섹션 -->
            <div class="bg-white rounded-lg shadow-lg p-8 mb-8">
                <h2 class="text-2xl font-bold mb-4">평균 평점</h2>
                <div class="flex items-center space-x-4">
                    <div class="text-4xl font-bold"><%= String.format("%.1f", summary.getAverageRating()) %></div>
                    <div class="star-rating text-3xl">
                        <%
                        double rating = summary.getAverageRating();
                        for (int i = 1; i <= 5; i++) {
                            if (i <= rating) {
                                out.print("★");
                            } else if (i - 0.5 <= rating) {
                                out.print("★");
                            } else {
                                out.print("☆");
                            }
                        }
                        %>
                    </div>
                    <div class="text-gray-500">(<%= summary.getTotalReviews() %>개의 리뷰)</div>
                </div>
            </div>

            <!-- 받은 태그 섹션 -->
            <div class="bg-white rounded-lg shadow-lg p-8 mb-8">
                <h2 class="text-2xl font-bold mb-4">받은 태그</h2>
                <div class="flex flex-wrap">
                    <% for (Tag tag : summary.getTopTags()) { %>
                        <div class="tag-badge">
                            <%= tag.getTagName() %>
                            <span class="tag-count"><%= tag.getCount() %></span>
                        </div>
                    <% } %>
                </div>
            </div>

            <!-- 최근 리뷰 섹션 -->
            <div class="bg-white rounded-lg shadow-lg p-8">
                <h2 class="text-2xl font-bold mb-4">최근 리뷰</h2>
                <div class="space-y-6">
                    <% for (Review review : summary.getRecentReviews()) { %>
                        <div class="border-b border-gray-200 pb-4 last:border-b-0 last:pb-0">
                            <div class="flex justify-between items-start">
                                <div>
                                    <div class="font-medium"><%= review.getFromUserNickname() %>님의 리뷰</div>
                                    <div class="text-sm text-gray-500">
                                        <%= dateFormat.format(review.getCreatedAt()) %>
                                    </div>
                                </div>
                                <div class="star-rating">
                                    <%
                                    for (int i = 0; i < 5; i++) {
                                        out.print(i < review.getRating() ? "★" : "☆");
                                    }
                                    %>
                                </div>
                            </div>
                            <% if (review.getTags() != null && !review.getTags().isEmpty()) { %>
                                <div class="mt-2">
                                    <% for (Tag tag : review.getTags()) { %>
                                        <span class="inline-block bg-gray-100 rounded-full px-3 py-1 text-sm mr-2">
                                            <%= tag.getTagName() %>
                                        </span>
                                    <% } %>
                                </div>
                            <% } %>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</body>
</html>