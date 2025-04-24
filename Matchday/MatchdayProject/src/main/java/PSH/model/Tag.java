// Tag.java
package PSH.model;

import java.sql.Timestamp;

public class Tag {
    private int tagId;
    private String tagName;
    private Timestamp createdAt;
    private int count;  // 이 태그가 받은 횟수

    // Getters and Setters
    public int getTagId() { return tagId; }
    public void setTagId(int tagId) { this.tagId = tagId; }
    
    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}