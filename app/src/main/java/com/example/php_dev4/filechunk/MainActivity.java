package com.example.php_dev4.filechunk;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.php_dev4.restapi.FileUploadService;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSelect;

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;

    TextView txtChunkSize = null;

    private ImageView ivParts, ivImage;

    private Common common;

    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSelect = findViewById(R.id.btnSelect);
        txtChunkSize = findViewById(R.id.txtChunkSize);
        ivParts = findViewById(R.id.ivParts);
        ivImage = findViewById(R.id.ivImage);
        progressBar = findViewById(R.id.progressBar);

        btnSelect.setOnClickListener(this);

        common = Common.getInstance();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("uploaded"));
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSelect:
                common.selectImage(this, SELECT_PICTURE);
                break;
        }

    }

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedImagePath = cursor.getString(columnIndex);
                cursor.close();

                final File file = new File(selectedImagePath);
                // try {

                //int parts = common.divideArray(common.fullyReadFileToBytes(wallpaperDirectory), 5 * 1024 * 1024).length;


                String fileName = "/TestUpload" + selectedImagePath.substring(selectedImagePath.lastIndexOf("."));

                FileUploadModel fileUploadModel = new FileUploadModel();
                fileUploadModel.setFileName(fileName);
                fileUploadModel.setSelectedImagePath(selectedImagePath);
                fileUploadModel.setTimesInMilli(System.currentTimeMillis());

                Log.d("File-size", ((int) Math.ceil(Math.nextUp((int) (file.length() / (1024 * 1024))))) + "");
                Log.d("Part-size", (int) Math.ceil(Math.nextUp((int) (file.length() / (1024 * 1024)) / 5)) + "");

                int totalParts = (int) Math.ceil(Math.nextUp((int) (file.length() / (1024 * 1024)) / 5));

                fileUploadModel.setParts(totalParts);

                Log.d("Num-Part-size",  fileUploadModel.getParts() + "");

                //common.toByteArray(wallpaperDirectory, size);

                //Log.d("Bytes array Size", common.fullyReadFileToBytes(wallpaperDirectory).size() + "");

                                /*fileUploadModel.setMultiParts(common.divideArray(common.fullyReadFileToBytes(wallpaperDirectory),
                                        5 * 1024 * 1024));*/

                /*Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                view.setImageBitmap(bitmap);*/

                //fileUploadModel.setParts(fileUploadModel.getMultiParts().length);

                setImageViewWithByteArray(fileUploadModel);
            }

                    /*txtChunkSize.setText("File is divided into " +
                            fileUploadModel.getParts() + " Parts");

                    setImageViewWithByteArray(fileUploadModel);*/

                /*} catch (IOException e) {
                    e.printStackTrace();
                }*/

        }
    }


    public void setImageViewWithByteArray(FileUploadModel fileUploadModel) {

        FileUploadUtility.getInstance().getFileUploadList().add(fileUploadModel);

        progressBar.setVisibility(View.VISIBLE);

        /*Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        view.setImageBitmap(bitmap);*/

        //String command = Constants.CREATE_MULTIPART_UPLOAD;

        //JSONObject fileInfo = new JSONObject();
        //common.getBasicUrl(this, command, fileUploadModel.getFileName(), fileUploadModel.getParts(), progressBar);

        Integer tag = (int) (long) fileUploadModel.getTimesInMilli();

        new FileUploadNotification(this, tag);

        Intent intent = new Intent(this, FileUploadService.class);

        intent.putExtra(Constants.TAG, fileUploadModel.getTimesInMilli());

        startService(intent);

    }

    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    protected void onResume() {
        super.onResume();

        IntentFilter iff = new IntentFilter("uploaded");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, iff);
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            progressBar.setVisibility(View.GONE);

            String message = intent.getStringExtra("message");
            txtChunkSize.setText(txtChunkSize.getText().toString() + "\n\n" + message);
            Log.d("receiver", "Got message: " + message);
        }
    };
}