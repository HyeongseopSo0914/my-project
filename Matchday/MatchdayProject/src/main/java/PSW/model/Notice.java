package PSW.model;

import java.sql.Timestamp;

public class Notice {
    private int noticeId;
    private String noticeTitle;
    private String noticeContent;
    private Timestamp noticeRegdate;
    
    public Notice() {};
    
	 // 생성자 추가
	    public Notice(String title, String content) {
	        this.noticeTitle = title;
	        this.noticeContent = content;
	    }


    public Notice(int noticeId, String noticeTitle, String noticeContent, Timestamp noticeRegdate) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeRegdate = noticeRegdate;
    }

    // Getter and Setter
    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public Timestamp getNoticeRegdate() {
        return noticeRegdate;
    }

    public void setNoticeRegdate(Timestamp noticeRegdate) {
        this.noticeRegdate = noticeRegdate;
    }
}

