package com.deepak.scavengerhunter.classes.Modals;

public class User {

    String user_id;
    String name;
    String email;
    String googleid;
    String facebookid;
    String image_url;
    String mode;
    String created;
    String updated;

    public User(String user_id, String name, String email, String googleid, String facebookid, String image_url, String mode, String created, String updated) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.googleid = googleid;
        this.facebookid = facebookid;
        this.image_url = image_url;
        this.mode = mode;
        this.created = created;
        this.updated = updated;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getGoogleid() {
        return googleid;
    }

    public void setGoogleid(String googleid) {
        this.googleid = googleid;
    }

    public String getFacebookid() {
        return facebookid;
    }

    public void setFacebookid(String facebookid) {
        this.facebookid = facebookid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
