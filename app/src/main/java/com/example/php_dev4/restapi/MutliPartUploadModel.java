package com.example.php_dev4.restapi;

public class MutliPartUploadModel {

    MULTIPART_UPLOAD multipartUpload;

    String Url, ETag;
    //byte[] part;
    int partNumber;

    public enum MULTIPART_UPLOAD {
        COMPLETED, FAILURE, IN_QUEUE
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    /*public byte[] getPart() {
        return part;
    }

    public void setPart(byte[] part) {
        this.part = part;
    }*/

    public MULTIPART_UPLOAD getMultipartUpload() {
        return multipartUpload;
    }

    public void setMultipartUpload(MULTIPART_UPLOAD multipartUpload) {
        this.multipartUpload = multipartUpload;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        MutliPartUploadModel mutliPartUploadModel = (MutliPartUploadModel) obj;
        return partNumber == mutliPartUploadModel.partNumber;
    }


}


