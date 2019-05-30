package com.robining.android.retrofit2.progress;


import okhttp3.EventListener;

public class ProgressWrapper<R> {
    private ProgressCall progressCall;
    private R call;

    ProgressWrapper(ProgressCall progressCall, R call) {
        this.progressCall = progressCall;
        this.call = call;
    }

    public R getCall() {
        return call;
    }

    public ProgressWrapper<R> setProgressListener(ProgressListener listener) {
        progressCall.setProgressListener(listener);
        return this;
    }

    public ProgressWrapper<R> setEventListener(EventListener listener) {
        progressCall.setEventListener(listener);
        return this;
    }
}
