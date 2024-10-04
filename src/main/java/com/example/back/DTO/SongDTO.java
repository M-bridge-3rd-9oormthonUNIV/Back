package com.example.back.DTO;

public class SongDTO {

    private String title;
    private String artist;
    private String songId;

    public SongDTO(String title, String artist, String songId) {
        this.title = title;
        this.artist = artist;
        this.songId = songId;
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

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}
