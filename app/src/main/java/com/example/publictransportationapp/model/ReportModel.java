package com.example.publictransportationapp.model;

import java.util.Date;

public class ReportModel {

    private String description;
    private String latitude;
    private String longitude;
    private String location;
    private String type;
    private String proof;
    private String proofOriginal;
    private Date timestamp;

    public ReportModel(){

    }

    public ReportModel(String description, String latitude, String longitude, String location, String type, String proof, String proofOriginal, Date timestamp){

        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.type = type;
        this.proof = proof;
        this.proofOriginal = proofOriginal;
        this.timestamp = timestamp;

    }

    public String getDescription() {
        return description;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getProof() {
        return proof;
    }

    public String getProofOriginal() {
        return proofOriginal;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
