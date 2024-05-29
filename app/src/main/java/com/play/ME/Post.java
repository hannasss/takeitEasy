package com.play.ME;
import java.io.Serializable;

public class Post implements Serializable {
    // Post 클래스의 내용

    private String postId;
    private String author;
    private String title;
    private String content;
    private long timestamp;

    private String userId;



    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String postId, String author, String title, String content, long timestamp, String userId) {
        this.postId = postId;
        this.author = author;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.userId=userId;
    }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }
}


