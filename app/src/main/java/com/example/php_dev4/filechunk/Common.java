package com.example.php_dev4.filechunk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.php_dev4.restapi.ApiClient;
import com.example.php_dev4.restapi.ApiInterface;
import com.example.php_dev4.restapi.BasicUrl;
import com.example.php_dev4.restapi.CompletionMultipartUploadResult;
import com.example.php_dev4.restapi.InitiateMultipartUploadResult;
import com.example.php_dev4.restapi.MultipartUrl;
import com.example.php_dev4.restapi.MutliPartUploadModel;
import com.example.php_dev4.restapi.PartRead;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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


public class Common {

    public static Common common;

    private String TAG = "API Request", key = "", url = "", uploadId = "", completeUrl = "", abortUrl = "";

    private ProgressBar progressBar;

    private List<MutliPartUploadModel> multiPartsList = null;
    private List<MutliPartUploadModel> eTagList = null;
    private FileUploadNotification fileUploadNotification;

    public byte[][] multiParts = null;

    public static Common getInstance() {

        if (common == null)
            common = new Common();

        return common;
    }

    public void selectImage(Activity context, int tag) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        //getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        context.startActivityForResult(chooserIntent, tag);

    }

    public byte[][] toByteArray(File file, int byteBlockSize) throws IOException {

        InputStream in = new FileInputStream(file);
        long noOfBlocks = (long) Math.ceil((double)file.length() / (double)byteBlockSize);
        byte[][] result = new byte[(int)noOfBlocks][byteBlockSize];
        int offset = 0;
        for(int i = 0; i < result.length; i++) {
            readByteBlock(in, offset, byteBlockSize);
        }
        return result;
    }


    private byte[] readByteBlock(InputStream in, int offset, int noBytes) throws IOException {
        byte[] result = new byte[noBytes];
        in.read(result, offset, noBytes);
        return result;
    }


    public byte[] getFilePart(File file, int partNumber) {

        int fileSizeInMb = (int)Math.ceil(Math.nextUp((int) (file.length() / (1024 * 1024))));
        int byteToRead = 0;
        int read = 0;

        byte[] bytes = new byte[Constants.CHUNK_SIZE_BYTES];

        try {

            InputStream fis = new FileInputStream(file);

            for (int i = 1; i <= (int)Math.ceil(Math.nextUp(fileSizeInMb / Constants.CHUNK_SIZE_MB)); i++) {

                if ((file.length() / i) < Constants.CHUNK_SIZE_MB) {
                    byteToRead = (int) (Constants.CHUNK_SIZE_MB - (file.length() / i));
                } else {
                    byteToRead = Constants.CHUNK_SIZE_MB;
                }

                if (i == (partNumber + 1)) {

                    Log.d("Index ==>", "Read Begin Index => " + read);

                    read = read + fis.read(bytes, 0 , byteToRead);

                    Log.d("Index ==>", "Read End Index => " + read);

                    return bytes;
                } else
                    read = read + fis.read(bytes, 0 , byteToRead);


            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    List<PartRead> fullyReadFileToBytes(File file) throws IOException {

        //int size = (int) file.length();

        int sizeInMb = (int)Math.ceil(Math.nextUp((int) (file.length() / (1024 * 1024))));

        int mbSize = 5 * 1024 * 1024;

        Log.d("Size", Math.round(sizeInMb / 5) + "");


        List<PartRead> parts = new ArrayList<>();
        InputStream fis = new FileInputStream(file);

        int read = 0;
        int byteToRead = 0;

        for (int i = 1; i <= (int)Math.ceil(Math.nextUp(sizeInMb / 5)); i++) {

            byte bytes[] = new byte[mbSize];

            if ((file.length() / i) < mbSize) {
                byteToRead = (int) (mbSize - (file.length() / i));
            } else {
                byteToRead = mbSize;
            }

            PartRead partRead = new PartRead();

            partRead.setStartReadIndex(read);

            read = read + fis.read(bytes, 0 , byteToRead);

            partRead.setEndReadIndex(read);

            partRead.setPartNumber(i);

            Log.d("Index ==>", "Read Index => " + partRead.getStartReadIndex() + " \n " +
                    "End Index => " + partRead.getEndReadIndex() + " Part Number ==> " + partRead.getPartNumber());

            System.gc();

            parts.add(partRead);

        }

        /*byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(file);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (OutOfMemoryError ex) {
            System.gc();                // clean up largeVar data
            //isOutOfMemory = true;       // flag available for use
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }*/

        return parts;
    }

    public static byte[][] divideArray(byte[] source, int chunksize) {


        byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunksize)][chunksize];

        int start = 0;

        int parts = 0;


        for (int i = 0; i < ret.length; i++) {
            if (start + chunksize > source.length) {
                System.arraycopy(source, start, ret[i], 0, source.length - start);
            } else {
                System.arraycopy(source, start, ret[i], 0, chunksize);
            }
            start += chunksize;
            parts++;
        }

        Log.d("Parts", parts + "");

        //Common.getInstance().multiParts = ret;
        return ret;
    }


    /**
     * Getting basic upload url in S3 Amazon
     * This url from S3 provided by our backend services
     *
     * @param command
     * @param fileName
     * @param progressBar
     */
    public void getBasicUrl(final Context context, String command, String fileName, final int numParts, final ProgressBar progressBar) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        fileUploadNotification = new FileUploadNotification(context, 101);

        this.progressBar = progressBar;

        Call<BasicUrl> call = apiService.getBasicUrl(command, fileName);
        call.enqueue(new Callback<BasicUrl>() {
            @Override
            public void onResponse(Call<BasicUrl> call, Response<BasicUrl> response) {

                key = response.body().getKey();

                getUploadId(context, response.body().getUrl(), numParts);
            }

            @Override
            public void onFailure(Call<BasicUrl> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.toString());
            }
        });
    }

    /**
     * Use for getting upload id
     * Passing Basic Url and key which are getting from Our api response.
     *
     * @param url
     */
    public void getUploadId(final Context context, String url, final int numParts) {

        ApiInterface apiService =
                ApiClient.getXMLClient().create(ApiInterface.class);

        Call<InitiateMultipartUploadResult> call = apiService.getUploadIds(url);
        call.enqueue(new Callback<InitiateMultipartUploadResult>() {
            @Override
            public void onResponse(Call<InitiateMultipartUploadResult> call, Response<InitiateMultipartUploadResult> response) {
                Log.d(TAG, "Upload Id : " + response.body().getUploadId());

                uploadId = response.body().getUploadId();

                getMultipartUrls(context, numParts);
            }

            @Override
            public void onFailure(Call<InitiateMultipartUploadResult> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    public void getMultipartUrls(final Context context, int numParts) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MultipartUrl> call = apiService.getMultipartUrl(Constants.SIGNUPLOADPART, key, numParts, uploadId);
        call.enqueue(new Callback<MultipartUrl>() {
            @Override
            public void onResponse(Call<MultipartUrl> call, Response<MultipartUrl> response) {

                List<MultipartUrl.Part> urlList = response.body().getParts();

                completeUrl = response.body().getComplete();
                abortUrl = response.body().getAbort();

                multiPartsList = new ArrayList<>();

                for (int i = 0; i < urlList.size(); i++) {

                    MultipartUrl.Part part = urlList.get(i);

                    MutliPartUploadModel mutliPartUploadModel = new MutliPartUploadModel();
                    mutliPartUploadModel.setUrl(part.getUrl());
                    mutliPartUploadModel.setMultipartUpload(MutliPartUploadModel.MULTIPART_UPLOAD.IN_QUEUE);
                    //mutliPartUploadModel.setPart(multiParts[i]);
                    mutliPartUploadModel.setPartNumber(i + 1);

                    Log.d("size-file", multiParts[i].length + "");

                    multiPartsList.add(mutliPartUploadModel);

                    UploadParts(multiPartsList.get(i));

                }

                if (multiPartsList.size() > 0) {
                    eTagList = new ArrayList<>();

                    //fileUploadNotification.updateNotification("100", "temp", "Uploading ......");
                }

                Log.d(TAG, "Total Urls : " + multiPartsList.size());


            }

            @Override
            public void onFailure(Call<MultipartUrl> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void UploadParts(final MutliPartUploadModel mutliPartUploadModel) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        this.progressBar = progressBar;

        RequestBody fbody = RequestBody.create(MediaType.parse("image"),  "" /*mutliPartUploadModel.getPart()*/);

        Call<ResponseBody> call = apiService.uploadMultipart(mutliPartUploadModel.getUrl(), fbody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                mutliPartUploadModel.setMultipartUpload(MutliPartUploadModel.MULTIPART_UPLOAD.COMPLETED);

                multiPartsList.remove(mutliPartUploadModel);

                okhttp3.Headers headerList = response.headers();
                Log.d("headers etag", headerList.get("ETag"));

                mutliPartUploadModel.setETag(headerList.get("ETag"));
                eTagList.add(mutliPartUploadModel);

                if (multiPartsList.size() == 0) {
                    FileUploadNotification.updateNotification(101, 100, "Sample", "Uploading Completed");
                    progressBar.setVisibility(View.GONE);
                    try {
                        Log.d("XML", createXMLDOM());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getFileLocation();

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.toString());
            }
        });

    }

    private void getFileLocation() {

        ApiInterface apiService =
                ApiClient.getXMLClient().create(ApiInterface.class);

        Call<CompletionMultipartUploadResult> call = null;
        try {
            RequestBody body =
                    RequestBody.create(MediaType.parse("text/plain"), createXMLDOM());

            call = apiService.getRealLocation(completeUrl, body);
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
            }

            @Override
            public void onFailure(Call<CompletionMultipartUploadResult> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    private String createXMLDOM() throws Exception {

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        // create root: CompleteMultipartUpload
        Element root = doc.createElement(Constants.COMPLETE_MULITPART_UPLOAD);
        doc.appendChild(root);

        //sort by First name
        Collections.sort(eTagList, new Comparator() {
            @Override
            public int compare(Object obj1, Object obj2) {
                MutliPartUploadModel emp1 = (MutliPartUploadModel) obj1;
                MutliPartUploadModel emp2 = (MutliPartUploadModel) obj2;
                return emp1.getPartNumber() - (emp2.getPartNumber());
            }
        });

        for (int i = 0; i < eTagList.size(); i++) {

            // create: <Part>
            Element tagPart = doc.createElement(Constants.PART);
            root.appendChild(tagPart);

            // create: <PartNumber>
            Element tagPartNumber = doc.createElement(Constants.PART_NUMBER);
            tagPart.appendChild(tagPartNumber);
            tagPartNumber.setTextContent(eTagList.get(i).getPartNumber() + "");

            // create: <PartNumber>
            Element tagETag = doc.createElement(Constants.ETAG);
            tagPart.appendChild(tagETag);
            tagETag.setTextContent(eTagList.get(i).getETag() + "");

        }

        // create Transformer object
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(new DOMSource(doc), result);

        // return XML string
        return writer.toString();

    }

}
