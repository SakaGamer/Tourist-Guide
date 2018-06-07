package kh.com.touristguide.models;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kh.com.touristguide.helpers.ConstantValue;

public class Place implements Serializable {

    private String id;
    private String name;
    private String nameKh;
    private String address;
    private String addressKh;
    private String description;
    private String descriptionKh;
    private String province;
    private String provinceKh;
    private String photoUrl;
    private long totalStars;
    private long avgRating;
    private Map<String, Double> geoPoint = new HashMap<>();
    private Map<String, Boolean> categories = new HashMap<>();
    private Map<String, Boolean> categoriesKh = new HashMap<>();
    private Map<String, Long> savedBy = new HashMap<>();
    private Map<String, Boolean> people = new HashMap<>();
    private Map<String, Boolean> peopleKh = new HashMap<>();

    public Place() {
    }

    public Place(String id, String name, String description, String province, Map<String, Boolean> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.province = province;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameKh() {
        return nameKh;
    }

    public void setNameKh(String nameKh) {
        this.nameKh = nameKh;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressKh() {
        return addressKh;
    }

    public void setAddressKh(String addressKh) {
        this.addressKh = addressKh;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionKh() {
        return descriptionKh;
    }

    public void setDescriptionKh(String descriptionKh) {
        this.descriptionKh = descriptionKh;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceKh() {
        return provinceKh;
    }

    public void setProvinceKh(String provinceKh) {
        this.provinceKh = provinceKh;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public long getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(long totalStars) {
        this.totalStars = totalStars;
    }

    public long getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(long avgRating) {
        this.avgRating = avgRating;
    }

    public Map<String, Double> getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(Map<String, Double> geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Map<String, Boolean> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Boolean> categories) {
        this.categories = categories;
    }

    public Map<String, Boolean> getCategoriesKh() {
        return categoriesKh;
    }

    public void setCategoriesKh(Map<String, Boolean> categoriesKh) {
        this.categoriesKh = categoriesKh;
    }

    public Map<String, Long> getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(Map<String, Long> savedBy) {
        this.savedBy = savedBy;
    }

    public Map<String, Boolean> getPeople() {
        return people;
    }

    public void setPeople(Map<String, Boolean> people) {
        this.people = people;
    }

    public Map<String, Boolean> getPeopleKh() {
        return peopleKh;
    }

    public void setPeopleKh(Map<String, Boolean> peopleKh) {
        this.peopleKh = peopleKh;
    }

    // end class
}
