package com.example.abreak.ModelClass;

import java.util.ArrayList;

public class stories {
    private long latestStory;
    private String name, pPic;

    private ArrayList<status> statuses;

    public stories(){

    }

    public stories(long latestStory, String name, String pPic, ArrayList<status> statuses) {
        this.latestStory = latestStory;
        this.name = name;
        this.pPic = pPic;
        this.statuses = statuses;
    }

    public long getLatestStory() {
        return latestStory;
    }

    public void setLatestStory(long latestStory) {
        this.latestStory = latestStory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpPic() {
        return pPic;
    }

    public void setpPic(String pPic) {
        this.pPic = pPic;
    }

    public ArrayList<status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<status> statuses) {
        this.statuses = statuses;
    }
}
