package com.tnc.wishlist.staticClass;

import com.tnc.wishlist.ModelClasses.AdminInformation;
import com.tnc.wishlist.ModelClasses.OrphanAgeHomeInformation;
import com.tnc.wishlist.ModelClasses.childInformation;
import com.tnc.wishlist.ModelClasses.Wishinformation;

import java.util.ArrayList;

public class DataCentre {
    public static ArrayList<childInformation> childInformations;
    public static ArrayList<AdminInformation> adminInformations;
    public static ArrayList<OrphanAgeHomeInformation> orphanAgeHomeInformations;
    public static ArrayList<Wishinformation> wishinformations;
    public static Wishinformation selectedWish;
    public static childInformation selectedChild;
    public static OrphanAgeHomeInformation selectedhome;

    public static String userId;
    public static int userType;//0 for orphanage //1 for voulunteeers//2 for superAdmin
}
