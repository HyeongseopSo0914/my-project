<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 - Matchday</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/PSHCSS/signup.css?v=2">
<script>
var contextPath = '<%=request.getContextPath()%>';
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const passwordInput = document.querySelector('input[name="password"]');
    const confirmPasswordInput = document.querySelector('input[name="confirmPassword"]');
    
    // 비밀번호 입력칸 바로 밑에 요구사항 div 추가
    const requirementsList = document.createElement('div');
    requirementsList.style.fontSize = '12px';
    requirementsList.style.marginTop = '4px';
    requirementsList.style.color = '#666';
    requirementsList.innerHTML = `
        <div style="margin-bottom: 4px;">
            <span style="color: #dc2626;">✕</span> 8자 이상
        </div>
        <div style="margin-bottom: 4px;">
            <span style="color: #dc2626;">✕</span> 영문 대문자 포함
        </div>
        <div style="margin-bottom: 4px;">
            <span style="color: #dc2626;">✕</span> 영문 소문자 포함
        </div>
        <div style="margin-bottom: 4px;">
            <span style="color: #dc2626;">✕</span> 숫자 포함
        </div>
        <div style="margin-bottom: 4px;">
            <span style="color: #dc2626;">✕</span> 특수문자 포함
        </div>
    `;
    
    // 비밀번호 입력칸 다음에 요구사항 추가
    passwordInput.parentNode.insertBefore(requirementsList, passwordInput.nextSibling);
    
    // 비밀번호 입력할 때마다 검사
    passwordInput.addEventListener('input', function() {
        const password = this.value;
        const requirements = requirementsList.children;
        
        // 8자 이상 체크
        if(password.length >= 8) {
            requirements[0].innerHTML = '<span style="color: #059669;">✓</span> 8자 이상';
        } else {
            requirements[0].innerHTML = '<span style="color: #dc2626;">✕</span> 8자 이상';
        }
        
        // 대문자 포함 체크
        if(/[A-Z]/.test(password)) {
            requirements[1].innerHTML = '<span style="color: #059669;">✓</span> 영문 대문자 포함';
        } else {
            requirements[1].innerHTML = '<span style="color: #dc2626;">✕</span> 영문 대문자 포함';
        }
        
        // 소문자 포함 체크
        if(/[a-z]/.test(password)) {
            requirements[2].innerHTML = '<span style="color: #059669;">✓</span> 영문 소문자 포함';
        } else {
            requirements[2].innerHTML = '<span style="color: #dc2626;">✕</span> 영문 소문자 포함';
        }
        
        // 숫자 포함 체크
        if(/[0-9]/.test(password)) {
            requirements[3].innerHTML = '<span style="color: #059669;">✓</span> 숫자 포함';
        } else {
            requirements[3].innerHTML = '<span style="color: #dc2626;">✕</span> 숫자 포함';
        }
        
        // 특수문자 포함 체크
        if(/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
            requirements[4].innerHTML = '<span style="color: #059669;">✓</span> 특수문자 포함';
        } else {
            requirements[4].innerHTML = '<span style="color: #dc2626;">✕</span> 특수문자 포함';
        }
        
        // 입력창 테두리 색상 변경
        if(isPasswordValid(password)) {
            this.style.borderColor = '#059669';
        } else {
            this.style.borderColor = '#dc2626';
        }
    });
    
 // 비밀번호 확인 입력할 때마다 검사
    confirmPasswordInput.addEventListener('input', function() {
        // 메시지를 표시할 div 요소 찾기 또는 생성
        let feedbackDiv = document.getElementById('password-match-feedback');
        if (!feedbackDiv) {
            feedbackDiv = document.createElement('div');
            feedbackDiv.id = 'password-match-feedback';
            feedbackDiv.style.fontSize = '12px';
            feedbackDiv.style.marginTop = '4px';
            this.parentNode.appendChild(feedbackDiv);
        }

        if (this.value.length === 0) {
            // 입력값이 없을 때는 메시지와 테두리 스타일 제거
            feedbackDiv.textContent = '';
            this.style.borderColor = '';
        } else if (this.value === passwordInput.value) {
            // 비밀번호 일치할 때
            feedbackDiv.textContent = '비밀번호가 일치합니다.';
            feedbackDiv.style.color = '#059669';
            this.style.borderColor = '#059669';
        } else {
            // 비밀번호 불일치할 때
            feedbackDiv.textContent = '비밀번호가 일치하지 않습니다.';
            feedbackDiv.style.color = '#dc2626';
            this.style.borderColor = '#dc2626';
        }
    });
 
 // 이용약관 동의 이벤트 리스너 추가
    document.querySelector('input[name="termsAgreed"]').addEventListener('change', function(e) {
        formValidation.termsAgreed = e.target.checked;
        updateSubmitButton();
    });

    // 개인정보 수집·이용 동의 이벤트 리스너 추가
    document.querySelector('input[name="privacyAgreed"]').addEventListener('change', function(e) {
        formValidation.privacyAgreed = e.target.checked;
        updateSubmitButton();
    });
    // 폼 제출할 때 최종 검사
    // 폼 제출할 때 최종 검사
