package com.example.php_dev4.filechunk;

import com.example.php_dev4.restapi.MutliPartUploadModel;

import java.io.Serializable;
import java.util.List;

public class FileUploadModel implements Serializable {

    String key;
    String completeUrl;
    String abortUrl;
    String url;
    String fileName;
    String selectedImagePath;
    long timesInMilli;
    int parts;
    String uploadId;

    public enum UPLOADING_STATUS {
        IN_PROGRESS, COMPLETED, ABORTED
    }

    UPLOADING_STATUS uploadingStatus;

    List<MutliPartUploadModel> mutliPartUploadList = null;

    int completionCount;
    int totalPartRequestedCount;




























    public String getSelectedImagePath() {
        return selectedImagePath;
    }

    public void setSelectedImagePath(String selectedImagePath) {
        this.selectedImagePath = selectedImagePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTotalPartRequestedCount() {
        return totalPartRequestedCount;
    }

    public void setTotalPartRequestedCount(int totalPartRequestedCount) {
        this.totalPartRequestedCount = totalPartRequestedCount;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public void setCompletionCount(int completionCount) {
        this.completionCount = completionCount;
    }

    /*public byte[][] getMultiParts() {
        return multiParts;
    }*/

    /*public void setMultiParts(byte[][] multiParts) {
        this.multiParts = multiParts;
    }

    public byte[][] multiParts = null;*/

    public String getCompleteUrl() {
        return completeUrl;
    }


    public List<MutliPartUploadModel> getMutliPartUploadList() {
        return mutliPartUploadList;
    }

    public void setMutliPartUploadList(List<MutliPartUploadModel> mutliPartUploadList) {
        this.mutliPartUploadList = mutliPartUploadList;
    }

    public void setCompleteUrl(String completeUrl) {
        this.completeUrl = completeUrl;
    }

    public String getAbortUrl() {
        return abortUrl;
    }

    public void setAbortUrl(String abortUrl) {
        this.abortUrl = abortUrl;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }


    public long getTimesInMilli() {
        return timesInMilli;
    }

    public void setTimesInMilli(long timesInMilli) {
        this.timesInMilli = timesInMilli;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UPLOADING_STATUS getUploadingStatus() {
        return uploadingStatus;
    }

    public void setUploadingStatus(UPLOADING_STATUS uploadingStatus) {
        this.uploadingStatus = uploadingStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        FileUploadModel fileUploadModel = (FileUploadModel) obj;
        return fileUploadModel.getTimesInMilli() == ((FileUploadModel) obj).getTimesInMilli();
    }

}
