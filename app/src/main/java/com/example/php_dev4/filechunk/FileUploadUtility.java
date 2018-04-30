package com.example.php_dev4.filechunk;

import java.util.ArrayList;
import java.util.List;

public class FileUploadUtility {

    List<FileUploadModel> fileUploadList = null;
    List<retrofit2.Call> fileUploadRequest = null;

    public List<retrofit2.Call> getFileUploadRequest() {
        return fileUploadRequest;
    }

    public void setFileUploadRequest(List<retrofit2.Call> fileUploadRequest) {
        this.fileUploadRequest = fileUploadRequest;
    }

    private static FileUploadUtility fileUploadUtility = null;

    private FileUploadUtility() {}

    public static FileUploadUtility getInstance() {

        if (fileUploadUtility == null)
            fileUploadUtility = new FileUploadUtility();

        return fileUploadUtility;

    }

    public List<FileUploadModel> getFileUploadList() {

        if (fileUploadList == null)
            fileUploadList = new ArrayList<>();

        return fileUploadList;
    }

    public void setFileUploadList(List<FileUploadModel> fileUploadList) {
        this.fileUploadList = fileUploadList;
    }

}
