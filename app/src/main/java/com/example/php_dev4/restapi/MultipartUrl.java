package com.example.php_dev4.restapi;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MultipartUrl {

    @SerializedName("parts")
    @Expose
    private List<Part> parts = null;
    @SerializedName("abort")
    @Expose
    private String abort;
    @SerializedName("complete")
    @Expose
    private String complete;

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public String getAbort() {
        return abort;
    }

    public void setAbort(String abort) {
        this.abort = abort;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public class Part {

        @SerializedName("index")
        @Expose
        private Integer index;
        @SerializedName("url")
        @Expose
        private String url;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

}