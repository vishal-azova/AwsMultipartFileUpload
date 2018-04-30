package com.example.php_dev4.restapi;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "InitiateMultipartUploadResult")
public class InitiateMultipartUploadResult {


    /*<InitiateMultipartUploadResult xmlns="http://s3.amazonaws.com/doc/2006-03-01/">
            <Bucket>test-azova</Bucket>
            <Key>multipartuploads/1524631061Testbucket</Key>
            <UploadId>25idY6KJNfrs2d.eSQjyVJZMmV3GziYMzD8EiogTxmzwU_49kriY2ZjThjjM2AKS_b.j2X_BBwKK.J5Tx7af2LM4b6dhGVlVhiFflXBaab0-</UploadId>
            </InitiateMultipartUploadResult>
    */

    public String getUploadId() {
        return uploadId;
    }

    @Element(name = "UploadId")
    private String uploadId;

    @Element(name = "Bucket")
    private String bucket;

    @Element(name = "Key")
    private String key;

    public InitiateMultipartUploadResult() {
    }
}