form.addEventListener('submit', function(e) {
    const email = document.getElementById('email').value;
    if (!email || !formValidation.email || !formValidation.emailVerified) {
        e.preventDefault();
        alert('이메일 인증이 필요합니다.');
        return;
    }

    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;
    
    if (!isPasswordValid(password)) {
        e.preventDefault();
        alert('비밀번호가 모든 요구사항을 충족하지 않습니다.');
        passwordInput.focus();
        return;
    }
    
    if (password !== confirmPassword) {
        e.preventDefault();
        alert('비밀번호가 일치하지 않습니다.');
        confirmPasswordInput.focus();
        return;
    }
});
    
    // 비밀번호 유효성 검사 함수
    function isPasswordValid(password) {
        return password.length >= 8 && 
               /[A-Z]/.test(password) && 
               /[a-z]/.test(password) && 
               /[0-9]/.test(password) && 
               /[!@#$%^&*(),.?":{}|<>]/.test(password);
    }
});
</script>
<style>
        /* 모달 스타일 */
/* 모달 기본 스타일: termsModal와 privacyModal 둘 다 적용 */
#termsModal,
#privacyModal {
    display: none;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
    background-color: rgba(0,0,0,0.5);
    opacity: 0;
    transition: opacity 0.3s ease;
    pointer-events: none;
}

#termsModal.show,
#privacyModal.show {
    display: flex;
    opacity: 1;
    pointer-events: auto;
    align-items: center;
    justify-content: center;
}

/* 모달 콘텐츠 스타일 */
.terms-modal-content {
    background-color: white;
    width: 90%;
    max-width: 800px;
    max-height: 80vh;
    border-radius: 8px;
    padding: 20px;
    position: relative;
    overflow-y: auto;
    transform: scale(0.9);
    opacity: 0;
    transition: all 0.3s ease;
}

/* 모달 show 상태일 때 콘텐츠 애니메이션 */
#termsModal.show .terms-modal-content,
#privacyModal.show .terms-modal-content {
    transform: scale(1);
    opacity: 1;
}

/* 기타 모달 내부 스타일 */
.terms-modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.close-modal {
    font-size: 24px;
    cursor: pointer;
    color: #666;
}

.terms-modal-body {
    max-height: 60vh;
    overflow-y: auto;
}

.terms-modal-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}

