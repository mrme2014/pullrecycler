package com.mrs.mrs.swiprecyclerview.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by mrs on 2017/5/18.
 */

public class ProgressRequestBody extends RequestBody {

    private int posInAdapter;
    private final ProgressListener progressListener;
    private File mFile;
    private Handler uiHandler;
    private Long remaining;

    public ProgressRequestBody(File uploadFile, int posInAdapter, ProgressListener progressListener) {
        this.mFile = uploadFile;
        this.posInAdapter = posInAdapter;
        this.progressListener = progressListener;

        if (progressListener != null) {
            uiHandler = new Handler(Looper.getMainLooper());
        }
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("image/png");
    }

    @Override
    public long contentLength() {
        remaining = mFile.length();
        return remaining;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source;
        try {
            source = Okio.source(mFile);
            //sink.writeAll(source);
            Buffer buf = new Buffer();

            for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                sink.write(buf, readCount);
                remaining -= readCount;
                Log.e("writeTo: ", contentLength() + "--" + remaining + "---" + (remaining == 0));
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.update(contentLength(), remaining, remaining == 0, posInAdapter);
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ProgressListener {
        void update(long totalLenth, long progress, boolean ok, int posInAdapter);
    }
}