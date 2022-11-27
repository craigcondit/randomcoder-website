package org.randomcoder.website.model;


public class TagStatistics {

    private Tag tag;
    private int articleCount;

    public TagStatistics() {
    }

    public TagStatistics(Tag tag, int articleCount) {
        this.tag = tag;
        this.articleCount = articleCount;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

}
