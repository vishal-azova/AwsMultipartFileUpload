package com.example.php_dev4.restapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasicUrl {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("key")
    @Expose
    private String key;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}