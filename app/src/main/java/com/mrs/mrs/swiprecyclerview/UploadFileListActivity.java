package com.mrs.mrs.swiprecyclerview;

import android.util.Log;
import android.view.MenuItem;

import com.mrs.mrs.multisupportrecyclerview.R;
import com.mrs.mrs.swiprecyclerview.bean.FileEntiy;
import com.mrs.mrs.swiprecyclerview.okhttp.MultiProRequestBody;
import com.mrs.mrs.swiprecyclerview.okhttp.ProgressRequestBody;
import com.mrs.mrs.swiprecyclerview.widget.ProcessImageView;
import com.qiaomu.libmultirecyclerview.multisupport.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mrs on 2017/5/18.
 */

public class UploadFileListActivity extends BaseListActivity<FileEntiy> {
    String url = "http://jchapi.huored.net/client/service.json?authedUserId=8201606224095767&loginKey=JCZJ_LOGIN20170519103528&service=USER_LOGO_EDIT&sign=9e4792cdc4d2286be3fb556a46bb8cc3";
    ArrayList<FileEntiy> picPaths = new ArrayList<>();
    public static String[] pics = new String[]{
            "storage/emulated/0/download/header_1.gif",
            "storage/emulated/0/download/header_2.gif",
            "storage/emulated/0/download/3.gif",
            "storage/emulated/0/download/4.gif",
            "storage/emulated/0/download/5.gif",
            "storage/emulated/0/download/6.jpg",
            "storage/emulated/0/download/7.gif",
            "storage/emulated/0/download/8.gif",
            "storage/emulated/0/download/9.gif",
            "storage/emulated/0/download/10.gif",
            "storage/emulated/0/download/11.gif",
            "storage/emulated/0/download/12.jpeg"
    };
    private OkHttpClient client;

    @Override
    protected void onSetUpView() {
        setUpToolbar(R.string.file_upload, R.menu.add);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS);

        client = builder
                .addInterceptor(new signingInterceptor())
                .addNetworkInterceptor(new progressInterceptor())
                .build();
    }

    private class signingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("appName", "jczj-android")
                    .addHeader("appVersion", "header_1.header_1.8")
                    .addHeader("appUserAgent", "header_1")
                    .build();
            return chain.proceed(request);
        }
    }


    private class progressInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            //包装响应体并返回
            return originalResponse.newBuilder()
                    .body(new MultiProRequestBody(originalResponse.body(), new MultiProgressListener()))
                    .build();
        }
    }

    @Override
    public int getItemLayoutRes() {
        return R.layout.list_item_img;
    }

    @Override
    public void onRefresh(boolean isRefresh) {

    }

    @Override
    public void onBindHolder(ViewHolder holder, FileEntiy item, int position) {
        ProcessImageView piv = holder.getView(R.id.proimg);
        Log.e("onBindHolder: ", item.progress + "--");
        piv.setProgress(item.progress);
       // holder.setImageByUrl(this, R.id.proimg, item.path);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
//        String pic = pics[5];
//        String pic1 = pics[11];
//        picPaths.add(new FileEntiy(pic));
//        picPaths.add(new FileEntiy(pic1));
//        picPaths.add(new FileEntiy(pic));
//        picPaths.add(new FileEntiy(pic1));
//        File file = new File(pic);
//        File file1 = new File(pic1);
//
//        RequestBody requestBody = new ProgressRequestBody(file, 0, new ProgressListener());
//        RequestBody requestBody1 = new ProgressRequestBody(file1, header_1, new ProgressListener());
//        RequestBody requestBody2 = new ProgressRequestBody(file, header_2, new ProgressListener());
//        RequestBody requestBody3 = new ProgressRequestBody(file1, 3, new ProgressListener());
//        MultipartBody body = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addPart(requestBody)
//                .addPart(requestBody1)
//                .addPart(requestBody2)
//                .addPart(requestBody3)
//                .build();
        Request request = new Request.Builder()
                //下面图片的网址是在百度图片随便找的
                .url("http://img.ivsky.com/img/bizhi/pre/201705/31/dream_of_three_ancient_kingdoms_2.jpg")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                int posInAdapter = ((MultipartBody) call.request().body()).size();
                Log.e("onFailure: ", e.toString() + "--" + posInAdapter);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("onResponse: ", response.body().string());
            }
        });

        loadCompleted(picPaths);
        return true;
    }


    private class ProgressListener implements ProgressRequestBody.ProgressListener {

        @Override
        public void update(long totalLenth, long progress, boolean ok, int posInAdapter) {
            Log.e("update: ", (progress * 100 / totalLenth) + "--" + posInAdapter);
            mDatas.get(posInAdapter).progress = (progress * 100 / totalLenth);
            mAdapter.notifyItemChanged(posInAdapter);

        }
    }


    private class MultiProgressListener implements MultiProRequestBody.ProgressListener {

        @Override
        public void onProgress(long totalBytesRead, long totalLength, boolean ok) {
            Log.e("update-Multi: ", totalBytesRead + "--" + totalLength + "--" + ok);
        }
    }
}
