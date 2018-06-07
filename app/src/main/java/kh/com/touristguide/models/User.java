package kh.com.touristguide.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String uid;
    private String name;
    private String email;
    private String photoUrl;
    private String location;
    private Map<String, Long> clickHistory;
    private Timestamp createdAt;
    private Map<String, Boolean> savedPlace = new HashMap<>();

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Boolean> getSavedPlace() {
        return savedPlace;
    }

    public void setSavedPlace(Map<String, Boolean> savedPlace) {
        this.savedPlace = savedPlace;
    }
}
