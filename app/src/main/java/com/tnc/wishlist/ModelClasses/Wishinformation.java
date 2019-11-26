package com.tnc.wishlist.ModelClasses;

import java.util.HashMap;

public class Wishinformation {
    private String currentCondition;
    private HashMap<String,String> donor;
    private String name;
    private String orphanId;
    private String photo;
    private String price;
    private String pricePayed;
    private String requirnment;
    private String wishId;
    private String wisherName;
    private String date;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWisherName() {
        return wisherName;
    }

    public void setWisherName(String wisherName) {
        this.wisherName = wisherName;
    }

    public String getCurrentCondition() {
        return currentCondition;
    }

    public void setCurrentCondition(String currentCondition) {
        this.currentCondition = currentCondition;
    }

    public HashMap<String, String> getDonor() {
        return donor;
    }

    public void setDonor(HashMap<String, String> donor) {
        this.donor = donor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrphanId() {
        return orphanId;
    }

    public void setOrphanId(String orphanId) {
        this.orphanId = orphanId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPricePayed() {
        return pricePayed;
    }

    public void setPricePayed(String pricePayed) {
        this.pricePayed = pricePayed;
    }

    public String getRequirnment() {
        return requirnment;
    }

    public void setRequirnment(String requirnment) {
        this.requirnment = requirnment;
    }

    public String getWishId() {
        return wishId;
    }

    public void setWishId(String wishId) {
        this.wishId = wishId;
    }
}
