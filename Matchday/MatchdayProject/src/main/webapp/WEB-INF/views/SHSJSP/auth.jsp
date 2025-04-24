<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%! 
    // DB에서 인증 코드가 유효한지 확인하는 메서드
    private boolean checkAuthCodeInDatabase(String authCode) {
        // 실제 DB 연결 로직 추가
        // 예시로 항상 true를 반환
        return true;
    }
%>

<%
    String authCode = request.getParameter("code");

    if (authCode != null) {
        // 인증 코드를 DB에서 조회하여 해당 이메일을 인증 처리
        boolean isValidCode = checkAuthCodeInDatabase(authCode);

        if (isValidCode) {
            out.println("인증이 완료되었습니다.");
        } else {
            out.println("유효하지 않은 인증 코드입니다.");
        }
    } else {
        out.println("인증 코드가 없습니다.");
    }
%>
