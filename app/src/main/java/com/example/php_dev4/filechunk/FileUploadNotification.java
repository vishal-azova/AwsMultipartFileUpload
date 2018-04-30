package com.example.php_dev4.filechunk;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class FileUploadNotification {
    public static NotificationManager mNotificationManager;
    static NotificationCompat.Builder builder;
    static Context context;
    static int NOTIFICATION_ID = 111;
    static FileUploadNotification fileUploadNotification;

    /*public static FileUploadNotification createInsance(Context context) {
        if(fileUploadNotification == null)
            fileUploadNotification = new FileUploadNotification(context);

        return fileUploadNotification;
    }*/
    public FileUploadNotification(Context context, int notificationId) {
        mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("started uploading...")
                .setContentText("Sample")
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setProgress(0, 0, true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(false);

        //Send the notification:
        Notification notification = builder.build();
        mNotificationManager.notify(notificationId, builder.build());
    }

    public static void updateNotification(int tag, int percent, String fileName, String contentText) {
        try {
            builder.setContentText(contentText)
                    .setContentTitle(fileName)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true)
                    .setContentInfo(percent + "%")
                    .setProgress(0, 0, false);


            mNotificationManager.notify(tag, builder.build());
            /*if (Integer.parseInt(percent) == 100)
                deleteNotification();*/

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("Error...Notification.", e.getMessage() + ".....");
            e.printStackTrace();
        }
    }

    public static void failUploadNotification(/*int percentage, String fileName*/) {
        Log.e("downloadsize", "failed notification...");

        if (builder != null) {
            /* if (percentage < 100) {*/
            builder.setContentText("Uploading Failed")
                    //.setContentTitle(fileName)
                    .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                    .setOngoing(false);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        /*} else {
            mNotificationManager.cancel(NOTIFICATION_ID);
            builder = null;
        }*/
        } else {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }

    public static void deleteNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
        builder = null;
    }
}