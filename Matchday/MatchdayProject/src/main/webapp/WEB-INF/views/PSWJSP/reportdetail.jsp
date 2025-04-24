<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="PSW.model.Report" %>
<%@ page import="util.DBUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/PSWCSS/board.css">
</head>
<style>
.bulletin-detail {
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 부드러운 그림자 효과 */
    padding: 20px;
    margin: 20px auto;
    width: 80%;
    max-width: 800px;
    text-align: center; /* 내용 가운데 정렬 */
}

.bulletin-detail h2, .bulletin-detail p {
    color: #000; /* 글자 색을 검은색으로 설정 */
    font-family: 'Arial', sans-serif;
    margin-bottom: 20px;
}

.bulletin-content p {
    color: #333; /* 본문 내용 색상 */
    font-size: 1.1rem;
    line-height: 1.6;
}

/* 버튼 컨테이너 */
.button-container {
    display: flex;
    justify-content: center;
    gap: 20px; /* 버튼 사이 간격 */
    margin-top: 20px;
}

/* 추천 버튼 */
.btn-recommend {
    background-color: #4CAF50; /* 녹색 배경 */
    color: white;
    border: none;
    border-radius: 5px;
    padding: 10px 20px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.btn-recommend:hover {
    background-color: #45a049; /* 마우스 오버 시 색상 변경 */
}

/* 뒤로가기 버튼 */
.btn-back {
    background-color: #f1f1f1; /* 연한 회색 배경 */
    color: #000;
    border: 1px solid #ccc;
    border-radius: 5px;
    padding: 10px 20px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease, border-color 0.3s ease;
}

.btn-back:hover {
    background-color: #ddd; /* 마우스 오버 시 색상 변경 */
    border-color: #bbb;
}
.w-btn {
    position: relative;
    border: none;
    display: inline-block;
    padding: 15px 30px;
    border-radius: 15px;
    font-family: "paybooc-Light", sans-serif;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
    text-decoration: none;
    font-weight: 600;
    transition: 0.25s;
}
.w-btn-indigo {
    background-color: black;
    color: white;
}
</style>
<body>

    <%-- ✅ nav.jsp 포함 --%>
    <jsp:include page="nav.jsp" />

    <main class="faq-container">
        <section class="faq-content">
            <%
                // bulletinId를 URL 파라미터로 받아오기
                String ReportIdStr = request.getParameter("reportId");
                if (ReportIdStr != null) {
                    try {
                        int reportId = Integer.parseInt(ReportIdStr); // ReportIdStr을 int로 변환

                        // DB에서 해당 게시글 정보 조회
                        String sql = "SELECT bb.report_id, bb.report_title, bb.report_content, m.nickname, bb.report_regdate " +
                                     "FROM report_board bb " +
                                     "JOIN members m ON bb.report_writer_id = m.id " +
                                     "WHERE bb.report_id = ?";
                        Report report = null;

                        try (Connection conn = DBUtil.getConnection();
                             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setInt(1, reportId);  // SQL 쿼리에 reportId를 바인딩

                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    report = new Report();
                                    report.setId(rs.getInt("report_id"));
                                    report.setTitle(rs.getString("report_title"));
                                    report.setContent(rs.getString("report_content"));
                                    report.setNickname(rs.getString("nickname"));
                                    report.setRegdate(rs.getTimestamp("report_regdate"));
                                }
                            }
                        }

                        if (report != null) {
            %>
            <div class="bulletin-detail">
                <h1>신고글 상세</h1>
                <br>
                <h2><%= report.getTitle() %></h2>
                <p>작성자: <%= report.getNickname() %> | 등록일: <%= report.getRegdate() %></p>
                <div class="bulletin-content">
                    <p><%= report.getContent() %></p>
                </div>
                <div class="button-container">
                    <%-- 뒤로가기 버튼 --%>
                    <button class="w-btn w-btn-indigo" onclick="history.back()">뒤로가기</button>
                </div>
            </div>
            <%
                        } else {
            %>
            <p>해당 게시글을 찾을 수 없습니다.</p>
            <%
                        }
                    } catch (NumberFormatException e) {
            %>
            <p>잘못된 요청입니다. 숫자 형식이 아닙니다.</p>
            <%
                    } catch (SQLException e) {
            %>
            <p>데이터베이스 오류가 발생했습니다. 관리자에게 문의하세요.</p>
            <%
                    }
                } else {
            %>
            <p>게시글 ID가 없습니다. URL을 확인하세요.</p>
            <%
                }
            %>
        </section>
    </main>

    <%-- ✅ footer.jsp 포함 --%>
    <jsp:include page="footer.jsp" />

</body>
</html>
