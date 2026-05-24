package com.nammashasane.data.model;

import com.google.firebase.Timestamp;

public class Inscription {
    private String id;
    private String name;
    private String locationName;
    private double latitude;
    private double longitude;
    private String photoUrl;
    private String notes;
    private String dynasty;
    private String period;
    private String translationKannada;
    private String giftLaw;
    private Timestamp createdAt;

    // Required empty constructor for Firestore
    public Inscription() {}

    public Inscription(String name, String locationName, double latitude, double longitude,
                       String photoUrl, String notes) {
        this.name = name;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoUrl = photoUrl;
        this.notes = notes;
        this.createdAt = Timestamp.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDynasty() { return dynasty; }
    public void setDynasty(String dynasty) { this.dynasty = dynasty; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getTranslationKannada() { return translationKannada; }
    public void setTranslationKannada(String translationKannada) { this.translationKannada = translationKannada; }

    public String getGiftLaw() { return giftLaw; }
    public void setGiftLaw(String giftLaw) { this.giftLaw = giftLaw; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
