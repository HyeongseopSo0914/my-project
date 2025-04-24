package PSW.model;

import java.sql.Timestamp;

public class Bulletin {
    private int id;
    private String title;
    private String nickname;
    private int recommend;
    private Timestamp regdate;
    private String content;
    
    // 기본 생성자
    public Bulletin() {}

    // 매개변수 생성자
    public Bulletin(int id, String title, String nickname, int recommend, Timestamp regdate, String content) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.recommend = recommend;
        this.regdate = regdate;
        this.content = content;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getRecommend() {
        return recommend;
    }
    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }
    public Timestamp getRegdate() {
        return regdate;
    }
    public void setRegdate(Timestamp regdate) {
        this.regdate = regdate;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
