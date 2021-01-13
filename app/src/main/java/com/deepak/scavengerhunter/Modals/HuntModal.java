package com.deepak.scavengerhunter.Modals;

import org.json.JSONArray;

public class HuntModal {

    String hunt_id;
    String createdBy;
    String name;
    String startingArea;
    String completeStartingAddress;
    String startingLong;
    String startingLat;
    String endingArea;
    String endingStartingAddress;
    String endingLong;
    String endingLat;
    String created;
    String updated;
    String status;
    JSONArray posts;

    public JSONArray getPosts() {
        return posts;
    }

    public void setPosts(JSONArray posts) {
        this.posts = posts;
    }

    public HuntModal(String hunt_id, String createdBy, String name, String startingArea, String completeStartingAddress, String startingLong, String startingLat, String endingArea, String endingStartingAddress, String endingLong, String endingLat, String created, String updated, String status, JSONArray posts) {
        this.hunt_id = hunt_id;
        this.createdBy = createdBy;
        this.name = name;
        this.startingArea = startingArea;
        this.completeStartingAddress = completeStartingAddress;
        this.startingLong = startingLong;
        this.startingLat = startingLat;
        this.endingArea = endingArea;
        this.endingStartingAddress = endingStartingAddress;
        this.endingLong = endingLong;
        this.endingLat = endingLat;
        this.created = created;
        this.updated = updated;
        this.status = status;
        this.posts = posts;
    }

    public String getHunt_id() {
        return hunt_id;
    }

    public void setHunt_id(String hunt_id) {
        this.hunt_id = hunt_id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartingArea() {
        return startingArea;
    }

    public void setStartingArea(String startingArea) {
        this.startingArea = startingArea;
    }

    public String getCompleteStartingAddress() {
        return completeStartingAddress;
    }

    public void setCompleteStartingAddress(String completeStartingAddress) {
        this.completeStartingAddress = completeStartingAddress;
    }

    public String getStartingLong() {
        return startingLong;
    }

    public void setStartingLong(String startingLong) {
        this.startingLong = startingLong;
    }

    public String getStartingLat() {
        return startingLat;
    }

    public void setStartingLat(String startingLat) {
        this.startingLat = startingLat;
    }

    public String getEndingArea() {
        return endingArea;
    }

    public void setEndingArea(String endingArea) {
        this.endingArea = endingArea;
    }

    public String getEndingStartingAddress() {
        return endingStartingAddress;
    }

    public void setEndingStartingAddress(String endingStartingAddress) {
        this.endingStartingAddress = endingStartingAddress;
    }

    public String getEndingLong() {
        return endingLong;
    }

    public void setEndingLong(String endingLong) {
        this.endingLong = endingLong;
    }

    public String getEndingLat() {
        return endingLat;
    }

    public void setEndingLat(String endingLat) {
        this.endingLat = endingLat;
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
