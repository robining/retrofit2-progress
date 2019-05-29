package com.robining.android.retrofit2.progress;

import android.os.Handler;
import android.os.Looper;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

final class ProgressCall<T> implements Call<T>, ProgressListener {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Call<T> call;
    private ProgressListener progressListener;

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    ProgressCall<T> setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    ProgressCall(final Call<T> call) {
        this.call = call;
    }

    private void configProgressListener() {
        ProxyProgressListener proxyProgressListener = request().tag(ProxyProgressListener.class);
        if (proxyProgressListener != null) {
            proxyProgressListener.setProgressListener(ProgressCall.this);
        }
    }

    @Override
    public Response<T> execute() throws IOException {
        configProgressListener(); //必须先调用request()方法生成OkHttp类型的Request
        return call.execute();
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        configProgressListener(); //必须先调用request()方法生成OkHttp类型的Request
        this.call.enqueue(callback);
    }

    @Override
    public boolean isExecuted() {
        return call.isExecuted();
    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @Override
    public boolean isCanceled() {
        return call.isCanceled();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Call<T> clone() {
        return new ProgressCall<>(call.clone()).setProgressListener(progressListener);
    }

    @Override
    public Request request() {
        return call.request();
    }

    @Override
    public void onUpProgress(final long progress, final long total) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (progressListener != null) {
                    progressListener.onUpProgress(progress, total);
                }
            }
        });

    }

    @Override
    public void onDownProgress(final long progress, final long total, final long fixTotal) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (progressListener != null) {
                    progressListener.onDownProgress(progress, total, fixTotal);
                }
            }
        });
    }

    @Override
    public void onExceptionProgress(final Throwable ex) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (progressListener != null) {
                    progressListener.onExceptionProgress(ex);
                }
            }
        });
    }
}
