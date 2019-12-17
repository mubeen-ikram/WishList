package com.tnc.wishlist.ModelClasses;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DonarDonations implements Serializable {
    private String debitCardNumber;
    private String donation;
    private String notes;

    public DonarDonations() {
    }

    public String getDebitCardNumber() {
        return debitCardNumber;
    }

    public void setDebitCardNumber(String debitCardNumber) {
        this.debitCardNumber = debitCardNumber;
    }

    public String getDonation() {
        return donation;
    }

    public void setDonation(String donation) {
        this.donation = donation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

//
//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("debitCardNumber", debitCardNumber);
//        result.put("donation", donation);
//        result.put("notes", notes);
//
//        return result;
//    }
}
