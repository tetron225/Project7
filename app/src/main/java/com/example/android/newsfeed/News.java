package com.example.android.newsfeed;

public class News {
    private String mTitle;
    private String mSection;
    private String mAuthor;
    private String mDate;
    private String mUrl;

    public News(String title, String author, String section, String date, String url) {
        mTitle = title;
        mAuthor = author;
        mSection = section;
        mDate = date;
        mUrl = url;
    }

    public String getTitle() { return mTitle; }

    public String getAuthor() { return mAuthor; }

    public String getSection() { return mSection; }

    public String getDate() { return mDate; }

    public String getUrl() { return mUrl; }

}

