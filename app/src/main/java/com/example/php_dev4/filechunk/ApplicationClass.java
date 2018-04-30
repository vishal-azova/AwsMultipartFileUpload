package com.example.php_dev4.filechunk;

import android.app.Application;
import com.example.php_dev4.restapi.ApiClient;
import com.example.php_dev4.restapi.ApiInterface;

public class ApplicationClass extends Application {

    static ApplicationClass applicationClass;

    public ApiInterface apiService = null;

    public static ApplicationClass getInstance() {

        if (applicationClass == null) {

            applicationClass = new ApplicationClass();

        }

        return applicationClass;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        applicationClass = this;


    }

    public ApiInterface getApiService() {
        if (apiService == null)
            apiService = ApiClient.getClient().create(ApiInterface.class);

        return apiService;
    }

    public void setApiService(ApiInterface apiService) {
        this.apiService = apiService;
    }

}