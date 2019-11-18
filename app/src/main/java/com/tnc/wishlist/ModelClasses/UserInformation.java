package com.tnc.wishlist.ModelClasses;

import java.util.ArrayList;
import java.util.HashMap;

public class UserInformation {
    String condition;
    String contactNumber;
    String description;
    String email;
    String name;
    String orphanageId;
    String photo;
    String type;
    String noOfWishes;
    HashMap<String,String> wishes;
    String userId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrphanageId() {
        return orphanageId;
    }

    public void setOrphanageId(String orphanageId) {
        this.orphanageId = orphanageId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNoOfWishes() {
        return noOfWishes;
    }

    public void setNoOfWishes(String noOfWishes) {
        this.noOfWishes = noOfWishes;
    }

    public HashMap<String, String> getWishes() {
        return wishes;
    }

    public void setWishes(HashMap<String, String> wishes) {
        this.wishes = wishes;
    }
}
