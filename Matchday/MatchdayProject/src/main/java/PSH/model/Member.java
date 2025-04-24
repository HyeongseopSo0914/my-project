package PSH.model;

import java.sql.Timestamp;

public class Member {
	private int id;
    private String email;
    private String password;
    private String salt;  // 🔑 Salt 필드 추가
    private String nickname;
    private String profileImageUrl;
    private String favoriteGames;
    private String friendStatus;  // NONE, PENDING, ACCEPTED, FRIENDS
    private boolean termsAgreed;
    private Timestamp termsAgreedAt;
    private boolean privacyAgreed;
    private Timestamp privacyAgreedAt;
    private boolean marketingAgreed;
    private Timestamp marketingAgreedAt;
    private boolean emailVerified;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
 // 🔑 Salt Getter/Setter 추가
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    
    public String getFavoriteGames() {
        return favoriteGames;
    }

    public void setFavoriteGames(String favoriteGames) {
        this.favoriteGames = favoriteGames;
    }
    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }
 // getter/setter 추가
    public boolean isTermsAgreed() { return termsAgreed; }
    public void setTermsAgreed(boolean termsAgreed) { this.termsAgreed = termsAgreed; }

    public Timestamp getTermsAgreedAt() { return termsAgreedAt; }
    public void setTermsAgreedAt(Timestamp termsAgreedAt) { this.termsAgreedAt = termsAgreedAt; }
    
 // getter/setter 추가
    public boolean isPrivacyAgreed() { return privacyAgreed; }
    public void setPrivacyAgreed(boolean privacyAgreed) { this.privacyAgreed = privacyAgreed; }

    public Timestamp getPrivacyAgreedAt() { return privacyAgreedAt; }
    public void setPrivacyAgreedAt(Timestamp privacyAgreedAt) { this.privacyAgreedAt = privacyAgreedAt; }

    public boolean isMarketingAgreed() { return marketingAgreed; }
    public void setMarketingAgreed(boolean marketingAgreed) { this.marketingAgreed = marketingAgreed; }

    public Timestamp getMarketingAgreedAt() { return marketingAgreedAt; }
    public void setMarketingAgreedAt(Timestamp marketingAgreedAt) { this.marketingAgreedAt = marketingAgreedAt; }
    
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    
    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", favoriteGames='" + favoriteGames + '\'' +
                ", friendStatus='" + friendStatus + '\'' +
                ", termsAgreed=" + termsAgreed +
                ", termsAgreedAt=" + termsAgreedAt +
                '}';
    }
    
}