.close-button {
    padding: 8px 16px;
    background-color: #000;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

    </style>
</head>

<body>
    <div class="container">
        <div class="signup-wrapper">
            <!-- 왼쪽: 로고와 슬로건 -->
            <div class="left-side">
                <div class="logo">
        <a href="${pageContext.request.contextPath}/" style="display: flex; align-items: center; gap: 15px; text-decoration: none; color: inherit;">
            <img src="https://res.cloudinary.com/dnjqljait/image/upload/v1736337453/icononly_cytjxl.png" 
                 alt="Matchday Logo" class="logo-img">
            <div class="logo-text">
                <h1>MATCHDAY</h1>
                <p>GAMERS ON PLAY</p>
            </div>
        </a>
    </div>
                <div class="slogan">
                    <h2>보드게임 친구를<br>찾고 계신가요?</h2>
                    <p>회원가입하고 함께 게임할 친구들을 만나보세요.</p>
                    <div class="image-container">
                        <img src="https://res.cloudinary.com/dnjqljait/image/upload/v1736946347/resized_matchday_image_e5wgcg.png" 
                             alt="Gaming Character" 
                             class="slogan-image">
                    </div>
                </div>
            </div>

            <!-- 오른쪽: 회원가입 폼 -->
            <div class="right-side">
                <div class="form-container">
                    <h3>회원가입</h3>
                    <form action="register" method="post">
                       <div class="input-group">
    <label>이메일</label>
    <input type="email" name="email" id="email" placeholder="example@email.com" required>
    <button type="button" id="verifyBtn">인증하기</button>
</div>
<div class="input-group" id="verification-input" style="display: none;">
    <label>인증번호</label>
    <input type="text" name="verificationCode" placeholder="인증번호 6자리 입력" maxlength="6">
    <button type="button" id="confirmBtn">확인</button>
    <span class="timer"></span>
</div>
                        <div class="input-group">
                            <label>비밀번호</label>
                            <input type="password" name="password" placeholder="8자 이상, 특수문자 포함" required>
                        </div>
                        <div class="input-group">
                            <label>비밀번호 확인</label>
                            <input type="password" name="confirmPassword" placeholder="비밀번호 재입력" required>
                        </div>
                        <div class="input-group">
                            <label>닉네임</label>
                            <input type="text" name="nickname" placeholder="2-10자 사이 입력" required>
                        </div>
                         <!-- 🌟 활동 지역 설정 (시/군/구 추가 방식) -->
        					<div class="input-group">
            					<label for="region">광역시/도 선택</label>
            					<select id="region" name="region">
               					 <option value="">광역시/도를 선택하세요</option>
                				<!-- 백엔드에서 동적으로 불러오기 -->
            					</select>
        					</div>

        					<div class="input-group">
            				<label for="district">시/군/구 선택</label>
            				<select id="district" name="district">
                			<option value="">먼저 시/도를 선택하세요</option>
            				</select>
        					</div>

        					<button type="button" id="add-district">활동 지역 추가</button>

        					<!-- 🌟 선택한 활동 지역 목록 -->
        					<ul id="selected-districts"></ul>
        
        					<!-- 숨겨진 input (선택된 지역을 서버로 전달) -->
        					<input type="hidden" name="selectedDistricts" id="selectedDistricts">
                        <div class="agreement-group">
    							<div class="agreement-left">
        							<span class="agreement-text">[필수] 이용약관 동의</span>
        							<input type="checkbox" name="termsAgreed" required class="agreement-checkbox">
   								 </div>
    							<a href="#" class="terms-link">약관 보기</a>
						</div>
						 <!-- 개인정보 수집·이용 동의 (필수) -->
    <div class="agreement-group">
        <div class="agreement-left">
            <span class="agreement-text">[필수] 개인정보 수집·이용 동의</span>
            <input type="checkbox" name="privacyAgreed" required class="agreement-checkbox">
        </div>
        <a href="#" class="privacy-link" data-modal="privacyModal">내용 보기</a>
    </div>

    <!-- 개인정보 수집·이용 동의 (선택) -->
    <div class="agreement-group">
        <div class="agreement-left">
            <span class="agreement-text">[선택] 마케팅 정보 수신 동의</span>
            <input type="checkbox" name="marketingAgreed" class="agreement-checkbox">
        </div>
        <a href="#" class="privacy-link" data-modal="privacyModal">내용 보기</a>
    </div>
</div>

                        <button type="submit" class="signup-button" disabled>다음 단계로</button>
                    </form>
                    <button type="button" onclick="history.back()" class="back-button">
    뒤로가기
</button>
                </div>
            </div>
        </div>
    </div>
    <!-- 약관 모달 -->
<div id="termsModal" class="terms-modal">
    <div class="terms-modal-overlay"></div>
    <div class="terms-modal-content-wrapper">
        <div class="terms-modal-content">
            <div class="terms-modal-header">
                 <h2>Matchday 이용약관</h2>
                <span class="close-modal">&times;</span>
            </div>
            <div class="terms-modal-body">
                <p><div class="terms-content">
    <div class="terms-section">
        
        <div class="terms-chapter">
            <h4>제1장 총칙</h4>
            
            <div class="terms-article">
                <h5>제1조 (목적)</h5>
                <p>본 약관은 Matchday(이하 "회사")가 제공하는 보드게임 모임 매칭 서비스(이하 "서비스") 이용과 관련하여 회사와 이용자의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.</p>
            </div>

            <div class="terms-article">
                <h5>제2조 (용어의 정의)</h5>
                <ol>
                    <li>1. "사이트"란 회사가 서비스를 이용자에게 제공하기 위하여 운영하는 온라인 플랫폼을 말합니다.</li>
                    <li>2. "이용자"란 사이트에 접속하여 본 약관에 따라 서비스를 이용하는 회원 및 비회원을 말합니다.</li>
                    <li>3. "회원"이란 사이트에 회원가입을 하여 아이디(ID)를 부여받은 자로서, 계속적으로 서비스를 이용할 수 있는 자를 말합니다.</li>
                    <li>4. "모임"이란 회원들 간에 이루어지는 보드게임 모임을 의미합니다.</li>
                    <li>5. "모임장"이란 모임을 생성하고 관리하는 회원을 말합니다.</li>
                    <li>6. "참가자"란 모임에 참여하는 회원을 말합니다.</li>
                </ol>
            </div>

            <div class="terms-article">
                <h5>제3조 (약관의 효력과 개정)</h5>
                <ol>
                    <li>1. 본 약관은 서비스 화면에 게시하거나 기타 방법으로 회원에게 공지함으로써 효력이 발생합니다.</li>
                    <li>2. 회사는 필요한 경우 관련 법령을 위배하지 않는 범위 내에서 본 약관을 개정할 수 있습니다.</li>
                    <li>3. 개정된 약관은 공지사항을 통해 7일 전에 공지하며, 회원이 명시적으로 거부의사를 표시하지 않을 경우 약관 개정에 동의한 것으로 봅니다.</li>
                </ol>
            </div>
        </div>

        <div class="terms-chapter">
            <h4>제2장 서비스 이용</h4>
            
            <div class="terms-article">
                <h5>제4조 (서비스의 내용)</h5>
                <p>회사가 제공하는 서비스는 다음과 같습니다:</p>
                <ol>
                    <li>1. 보드게임 모임 생성 및 참가 서비스</li>
                    <li>2. 모임 참가자 간 커뮤니케이션 서비스</li>
                    <li>3. 친구 추천 및 관리 서비스</li>
                    <li>4. 지역 기반 모임 추천 서비스</li>
                    <li>5. 알림 서비스</li>
                    <li>6. 기타 회사가 추가 개발하거나 제휴계약 등을 통해 회원에게 제공하는 일체의 서비스</li>
                </ol>
            </div>

            <div class="terms-article">
                <h5>제5조 (모임 운영)</h5>
                <div class="terms-sub-article">
                    <p>1. 모임장의 권한과 의무:</p>
                    <ul>
                        <li>- 모임의 내용, 일시, 장소, 참가 인원을 설정할 수 있습니다.</li>
                        <li>- 모임 확정 전에 모임 정보를 수정할 수 있습니다.</li>
                        <li>- 부적절한 행위를 하는 참가자를 모임에서 제외할 수 있습니다.</li>
                        <li>- 모임 시작 24시간 전까지 모임을 취소할 수 있습니다.</li>
                    </ul>
                </div>
                <div class="terms-sub-article">
                    <p>2. 참가자의 권한과 의무:</p>
                    <ul>
                        <li>- 모임 확정 전까지 참가를 취소할 수 있습니다.</li>
                        <li>- 모임 규칙과 약속 시간을 준수해야 합니다.</li>
                        <li>- 불가피한 사정으로 불참 시 반드시 모임장에게 사전 통보해야 합니다.</li>
                    </ul>
                </div>
            </div>

            <div class="terms-article">
                <h5>제6조 (지역 기반 서비스)</h5>
                <ol>
                    <li>1. 회원은 하나 이상의 활동 지역을 설정해야 합니다.</li>
                    <li>2. 활동 지역은 언제든지 추가 또는 삭제할 수 있습니다.</li>
                    <li>3. 회사는 회원의 활동 지역을 기반으로 모임을 추천할 수 있습니다.</li>
                </ol>
            </div>

            <div class="terms-article">
                <h5>제7조 (친구 관계)</h5>
                <ol>
                    <li>1. 회원은 다른 회원에게 친구 신청을 할 수 있습니다.</li>
                    <li>2. 친구 신청을 받은 회원은 이를 수락하거나 거절할 수 있습니다.</li>
                    <li>3. 친구 관계는 일방의 의사로 언제든지 해지할 수 있습니다.</li>
                </ol>
            </div>

            <div class="terms-article">
                <h5>제8조 (알림 서비스)</h5>
                <div class="terms-sub-article">
                    <p>1. 회사는 다음과 같은 경우 회원에게 알림을 제공합니다:</p>
                    <ul>
                        <li>- 친구 신청 및 수락</li>
                        <li>- 모임 참가 신청 및 수락</li>
                        <li>- 모임 확정 및 취소</li>
                        <li>- 모임 정보 변경</li>
                        <li>- 기타 서비스 이용에 필요한 정보</li>
                    </ul>
                </div>
                <p>2. 회원은 알림 설정을 변경하거나 해제할 수 있습니다.</p>
            </div>
        </div>

        <div class="terms-chapter">
            <h4>제3장 안전 및 책임</h4>
            
            <div class="terms-article">
                <h5>제9조 (모임 안전)</h5>
                <div class="terms-sub-article">
                    <p>1. 회원은 오프라인 모임 시 다음 사항을 준수해야 합니다:</p>
                    <ul>
                        <li>- 공공장소에서 모임 진행</li>
                        <li>- 방역 수칙 준수</li>
                        <li>- 다른 참가자에 대한 기본적 예의 준수</li>
                        <li>- 음주 및 흡연 등 타인에게 불쾌감을 줄 수 있는 행위 자제</li>
                    </ul>
                </div>
                <p>2. 회사는 모임 중 발생하는 사고나 분쟁에 대해 책임지지 않습니다.</p>
            </div>

            <div class="terms-article">
                <h5>제10조 (게임 관련 책임)</h5>
                <ol>
                    <li>1. 게임 규칙과 관련된 분쟁은 참가자들 간에 원만히 해결해야 합니다.</li>
                    <li>2. 게임 용품의 파손 등에 대한 책임은 파손의 원인을 제공한 참가자에게 있습니다.</li>
                </ol>
            </div>

            <div class="terms-article">
                <h5>제11조 (제재 조치)</h5>
                <div class="terms-sub-article">
                    <p>1. 회사는 다음 각 호에 해당하는 경우 회원의 서비스 이용을 제한하거나 회원 자격을 박탈할 수 있습니다:</p>
                    <ul>
                        <li>- 타인의 개인정보를 도용한 경우</li>
                        <li>- 모임 참가 후 무단 불참을 반복하는 경우</li>
                        <li>- 다른 회원에게 위협이나 피해를 주는 행위를 한 경우</li>
                        <li>- 서비스를 영리 목적으로 악용하는 경우</li>
                        <li>- 기타 법령 또는 본 약관을 위반한 경우</li>
                    </ul>
                </div>
                <div class="terms-sub-article">
                    <p>2. 제재 조치는 다음과 같은 단계로 이루어집니다:</p>
                    <ul>
                        <li>- 1차: 경고</li>
                        <li>- 2차: 30일 이용 정지</li>
                        <li>- 3차: 영구 이용 정지</li>
                    </ul>
                </div>
                <p>3. 회원은 제재 조치에 대해 이의신청을 할 수 있으며, 회사는 이를 검토하여 적절한 조치를 취합니다.</p>
            </div>
        </div>

        <div class="terms-chapter">
            <h4>제4장 기타</h4>
            
            <div class="terms-article">
                <h5>제12조 (준거법 및 관할법원)</h5>
                <ol>
                    <li>1. 본 약관은 대한민국 법률에 따라 규율되고 해석됩니다.</li>
                    <li>2. 서비스 이용으로 발생한 분쟁에 대해 소송이 제기될 경우 회사의 본점 소재지를 관할하는 법원을 전속적 합의관할 법원으로 합니다.</li>
                </ol>
            </div>

            <div class="terms-article">
                <h5>제13조 (운영정책)</h5>
                <ol>
                    <li>1. 회사는 본 약관 외에 별도의 운영정책을 둘 수 있습니다.</li>
                    <li>2. 운영정책은 서비스를 운영함에 있어 필요한 주요 사항을 규정하며, 회원은 이를 준수해야 합니다.</li>
                </ol>
            </div>
        </div>

        <div class="terms-chapter">
            <h4>부칙</h4>
            <div class="terms-article">
                <ol>
                    <li>1. 본 약관은 2025년 2월 5일부터 시행됩니다.</li>
                    <li>2. 본 약관 시행 전에 가입한 회원에 대해서도 본 약관이 적용됩니다.</li>
                </ol>
            </div>
        </div>
    </div>
</div>
           
        </div>
        <div class="terms-modal-footer">
                <button type="button" class="close-button">닫기</button>
            </div>
        </div>
    </div>
    </div>
<div id="privacyModal" class="terms-modal">
    <div class="terms-modal-overlay"></div>
    <div class="terms-modal-content-wrapper">
        <div class="terms-modal-content">
            <div class="terms-modal-header">
                <h2>개인정보 수집·이용 동의서</h2>
                <span class="close-modal">&times;</span>
            </div>
            <div class="terms-modal-body">
                <div class="privacy-policy">
                    <h4>개인정보 수집·이용 동의서</h4>
                    
                    <h5>1. 수집하는 개인정보 항목</h5>
                    <p>회사는 회원가입 및 서비스 이용을 위해 다음의 개인정보를 수집합니다:</p>
                    <ul>
                        <li>필수 항목: 이름, 이메일, 비밀번호, 활동 지역</li>
                        <li>선택 항목: 프로필 사진, 친구 목록</li>
                    </ul>

                    <h5>2. 개인정보 수집·이용 목적</h5>
                    <p>회사는 수집한 개인정보를 다음의 목적을 위해 활용합니다:</p>
                    <ul>
                        <li>회원 관리 및 본인 확인</li>
                        <li>보드게임 모임 매칭 및 알림 서비스 제공</li>
                        <li>친구 추천 및 관리 서비스 제공</li>
                    </ul>

                    <h5>3. 개인정보 보유 및 이용 기간</h5>
                    <p>회사는 개인정보 수집·이용 목적이 달성된 후에는 해당 정보를 지체 없이 파기합니다. 단, 관련 법령에 의해 보존이 필요한 경우 일정 기간 동안 보관합니다:</p>
                    <ul>
                        <li>회원 탈퇴 시 즉시 파기</li>
                        <li>전자상거래 등에서의 소비자 보호에 관한 법률에 따라 거래 기록 보존 (5년)</li>
                    </ul>

                    <h5>4. 동의를 거부할 권리 및 불이익</h5>
                    <p>이용자는 개인정보 수집·이용에 대한 동의를 거부할 수 있습니다. 다만, 필수 항목 수집에 대한 동의를 거부할 경우 서비스 이용이 제한될 수 있습니다.</p>

                    <h5>5. 선택적 개인정보 수집 및 이용</h5>
                    <p>회사는 마케팅 및 광고 목적으로 회원의 동의를 받아 별도로 개인정보를 수집할 수 있습니다. 해당 동의는 거부하셔도 서비스 이용에는 영향을 미치지 않습니다.</p>
                </div>
            </div>
            <div class="terms-modal-footer">
                <button type="button" class="close-button">닫기</button>
            </div>
        </div>
    </div>
</div>
    <script>
    
    let verificationTimer = null;

    function startVerificationTimer() {
        let timeLeft = 180; // 3분
        const timerSpan = document.querySelector('.timer');
        
        if (verificationTimer) {
            clearInterval(verificationTimer);
        }
        
        verificationTimer = setInterval(() => {
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;
            timerSpan.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;
            
            if (timeLeft <= 0) {
                clearInterval(verificationTimer);
                timerSpan.textContent = "시간 초과";
                document.getElementById('confirmBtn').disabled = true;
            }
            timeLeft--;
        }, 1000);
    }

    function sendVerification() {
        const email = document.getElementById('email').value;
        const verifyBtn = document.getElementById('verifyBtn');
        const emailInput = document.getElementById('email');

        if (!email || !formValidation.email) {
            alert('유효한 이메일을 입력해주세요.');
            return;
        }

        // 이메일 중복 체크
        fetch(contextPath + '/checkEmail', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'email=' + encodeURIComponent(email)
        })
        .then(response => response.json())
        .then(data => {
            if (data.isDuplicate) {
                alert('이미 등록된 이메일입니다.');
                return;
            }

            // 인증 메일 발송
            verifyBtn.disabled = true;
            verifyBtn.textContent = '발송 중...';

            fetch(contextPath + '/sendVerification', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'email=' + encodeURIComponent(email)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('인증번호가 발송되었습니다.');
                    document.getElementById('verification-input').style.display = 'flex';
                    startVerificationTimer();
                    verifyBtn.textContent = '재발송';
                    verifyBtn.disabled = false;

                    // ✅ `disabled` 대신 `readonly`로 변경 (서버로 값이 정상적으로 전송됨)
                    emailInput.removeAttribute('disabled');  // ❌ 삭제해야 함
                    emailInput.setAttribute('readonly', 'readonly');  // ✅ readonly 유지
                    emailInput.style.backgroundColor = '#f3f4f6';

                } else {
                    alert(data.message || '인증번호 발송에 실패했습니다.');
                    verifyBtn.textContent = '인증하기';
                    verifyBtn.disabled = false;
                    emailInput.removeAttribute('disabled');  // ❌ disabled 제거
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('인증번호 발송 중 오류가 발생했습니다.');
                verifyBtn.textContent = '인증하기';
                verifyBtn.disabled = false;
                emailInput.removeAttribute('disabled');  // ❌ disabled 제거
            });
        });
    }

    function verifyCode() {
        const email = document.getElementById('email').value;
        const verificationCode = document.querySelector('input[name="verificationCode"]').value;
        const confirmBtn = document.getElementById('confirmBtn');
        
        confirmBtn.disabled = true;
        
        fetch(contextPath + '/verifyEmail', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'email=' + encodeURIComponent(email) + '&authCode=' + encodeURIComponent(verificationCode)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                clearInterval(verificationTimer);
                document.querySelector('.timer').textContent = '인증완료';
                formValidation.emailVerified = true;
                updateSubmitButton();
                
                // 인증 완료 후 UI 처리
                const emailInput = document.getElementById('email');
                emailInput.setAttribute('readonly', 'readonly');  // disabled 대신 readonly 사용
                emailInput.style.backgroundColor = '#f3f4f6';    // 시각적으로 읽기 전용임을 표시
                document.getElementById('verifyBtn').disabled = true;
                document.querySelector('input[name="verificationCode"]').disabled = true;
                confirmBtn.disabled = true;
            } else {
                alert(data.message);
                confirmBtn.disabled = false;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('인증 확인 중 오류가 발생했습니다.');
            confirmBtn.disabled = false;
        });
    }

    // 이벤트 리스너 추가
    document.getElementById('verifyBtn').addEventListener('click', sendVerification);
    document.getElementById('confirmBtn').addEventListener('click', verifyCode);
    let nicknameTimeout = null;

    document.querySelector('input[name="nickname"]').addEventListener('input', function(e) {
        const nickname = e.target.value.trim();
        const nicknameInput = e.target;
        formValidation.nickname = false;
        updateSubmitButton();
        
        // 이전 타이머 취소
        if (nicknameTimeout) {
            clearTimeout(nicknameTimeout);
        }

        // 입력값이 없으면 검사하지 않음
        if (!nickname) {
            setNicknameStatus(nicknameInput, '', '');
            return;
        }

        // 2-10자 길이 체크
        if (nickname.length < 2 || nickname.length > 10) {
            setNicknameStatus(nicknameInput, false, '닉네임은 2-10자 사이여야 합니다.');
            return;
        }

        // 0.5초 디바운스 후 중복 체크
        nicknameTimeout = setTimeout(() => {
            fetch('${pageContext.request.contextPath}/check-nickname', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'nickname=' + encodeURIComponent(nickname)
            })
            .then(response => response.json())
            .then(data => {
                if (data.available) {
                    setNicknameStatus(nicknameInput, true, '사용 가능한 닉네임입니다.');
                    formValidation.nickname = true;
                } else {
                    setNicknameStatus(nicknameInput, false, '이미 사용 중인 닉네임입니다.');
                    formValidation.nickname = false;
                }
                updateSubmitButton();
            })
            .catch(error => {
                console.error('Error:', error);
                setNicknameStatus(nicknameInput, false, '닉네임 확인 중 오류가 발생했습니다.');
                formValidation.nickname = false;
                updateSubmitButton();
            });
        }, 500);
    });
    
 // 비밀번호 유효성 검사
    const passwordInput = document.querySelector('input[name="password"]');
    passwordInput.addEventListener('input', function(e) {
        const password = e.target.value;
        formValidation.password = validatePassword(password);
        updateSubmitButton();

        // 비밀번호 확인 필드 재검사
        const confirmPassword = confirmPasswordInput.value;
        formValidation.passwordConfirm = (password === confirmPassword && confirmPassword !== '');
        updateSubmitButton();
    });

    // 비밀번호 확인 검사
    const confirmPasswordInput = document.querySelector('input[name="confirmPassword"]');
    confirmPasswordInput.addEventListener('input', function(e) {
        const password = passwordInput.value;
        const confirmPassword = e.target.value;
        formValidation.passwordConfirm = (password === confirmPassword && password !== '');
        updateSubmitButton();
    });

    // 이메일 유효성 검사
     const emailInput = document.querySelector('input[name="email"]');
    emailInput.addEventListener('input', function(e) {
        const email = e.target.value;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        formValidation.email = emailRegex.test(email);
        updateSubmitButton();

        // 시각적 피드백
        if (email.length > 0) {
            if (formValidation.email) {
                emailInput.style.borderColor = '#059669';
            } else {
                emailInput.style.borderColor = '#dc2626';
            }
        } else {
            emailInput.style.borderColor = '';
        }
    });

    // 약관 동의 체크
    const termsCheckbox = document.querySelector('input[name="termsAgreed"]');
    termsCheckbox.addEventListener('change', function(e) {
        formValidation.terms = e.target.checked;
        updateSubmitButton();
    });

    // 활동 지역 선택 체크
   const selectedDistrictsObserver = new MutationObserver(function(mutations) {
        const selectedDistricts = document.getElementById('selected-districts');
        formValidation.district = selectedDistricts.children.length > 0;
        updateSubmitButton();
    });

    selectedDistrictsObserver.observe(document.getElementById('selected-districts'), {
        childList: true
    });
    
 // 폼 유효성 검사 상태 출력 (디버깅용)
    function logValidationState() {
        console.log('Form Validation State:', formValidation);
    }

    function setNicknameStatus(input, isValid, message) {
        const feedback = input.nextElementSibling || document.createElement('div');
        feedback.className = 'feedback-message ' + (isValid ? 'text-success' : 'text-error');
        feedback.style.fontSize = '12px';
        feedback.style.marginTop = '4px';
        feedback.style.color = isValid ? '#059669' : '#dc2626';
        feedback.textContent = message;

        if (!input.nextElementSibling) {
            input.parentNode.appendChild(feedback);
        }

        input.style.borderColor = isValid ? '#059669' : '#dc2626';
    }
    document.addEventListener('DOMContentLoaded', function() {
        loadRegions();
        setupEventListeners();
    });

 // 광역시/도 목록 불러오기
    function loadRegions() {
        fetch(contextPath + '/api/regions')
            .then(res => {
            	
                if (!res.ok) throw new Error('Failed to fetch regions');
                return res.json();
            })
            .then(data => {
                const regionSelect = document.getElementById('region');
                regionSelect.innerHTML = '<option value="">광역시/도를 선택하세요</option>';
                data.forEach(region => {
                    const opt = document.createElement('option');
                    opt.value = region.id;
                    opt.textContent = region.name;
                    regionSelect.appendChild(opt);
                });
            })
            .catch(err => console.error(err));
    }

    // 시/군/구 목록 불러오기
    function loadDistricts(regionId) {
        if (!regionId) return;
        
        const districtSelect = document.getElementById('district');
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
                districtSelect.disabled = true;
            });
    }

    // 이벤트 리스너 설정
    function setupEventListeners() {
        const regionSelect = document.getElementById('region');
        const addDistrictBtn = document.getElementById('add-district');
        const form = document.querySelector('form');
        
        regionSelect.addEventListener('change', (e) => {
            const regionId = e.target.value;
            loadDistricts(regionId);
        });

        addDistrictBtn.addEventListener('click', () => {
            const districtSelect = document.getElementById('district');
            const regionSelect = document.getElementById('region');
            const selectedDistricts = document.getElementById('selected-districts');
            
            if (!districtSelect.value) {
                alert('시/군/구를 선택해주세요.');
                return;
            }

            // 이미 추가된 지역인지 확인
            const existingDistrict = document.querySelector(
                `#selected-districts li[data-id="${districtSelect.value}"]`
            );
            
            if (existingDistrict) {
                alert('이미 추가된 지역입니다.');
                return;
            }

            // 선택된 지역의 텍스트 가져오기
            const regionText = regionSelect.options[regionSelect.selectedIndex].text;
            const districtText = districtSelect.options[districtSelect.selectedIndex].text;
            const fullText = regionText + ' ' + districtText;  // 템플릿 리터럴 대신 문자열 연결 사용
            
            console.log('RegionText:', regionText);
            console.log('DistrictText:', districtText);
            console.log('FullText:', fullText);

            // DOM 요소 생성
            const li = document.createElement('li');
            li.setAttribute('data-id', districtSelect.value);

            const span = document.createElement('span');
            span.className = 'district-text';
            span.textContent = fullText;  // textNode를 사용하는 대신 직접 textContent를 설정

            const button = document.createElement('button');
            button.type = 'button';
            button.className = 'remove-district';
            button.textContent = '×';

            li.appendChild(span);
            li.appendChild(button);

            console.log('Span textContent after setting:', span.textContent);
            console.log('Li innerHTML before append:', li.innerHTML);
            
            selectedDistricts.appendChild(li);
            
            console.log('Final li innerHTML:', li.innerHTML);
            
         // 선택 초기화
            districtSelect.value = '';
            updateSelectedDistricts();
        });

        // 지역 삭제 이벤트 위임
        document.getElementById('selected-districts').addEventListener('click', function(e) {
            if (e.target.classList.contains('remove-district')) {
                e.target.closest('li').remove();
                updateSelectedDistricts();
            }
        });
        form.addEventListener('submit', function(e) {
            const selectedDistricts = document.getElementById('selectedDistricts').value;
            
            if (!selectedDistricts) {
                e.preventDefault();
                alert('최소 1개 이상의 활동 지역을 선택해주세요.');
                document.getElementById('region').focus();
                return false;
            }
        });
    }

    // 선택된 지역 목록 업데이트
    function updateSelectedDistricts() {
        const selectedIds = Array.from(
            document.querySelectorAll('#selected-districts li')
        ).map(li => li.getAttribute('data-id'));
        
        document.getElementById('selectedDistricts').value = selectedIds.join(',');
    }
    
 // fetch 관련 코드 제거하고 다음 코드만 남김
