package com.lk.jucesp.bots.components;

import java.io.InputStream;

public class DocumentMetadata {

    private String date;
    private String description;
    private InputStream data;

    public DocumentMetadata(InputStream data) {
        this.data = data;
    }

    public DocumentMetadata(String date, String description, InputStream data) {
        this.date = date;
        this.description = description;
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }
}
