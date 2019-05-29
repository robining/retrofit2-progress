package com.robining.android.retrofit2.progress;

public class ProxyProgressListener implements ProgressListener {
    private ProgressListener progressListener;

    @Override
    public void onUpProgress(long progress, long total) {
        if (progressListener != null) {
            progressListener.onUpProgress(progress, total);
        }
    }

    @Override
    public void onDownProgress(long progress, long total, long fixTotal) {
        if (progressListener != null) {
            progressListener.onDownProgress(progress, total, fixTotal);
        }
    }

    @Override
    public void onExceptionProgress(Throwable ex) {
        if (progressListener != null) {
            progressListener.onExceptionProgress(ex);
        }
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public ProxyProgressListener setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }
}
