package com.deepak.scavengerhunter.activities.classes.Modals;

public class RegisterUser {

    String name;
    String email;
    String password;
    String googleid;
    String facebookid;
    String image_url;
    String mode;

    public RegisterUser(String name, String email, String password, String googleid, String facebookid, String image_url, String mode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.googleid = googleid;
        this.facebookid = facebookid;
        this.image_url = image_url;
        this.mode = mode;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
