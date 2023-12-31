package com.teresa.appnotbelate;

public class User {
    private String name;
    private String email;
    private String userId;
    ExistingEvent existingEvent=null;

    public User(String name, String email, String userId) {
        this.name = name;
        this.email = email;
        this.userId = userId;
    }

    public ExistingEvent getExistingEvent() {
        return existingEvent;
    }

    public void setExistingEvent(ExistingEvent existingEvent) {
        this.existingEvent = existingEvent;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
