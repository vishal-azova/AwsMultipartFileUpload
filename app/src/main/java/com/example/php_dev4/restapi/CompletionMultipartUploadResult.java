package com.example.php_dev4.restapi;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "CompleteMultipartUploadResult")
public class CompletionMultipartUploadResult {


    /*<?xml version="1.0" encoding="UTF-8"?>
<CompleteMultipartUploadResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
    <Location>https://s3-us-west-2.amazonaws.com/test-azova/multipartuploads%2F1524651477Testbucket1</Location>
    <Bucket>test-azova</Bucket>
    <Key>multipartuploads/1524651477Testbucket1</Key>
    <ETag>&quot;c802fd94e3b1035df32e0799379ff5bf-1&quot;</ETag>
</CompleteMultipartUploadResult>
    */

    @Element(name = "Location")
    private String location;

    @Element(name = "Bucket")
    private String bucket;

    @Element(name = "Key")
    private String key;

    @Element(name = "ETag")
    private String ETag;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }


    public CompletionMultipartUploadResult() {
    }
}