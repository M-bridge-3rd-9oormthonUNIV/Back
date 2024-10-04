package com.example.back.DTO;

public class albumDTO {
    private String title;
    private String artist;
    private String albumImageUrl;

    public albumDTO(String artist, String title, String albumImageUrl) {
        this.artist = artist;
        this.title = title;
        this.albumImageUrl = albumImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public void setAlbumImageUrl(String albumImageUrl) {
        this.albumImageUrl = albumImageUrl;
    }
}
