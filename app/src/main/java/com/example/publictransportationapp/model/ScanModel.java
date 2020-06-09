package com.example.publictransportationapp.model;

import java.util.Date;

public class ScanModel {

    private String code;
    private Date timestamp;

    public ScanModel(){

    }

    public ScanModel(String code, Date timestamp){

        this.code = code;
        this.timestamp = timestamp;

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getCode() {
        return code;
    }

}
