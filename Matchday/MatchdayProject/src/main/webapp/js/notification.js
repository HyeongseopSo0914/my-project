// 알림 시스템 초기화
document.addEventListener('DOMContentLoaded', function() {
    initNotificationSystem();
});

// 알림 시스템 초기화 함수
function initNotificationSystem() {
    // 데스크톱 알림 요소
    const notificationButton = document.getElementById('notificationButton');
    const notificationDropdown = document.getElementById('notificationDropdown');
    
    // 모바일 알림 요소
    const mobileNotificationButton = document.getElementById('mobile-notification-button');
    const mobileNotificationDropdown = document.getElementById('mobile-notification-dropdown');

    // 데스크톱 알림 버튼 클릭 이벤트
    if (notificationButton && notificationDropdown) {
        notificationButton.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleDesktopNotifications();
        });
    }

    // 모바일 알림 버튼 클릭 이벤트
    if (mobileNotificationButton && mobileNotificationDropdown) {
        mobileNotificationButton.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleMobileNotifications();
        });
    }

    // 문서 클릭시 드롭다운 닫기
    document.addEventListener('click', function(e) {
        closeNotificationsOnClickOutside(e);
    });

    // 초기 알림 확인 및 주기적 갱신
    checkUnreadNotifications();
    setInterval(checkUnreadNotifications, 30000); // 30초마다 갱신
}

// 데스크톱 알림 토글
function toggleDesktopNotifications() {
    const dropdown = document.getElementById('notificationDropdown');
    if (!dropdown) return;

    const isHidden = dropdown.classList.contains('hidden');
    
    if (isHidden) {
        loadNotifications('desktop');
        dropdown.classList.remove('hidden');
        // 모바일 알림 닫기
        const mobileDropdown = document.getElementById('mobile-notification-dropdown');
        if (mobileDropdown) {
            // 모바일은 .show 클래스로 제어하므로 remove만
            mobileDropdown.classList.remove('show');
        }
    } else {
        dropdown.classList.add('hidden');
    }
}

// 모바일 알림 토글
function toggleMobileNotifications() {
    const dropdown = document.getElementById('mobile-notification-dropdown');
    if (!dropdown) return;

    if (dropdown.classList.contains('show')) {
        dropdown.classList.remove('show');
    } else {
        loadNotifications('mobile');
        dropdown.classList.add('show');
        // 데스크톱 알림 닫기
        const desktopDropdown = document.getElementById('notificationDropdown');
        if (desktopDropdown) {
            desktopDropdown.classList.add('hidden');
        }
    }
}

// 문서 클릭 시 드롭다운 닫기
function closeNotificationsOnClickOutside(e) {
    const desktopButton = document.getElementById('notificationButton');
    const desktopDropdown = document.getElementById('notificationDropdown');
    const mobileButton = document.getElementById('mobile-notification-button');
    const mobileDropdown = document.getElementById('mobile-notification-dropdown');

    // 데스크톱 알림 닫기
    if (desktopDropdown && desktopButton && 
        !desktopDropdown.contains(e.target) && 
        !desktopButton.contains(e.target)) {
        desktopDropdown.classList.add('hidden');
    }

    // 모바일 알림 닫기
    if (mobileDropdown && mobileButton && 
        !mobileDropdown.contains(e.target) && 
        !mobileButton.contains(e.target)) {
        mobileDropdown.classList.remove('show');
    }
}

// 알림 목록 로드
function loadNotifications(type) {
    const listId = type === 'desktop' ? 'notificationList' : 'mobile-notificationList';
    
    fetch(`${contextPath}/api/notifications`)
        .then(response => response.json())
        .then(notifications => {
            renderNotificationList(listId, notifications);
        })
        .catch(error => {
            console.error('알림 로드 중 오류 발생:', error);
            showErrorMessage(listId);
        });
}

// 알림 목록 렌더링
function renderNotificationList(elementId, notifications) {
    const notificationList = document.getElementById(elementId);
    if (!notificationList) return;

    if (notifications.length === 0) {
        notificationList.innerHTML = '<div class="p-4 text-center text-gray-500">새로운 알림이 없습니다.</div>';
        return;
    }

    const notificationElements = notifications.map(notification => `
        <div class="notification-item ${notification.isRead ? 'bg-gray-50' : 'bg-white'} p-4 border-b border-gray-200">
            <div class="flex justify-between items-start">
                <div class="flex-1">
                    <p class="text-sm ${notification.isRead ? 'text-gray-500' : 'text-gray-900'}">${notification.content}</p>
                    <p class="text-xs text-gray-500 mt-1">${formatDate(notification.createdAt)}</p>
                </div>
                ${!notification.isRead ? `
                    <button onclick="markAsRead(${notification.id}, '${elementId}')" 
                            class="text-xs text-blue-600 hover:text-blue-800 ml-2">
                        읽음
                    </button>
                ` : ''}
            </div>
        </div>
    `).join('');

    notificationList.innerHTML = `
        <div class="flex justify-between items-center p-4 border-b border-gray-200">
            <h3 class="font-semibold text-gray-900">알림</h3>
            ${notifications.some(n => !n.isRead) ? `
                <button onclick="markAllAsRead('${elementId}')" 
                        class="text-sm text-blue-600 hover:text-blue-800">
                    모두 읽음
                </button>
            ` : ''}
        </div>
        ${notificationElements}
    `;
}

// 읽지 않은 알림 개수 확인
function checkUnreadNotifications() {
    fetch(`${contextPath}/api/notifications/unread-count`)
        .then(response => response.json())
        .then(data => {
            updateUnreadCount('unreadCount', data.count);
            updateUnreadCount('mobile-unreadCount', data.count);
        })
        .catch(error => console.error('알림 개수 확인 중 오류 발생:', error));
}

// 알림 개수 업데이트
function updateUnreadCount(elementId, count) {
    const unreadCount = document.getElementById(elementId);
    if (unreadCount) {
        unreadCount.textContent = count;
        unreadCount.style.display = count > 0 ? 'flex' : 'none';
    }
}

// 단일 알림 읽음 처리
function markAsRead(notificationId, listElementId) {
    fetch(`${contextPath}/api/notifications/mark-read`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `notificationId=${notificationId}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const type = listElementId === 'notificationList' ? 'desktop' : 'mobile';
            loadNotifications(type);
            checkUnreadNotifications();
        }
    })
    .catch(error => console.error('알림 읽음 처리 중 오류 발생:', error));
}

// 모든 알림 읽음 처리
function markAllAsRead(listElementId) {
    fetch(`${contextPath}/api/notifications/mark-all-read`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const type = listElementId === 'notificationList' ? 'desktop' : 'mobile';
            loadNotifications(type);
            checkUnreadNotifications();
        }
    })
    .catch(error => console.error('전체 알림 읽음 처리 중 오류 발생:', error));
}

// 에러 메시지 표시
function showErrorMessage(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '<div class="p-4 text-center text-red-500">알림을 불러오는데 실패했습니다.</div>';
    }
}

// 날짜 포맷팅
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    
    if (diff < 60000) return '방금 전';
    if (diff < 3600000) return `${Math.floor(diff / 60000)}분 전`;
    if (diff < 86400000) return `${Math.floor(diff / 3600000)}시간 전`;
    if (diff < 604800000) return `${Math.floor(diff / 86400000)}일 전`;
    
    return new Intl.DateTimeFormat('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }).format(date);
}
