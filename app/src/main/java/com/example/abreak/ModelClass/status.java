package com.example.abreak.ModelClass;

public class status {
    private  long timeofStory;
    private  String imgUrl;

    public status(){

    }

    public status(long timeofStory, String imgUrl) {
        this.timeofStory = timeofStory;
        this.imgUrl = imgUrl;
    }

    public long getTimeofStory() {
        return timeofStory;
    }

    public void setTimeofStory(long timeofStory) {
        this.timeofStory = timeofStory;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
