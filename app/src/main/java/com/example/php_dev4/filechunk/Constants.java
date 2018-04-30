package com.example.php_dev4.filechunk;

public class Constants {

    // First Request Param
    public static final String COMMAND = "command";
    public static final String CREATE_MULTIPART_UPLOAD = "CreateMultipartUpload";
    public static final String FILE_INFO = "fileInfo";
    public static final String UPLOADING = "uploading";
    public static final String NUMPARTS = "numParts";
    public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    public static final String SIZE = "size";
    public static final String UPLOADED = "uploaded";
    public static final String TYPE = "type";
    public static final String NAME = "name";

    //Third Request
    public static final String UPLOAD_ID = "uploadId";
    public static final String SIGNUPLOADPART = "signuploadpart";
    public static final String KEY = "key";

    // Fourth Request
    public static final String COMPLETE_MULITPART_UPLOAD = "CompleteMultipartUpload";
    public static final String PART = "Part";
    public static final String PART_NUMBER = "PartNumber";
    public static final String ETAG = "ETag";
    public static final String CONTENT_TYPE = "Content-Type";


    //Intents
    public static final String FILE_NAME = "FILE_NAME";
    public static final String TIMES_IN_MILLI = "TIMES_IN_MILLI";
    public static final String TOTAL_PARTS = "TOTAL_PARTS";
    public static final String FILE_UPLOADS = "FILE_UPLOADS";
    public static final String TAG = "TAG";

    public static final int CHUNK_SIZE_BYTES = 5 * 1024 * 1024;
    public static final int CHUNK_SIZE_MB = 5;

}
