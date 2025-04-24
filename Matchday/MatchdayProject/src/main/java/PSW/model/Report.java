package PSW.model;

import java.sql.Timestamp;

public class Report {
    private int id;
    private String title;
    private String content;
    private String nickname;
    private Timestamp regdate;
    
    // 기본 생성자
    public Report() {}

    // 매개변수 생성자
    public Report(int id, String title, String nickname, int recommend, Timestamp regdate, String content) {
        this.id = id;
        this.title = title;
        this.nickname = nickname;
        this.regdate = regdate;
        this.content = content;
    }


    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Timestamp getRegdate() { return regdate; }
    public void setRegdate(Timestamp regdate) { this.regdate = regdate; }
}