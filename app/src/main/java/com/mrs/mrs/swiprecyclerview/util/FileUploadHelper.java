package com.mrs.mrs.swiprecyclerview.util;

import android.os.RecoverySystem;

import com.mrs.mrs.swiprecyclerview.okhttp.ProgressRequestBody;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mrs on 2017/5/18.
 */

public class FileUploadHelper {
    static FileUploadHelper helper;
    static OkHttpClient client;

    protected static FileUploadHelper getInstance() {
        if (helper == null) {
            synchronized (helper) {
                if (helper == null) {
                    helper = new FileUploadHelper();
                }
            }

        }
        return helper;
    }


}
