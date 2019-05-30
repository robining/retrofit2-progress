package com.robining.android.retrofit2.progress;

import okhttp3.Call;
import okhttp3.EventListener;

public class ProgressEventListenerFactory implements EventListener.Factory {
    @Override
    public EventListener create(Call call) {
        return call.request().tag(ProxyProgressListener.class);
    }
}
