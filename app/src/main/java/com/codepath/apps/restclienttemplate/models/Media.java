package com.codepath.apps.restclienttemplate.models;


import org.parceler.Parcel;

@Parcel
public class Media {
    private String urlMedia;
    private int height;
    private int width;

    // Empty constructor needed by the Parceler library
    public Media() {
    }

    public Media(String urlMedia, int height, int width) {
        this.urlMedia = urlMedia;
        this.height = height;
        this.width = width;
    }

    public Media(String urlMedia) {
        this.urlMedia = urlMedia;
    }

    public String getUrlMedia() {
        return urlMedia;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
