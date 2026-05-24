package com.nammashasane.data.model;

import com.google.firebase.Timestamp;

public class PreservationAlert {
    private String id;
    private String damageType;
    private String photoUrl;
    private double latitude;
    private double longitude;
    private String notes;
    private Timestamp createdAt;

    public PreservationAlert() {}

    public PreservationAlert(String damageType, String photoUrl, double latitude,
                             double longitude, String notes) {
        this.damageType = damageType;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notes = notes;
        this.createdAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDamageType() { return damageType; }
    public void setDamageType(String damageType) { this.damageType = damageType; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
