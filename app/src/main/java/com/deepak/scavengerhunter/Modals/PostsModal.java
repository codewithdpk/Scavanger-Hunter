package com.deepak.scavengerhunter.Modals;

public class PostsModal {

    String post_id;
    String post_name;
    String address;
    String Long;
    String lat;
    String hunt_id;
    String hunt_name;
    String createdBy;
    String information;
    String defaultQuestion;
    String questionId;
    String created;
    String updated;
    String status;

    public PostsModal(String post_id, String post_name, String address, String aLong, String lat, String hunt_id, String hunt_name, String createdBy, String information, String defaultQuestion, String questionId, String created, String updated, String status) {
        this.post_id = post_id;
        this.post_name = post_name;
        this.address = address;
        Long = aLong;
        this.lat = lat;
        this.hunt_id = hunt_id;
        this.hunt_name = hunt_name;
        this.createdBy = createdBy;
        this.information = information;
        this.defaultQuestion = defaultQuestion;
        this.questionId = questionId;
        this.created = created;
        this.updated = updated;
        this.status = status;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_name() {
        return post_name;
    }

    public void setPost_name(String post_name) {
        this.post_name = post_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getHunt_id() {
        return hunt_id;
    }

    public void setHunt_id(String hunt_id) {
        this.hunt_id = hunt_id;
    }

    public String getHunt_name() {
        return hunt_name;
    }

    public void setHunt_name(String hunt_name) {
        this.hunt_name = hunt_name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getDefaultQuestion() {
        return defaultQuestion;
    }

    public void setDefaultQuestion(String defaultQuestion) {
        this.defaultQuestion = defaultQuestion;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
