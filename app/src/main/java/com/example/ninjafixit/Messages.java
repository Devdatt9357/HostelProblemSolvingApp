package com.example.ninjafixit;

import java.util.ArrayList;

public class Messages {
    String name;
    String description;
    String ImageUrl;
    String Headname;
    String HeadUrl;
    String CheckItem;
public Messages(){

}

    public Messages(String name, String description, String imageUrl, String headname, String headUrl, String checkItem) {
        this.name = name;
        this.description = description;
        ImageUrl = imageUrl;
        Headname = headname;
        HeadUrl = headUrl;
        CheckItem = checkItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getHeadname() {
        return Headname;
    }

    public void setHeadname(String headname) {
        Headname = headname;
    }

    public String getHeadUrl() {
        return HeadUrl;
    }

    public void setHeadUrl(String headUrl) {
        HeadUrl = headUrl;
    }

    public String getCheckItem() {
        return CheckItem;
    }

    public void setCheckItem(String checkItem) {
        CheckItem = checkItem;
    }
}
