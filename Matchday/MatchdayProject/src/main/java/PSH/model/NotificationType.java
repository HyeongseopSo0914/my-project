package PSH.model;

public enum NotificationType {
    // 친구 요청 관련
    FRIEND_REQUEST("친구 요청을 받았습니다"),
    FRIEND_ACCEPTED("친구 요청이 수락되었습니다"),
    FRIEND_REJECTED("친구 요청이 거절되었습니다"),
    
    // 모임 관련 - 지역, 게임
    MEETING_CREATED_IN_REGION("내 활동 지역에 새로운 모임이 생성되었습니다"),
    MEETING_CREATED_FOR_GAME("관심 게임의 새로운 모임이 생성되었습니다"),
    FRIEND_NEW_MEETING("친구가 새로운 모임을 생성했습니다"),
    PARTICIPANT_CANCELLED("참여 중인 모임에서 참가자가 취소했습니다"),
    
    // 모임 상태 변경
    MEETING_CONFIRMED("참여 중인 모임이 확정되었습니다"),
    MEETING_UPDATED("참여 중인 모임 정보가 변경되었습니다"),
    MEETING_CANCELLED("참여 중인 모임이 취소되었습니다"),
    NEW_PARTICIPANT("새로운 참가자가 모임에 참여했습니다"),
 // 모임 종료 관련
    MEETING_COMPLETED("모임이 성공적으로 종료되었습니다"),
    MEETING_EXPIRED("모임이 기간 만료되었습니다"),
    MEETING_KICKED("모임에서 퇴장되었습니다"),
    // 리마인더
    MEETING_REMINDER("모임 시작 24시간 전입니다");

    private final String defaultMessage;

    NotificationType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}