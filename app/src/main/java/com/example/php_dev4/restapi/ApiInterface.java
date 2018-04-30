package com.example.php_dev4.restapi;

import com.example.php_dev4.filechunk.Constants;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET("server.php")
    Call<BasicUrl> getBasicUrl(@Query(Constants.COMMAND) String command,
                                         @Query(Constants.NAME) String fileInfo);

    @POST
    Call<InitiateMultipartUploadResult> getUploadIds(@Url String url);


    @GET("server.php")
    Call<MultipartUrl> getMultipartUrl(@Query(Constants.COMMAND) String command,
                                       @Query(Constants.KEY) String key,
                                       @Query(Constants.NUMPARTS) int numParts,
                                       @Query(Constants.UPLOAD_ID) String uploadId);
    @PUT
    Call<ResponseBody> uploadMultipart(@Url String url,
                                       @Body RequestBody requestBody);

    @POST
    @Headers("Content-Type: text/plain; charset=UTF-8")
    Call<CompletionMultipartUploadResult> getRealLocation(@Url String url,
                                                              @Body RequestBody xml);
}
