package com.example.lenovo.hm.meizi;

public class user {
    private String imageUrl;
    private String title;
    private String name;
    private int favorites;
    private int comments;

    public user(String imageUrl, String title, String name, int favorites, int comments) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.name = name;
        this.favorites = favorites;
        this.comments = comments;
    }

    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getName() { return name; }
    public int getFavorites() { return favorites; }
    public int getComments() { return comments; }
}