$(function() {
    console.log("문서 준비됨");

    // 개인정보 모달
    const $privacyModal = $('#privacyModal');
    const $termsModal = $('#termsModal');

    console.log("Privacy Modal:", $privacyModal[0]);
    console.log("Terms Modal:", $termsModal[0]);

    // 모든 링크 선택
    const $privacyLinks = $('.privacy-link');
    const $termsLinks = $('.terms-link');

    console.log("Privacy Links:", $privacyLinks);
    console.log("Privacy Links Count:", $privacyLinks.length);
    console.log("Terms Links:", $termsLinks);
    console.log("Terms Links Count:", $termsLinks.length);

    // 모달 열기/닫기 함수
    function openModal($modal) {
        if ($modal.length) {
            console.log("모달 열기:", $modal[0]);
            $modal.addClass('show');
        } else {
            console.error("모달이 정의되지 않음");
        }
    }

    function closeModal($modal) {
        if ($modal.length) {
            $modal.removeClass('show');
        }
    }

    // 개인정보 모달 이벤트
    $privacyLinks.on('click', function(e) {
        e.preventDefault();
        console.log('개인정보 링크 클릭');
        console.log('클릭된 링크:', this);
        
        openModal($privacyModal);
    });

    // 약관 모달 이벤트
    $termsLinks.on('click', function(e) {
        e.preventDefault();
        console.log('약관 링크 클릭');
        console.log('클릭된 링크:', this);
        
        openModal($termsModal);
    });

    // 닫기 버튼 이벤트
    function setupCloseButtons($modal) {
        $modal.find('.close-modal, .close-button').on('click', function() {
            closeModal($modal);
        });

        // 모달 오버레이 클릭 시 닫기
        $modal.on('click', function(event) {
            if (event.target === this) {
                closeModal($modal);
            }
        });
    }

    // 모달 닫기 버튼 설정
    setupCloseButtons($privacyModal);
    setupCloseButtons($termsModal);
});
const formValidation = {
	    email: false,
	    emailVerified: false,
	    password: false,
	    passwordConfirm: false,
	    nickname: false,
	    termsAgreed: false,     // 이용약관 필수 동의
	    privacyAgreed: false,   // 개인정보 수집·이용 필수 동의
	    district: false
	    // marketingAgreed는 선택사항이므로 제외
	};
//제출 버튼 활성화/비활성화 함수
function updateSubmitButton() {
    const submitButton = document.querySelector('.signup-button');
    
    const isAllRequiredValid = 
        formValidation.email &&
        formValidation.emailVerified &&  // 추가
        formValidation.password &&
        formValidation.passwordConfirm &&
        formValidation.nickname &&
        formValidation.termsAgreed &&
        formValidation.privacyAgreed &&
        formValidation.district;
    
    submitButton.disabled = !isAllRequiredValid;
    submitButton.style.backgroundColor = isAllRequiredValid ? '#000000' : '#cccccc';
    submitButton.style.cursor = isAllRequiredValid ? 'pointer' : 'not-allowed';
}

function validatePassword(password) {
    const minLength = password.length >= 8;
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
    
    return minLength && hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar;
}

// 페이지 로드 시 초기 버튼 상태 설정
document.addEventListener('DOMContentLoaded', function() {
    updateSubmitButton();
});

    </script>
</body>
</html>
