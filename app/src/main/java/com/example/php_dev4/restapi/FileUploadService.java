package com.example.php_dev4.restapi;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.example.php_dev4.filechunk.ApplicationClass;
import com.example.php_dev4.filechunk.Common;
import com.example.php_dev4.filechunk.Constants;
import com.example.php_dev4.filechunk.FileUploadModel;
import com.example.php_dev4.filechunk.FileUploadNotification;
import com.example.php_dev4.filechunk.FileUploadUtility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUploadService extends IntentService {

    FileUploadUtility fileUploadUtility = null;

    String TAG = "FileUploadService";

    public FileUploadService() {
        super(FileUploadService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileUploadUtility = FileUploadUtility.getInstance();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        getBasicUrl(intent);
    }

    private void getBasicUrl(Intent intent) {

        long tag = intent.getLongExtra(Constants.TAG, 0);

        final FileUploadModel fileUploadModel = getFileUploadModel(tag);

        Call<BasicUrl> call = ApplicationClass.getInstance().getApiService().getBasicUrl(Constants.CREATE_MULTIPART_UPLOAD,
                fileUploadModel.getFileName());

        APIHelper.enqueueWithRetry(fileUploadModel.getTimesInMilli(), call, 2, new Callback<BasicUrl>() {
            @Override
            public void onResponse(Call<BasicUrl> call, Response<BasicUrl> response) {

                //Log.d("Response", "Success" + fileUploadModel.getFileName() + "");

                BasicUrl basicUrl = response.body();

                fileUploadModel.setUrl(basicUrl.getUrl());
                fileUploadModel.setKey(basicUrl.getKey());
                fileUploadModel.setUploadingStatus(FileUploadModel.UPLOADING_STATUS.IN_PROGRESS);

                int index = fileUploadUtility.getFileUploadList().indexOf(fileUploadModel);

                if (index >= 0)
                    fileUploadUtility.getFileUploadList().set(index, fileUploadModel);

                getUploadId(fileUploadModel.getTimesInMilli());

            }

            @Override
            public void onFailure(Call<BasicUrl> call, Throwable t) {

                Log.d("Response", "Failure");

            }
        });
    }

    private FileUploadModel getFileUploadModel(long tag) {

        FileUploadModel fileUploadModel = new FileUploadModel();
        fileUploadModel.setTimesInMilli(tag);

        int index = getIndex(fileUploadModel);

        if (index >= 0)
            return fileUploadUtility.getFileUploadList().get(index);

        return null;
    }

    private int getIndex(FileUploadModel fileUploadModel) {

        return fileUploadUtility.getFileUploadList().indexOf(fileUploadModel);

    }

    /**
     * Use for getting upload id
     * Passing Basic Url and key which are getting from Our api response.
     *
     * @param tag
     */
    public void getUploadId(final long tag) {

        final FileUploadModel fileUploadModel = getFileUploadModel(tag);

        ApiInterface apiService =
                ApiClient.getXMLClient().create(ApiInterface.class);

        Call<InitiateMultipartUploadResult> call = apiService.getUploadIds(fileUploadModel.getUrl());

        APIHelper.enqueueWithRetry(fileUploadModel.getTimesInMilli(), call, 2, new Callback<InitiateMultipartUploadResult>() {
            @Override
            public void onResponse(Call<InitiateMultipartUploadResult> call, Response<InitiateMultipartUploadResult> response) {

                Log.d(TAG, "Upload Id : " + response.body().getUploadId());

                String uploadId = response.body().getUploadId();

                fileUploadModel.setUploadId(uploadId);

                fileUploadUtility.getFileUploadList().set(getIndex(fileUploadModel), fileUploadModel);

                getMultipartUrls(tag);

            }

            @Override
            public void onFailure(Call<InitiateMultipartUploadResult> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.toString());

            }
        });
    }

    public void getMultipartUrls(final long tag) {

        final FileUploadModel fileUploadModel = getFileUploadModel(tag);

        Log.d("NumParts", fileUploadModel.getParts() + "");


        Call<MultipartUrl> call = ApplicationClass.getInstance().getApiService().getMultipartUrl(Constants.SIGNUPLOADPART,
                fileUploadModel.getKey(),
                fileUploadModel.getParts(), fileUploadModel.getUploadId());

        APIHelper.enqueueWithRetry(fileUploadModel.getTimesInMilli(), call, 2, new Callback<MultipartUrl>() {
            @Override
            public void onResponse(Call<MultipartUrl> call, Response<MultipartUrl> response) {

                List<MultipartUrl.Part> urlList = response.body().getParts();

                Log.d("Url list", urlList.size() + "");

                fileUploadModel.setCompleteUrl(response.body().getComplete());
                fileUploadModel.setAbortUrl(response.body().getAbort());

                List<MutliPartUploadModel> multiPartsList = new ArrayList<>();

                for (int i = 0; i < urlList.size(); i++) {

                    MultipartUrl.Part part = urlList.get(i);

                    MutliPartUploadModel mutliPartUploadModel = new MutliPartUploadModel();
                    mutliPartUploadModel.setUrl(part.getUrl());
                    mutliPartUploadModel.setMultipartUpload(MutliPartUploadModel.MULTIPART_UPLOAD.IN_QUEUE);
                    //mutliPartUploadModel.setPart(fileUploadModel.getMultiParts()[i]);
                    mutliPartUploadModel.setPartNumber(i + 1);

                    //Log.d("size-file", fileUploadModel.getMultiParts()[i].length + "");

                    multiPartsList.add(mutliPartUploadModel);

                    /*try {

                        if (i%5 == 0) {

                            Log.d("Thread", "Sleep");

                            Thread.sleep(1500);
                            System.gc();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                    if (urlList.size() < 20)
                        UploadParts(0, tag, multiPartsList.get(i));

                }

                if (urlList.size() >= 20) {
                    UploadParts(0, tag, multiPartsList.get(0));
                }


                fileUploadModel.setMutliPartUploadList(multiPartsList);
                fileUploadUtility.getInstance().getFileUploadList().set(getIndex(fileUploadModel), fileUploadModel);

            }

            @Override
            public void onFailure(Call<MultipartUrl> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());

            }
        });
    }

    private void UploadParts(final int position, final long tag, final MutliPartUploadModel mutliPartUploadModel) {

        final FileUploadModel fileUploadModel = getFileUploadModel(tag);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        byte[] bytes = Common.getInstance().getFilePart(new File(fileUploadModel.getSelectedImagePath()), mutliPartUploadModel.getPartNumber());

        RequestBody fbody = RequestBody.create(MediaType.parse("image"), bytes);

        System.gc();

        Call<ResponseBody> call = apiService.uploadMultipart(mutliPartUploadModel.getUrl(), fbody);
        call.enqueue(new Callback<ResponseBody>() {

            int pos = position;

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (fileUploadModel.getMutliPartUploadList().size() >= 20) {
                    if (++pos < fileUploadModel.getMutliPartUploadList().size()) {
                        UploadParts(pos, tag, fileUploadModel.getMutliPartUploadList().get(pos));
                    }
                }



                mutliPartUploadModel.setMultipartUpload(MutliPartUploadModel.MULTIPART_UPLOAD.COMPLETED);

                int index = fileUploadModel.getMutliPartUploadList().indexOf(mutliPartUploadModel);

                fileUploadModel.setCompletionCount(fileUploadModel.getCompletionCount() + 1);

                okhttp3.Headers headerList = response.headers();
                Log.d("headers etag", headerList.get("ETag"));

                Log.d("part number => etag", mutliPartUploadModel.getPartNumber() + " " +  headerList.get("ETag"));

                mutliPartUploadModel.setETag(headerList.get("ETag"));

                fileUploadModel.setTotalPartRequestedCount(fileUploadModel.getTotalPartRequestedCount() + 1);



                /*if (multiPartsList.size() == 0) {
                    FileUploadNotification.updateNotification(100, "Sample", "Uploading Completed");
                    progressBar.setVisibility(View.GONE);
                    try {
                        Log.d("XML", createXMLDOM());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getFileLocation();

                }*/

                fileUploadModel.getMutliPartUploadList().set(index, mutliPartUploadModel);

                fileUploadUtility.getFileUploadList().set(getIndex(fileUploadModel), fileUploadModel);

                if (fileUploadModel.getTotalPartRequestedCount() == fileUploadModel.getMutliPartUploadList().size()) {


                    try {
                        Log.d("XML", createXMLDOM(tag));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    getFileLocation(tag);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                mutliPartUploadModel.setMultipartUpload(MutliPartUploadModel.MULTIPART_UPLOAD.FAILURE);
                fileUploadModel.setTotalPartRequestedCount(fileUploadModel.getTotalPartRequestedCount() + 1);

                if (fileUploadModel.getTotalPartRequestedCount() == fileUploadModel.getMutliPartUploadList().size()) {
                    // TODO : // Need to call failure part request.

                    fileUploadModel.setTotalPartRequestedCount(fileUploadModel.getCompletionCount());

                    int abortedCount = 0;

                    for (int i = 0; i < fileUploadModel.getMutliPartUploadList().size(); i++) {

                        MutliPartUploadModel mutliPartUploadModel1 = fileUploadModel.getMutliPartUploadList().get(i);

                        if (mutliPartUploadModel.getMultipartUpload() == MutliPartUploadModel.MULTIPART_UPLOAD.FAILURE){

                            abortedCount++;

                            if (pos != 0)
                                --pos;
                            if (fileUploadModel.getMutliPartUploadList().size() >= 20) {
                                UploadParts(pos, tag, mutliPartUploadModel1);
                            }

                        }

                    }

                    if (abortedCount == 0){
                        Integer tag = (int) (long) fileUploadModel.getTimesInMilli();

                        FileUploadNotification.updateNotification(tag, 100, "Sample", "Uploading Completed");
                        try {
                            Log.d("XML", createXMLDOM(tag));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }

                fileUploadUtility.getFileUploadList().set(getIndex(fileUploadModel), fileUploadModel);

            }
        });

    }

    private String createXMLDOM(long tag) throws Exception {

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        // create root: CompleteMultipartUpload
        Element root = doc.createElement(Constants.COMPLETE_MULITPART_UPLOAD);
        doc.appendChild(root);

        FileUploadModel fileUploadModel = getFileUploadModel(tag);

        //sort by First name
        Collections.sort(fileUploadModel.getMutliPartUploadList(), new Comparator() {
            @Override
            public int compare(Object obj1, Object obj2) {
                MutliPartUploadModel emp1 = (MutliPartUploadModel)obj1;
                MutliPartUploadModel emp2 = (MutliPartUploadModel)obj2;
                return emp1.getPartNumber() - (emp2.getPartNumber());
            }
        });

        for (int i = 0; i < fileUploadModel.getMutliPartUploadList().size(); i++) {

            // create: <Part>
            Element tagPart = doc.createElement(Constants.PART);
            root.appendChild(tagPart);

            // create: <PartNumber>
            Element tagPartNumber = doc.createElement(Constants.PART_NUMBER);
            tagPart.appendChild(tagPartNumber);
            tagPartNumber.setTextContent(fileUploadModel.getMutliPartUploadList().get(i).getPartNumber() + "");

            // create: <PartNumber>
            Element tagETag = doc.createElement(Constants.ETAG);
            tagPart.appendChild(tagETag);
            tagETag.setTextContent(fileUploadModel.getMutliPartUploadList().get(i).getETag() + "");

        }

        // create Transformer object
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(new DOMSource(doc), result);

        // return XML string
        return writer.toString();

    }

    private void getFileLocation(final long tag) {

        final FileUploadModel fileUploadModel = getFileUploadModel(tag);

        ApiInterface apiService =
                ApiClient.getXMLClient().create(ApiInterface.class);

        Call<CompletionMultipartUploadResult> call = null;
        try {
            RequestBody body =
                    RequestBody.create(MediaType.parse("text/plain"), createXMLDOM(fileUploadModel.getTimesInMilli()));

            call = apiService.getRealLocation(fileUploadModel.getCompleteUrl(), body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        call.enqueue(new Callback<CompletionMultipartUploadResult>() {
            @Override
            public void onResponse(Call<CompletionMultipartUploadResult> call, Response<CompletionMultipartUploadResult> response) {

                Log.d("headers Etag", response.body().getLocation());

                Intent intent = new Intent("uploaded");
                // You can also include some extra data.
                intent.putExtra("message", response.body().getLocation());
                LocalBroadcastManager.getInstance(ApplicationClass.getInstance()).sendBroadcast(intent);

                Integer tag = (int) (long) fileUploadModel.getTimesInMilli();

                FileUploadNotification.updateNotification(tag, 100, "Sample", "Uploading Completed");

            }

            @Override
            public void onFailure(Call<CompletionMultipartUploadResult> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                //progressBar.setVisibility(View.GONE);

            }
        });

    }
}
