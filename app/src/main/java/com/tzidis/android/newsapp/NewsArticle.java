package com.tzidis.android.newsapp;

public class NewsArticle {

    private String mSectionName;
    private String mFirstPublicationDate;
    private String mWebUrl;
    private String mHeadline;
    private String mThumbnail;

 /*
  * Create a new NewsAricle object.
  *
  * @param sectionName is the section of the Guardian newspaper
  * @param firstPublicationDate is the date of the publication
  * @param webUrl is the url to article on the Guardian site
  * @param headline is the article's headline
  * @param thumbnail is the link to the image of the article
  * */

    public NewsArticle(String sectionName, String firstPublicationDate, String webUrl, String
            headline, String thumbnail){

        mSectionName = sectionName;
        mFirstPublicationDate = firstPublicationDate;
        mWebUrl = webUrl;
        mHeadline = headline;
        mThumbnail = thumbnail;
    }

    /**
     * Get the news section
     */

    public String getSectionName(){return mSectionName;}

    /**
     * Get the publication date
     */

    public String getFirstPublicationDate(){return mFirstPublicationDate;}

    /**
     * Get the url of the article
     */

    public String getWebUrl(){return mWebUrl;}

    /**
     * Get the article's headline
     */

    public String getHeadline(){return mHeadline;}

    /**
     * Get the thumbnail of the article
     */

    public String getThumbnail(){return mThumbnail;}

}
