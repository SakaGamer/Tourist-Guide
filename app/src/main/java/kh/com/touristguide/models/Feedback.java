package kh.com.touristguide.models;

import com.google.firebase.Timestamp;

public class Feedback {

    private String id;
    private String uid;
    private String commentFeedback;
    private Timestamp timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCommentFeedback() {
        return commentFeedback;
    }

    public void setCommentFeedback(String commentFeedback) {
        this.commentFeedback = commentFeedback;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
