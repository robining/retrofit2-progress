package com.robining.android.retrofit2.progress;

public interface ProgressListener {
    void onUpProgress(long progress, long total);

    void onDownProgress(long progress, long total, long fixTotal);

    void onExceptionProgress(Throwable ex);
}