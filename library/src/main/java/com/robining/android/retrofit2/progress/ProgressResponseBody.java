package com.robining.android.retrofit2.progress;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

import java.io.IOException;

public class ProgressResponseBody extends ResponseBody {
    private ResponseBody realBody;
    private ProgressListener progressListener;

    public ProgressResponseBody(ResponseBody realBody, ProgressListener progressListener) {
        this.realBody = realBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return realBody == null ? null : realBody.contentType();
    }

    @Override
    public long contentLength() {
        return realBody == null ? -1 : realBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        return realBody == null ? null : Okio.buffer(new ForwardingSource(realBody.source()) {
            private long readCount = 0;
            private long totalCount = contentLength();//当响应头没有Content-Length的时候为-1
            private boolean unKnownTotalCount = totalCount == -1;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                try {
                    long read = super.read(sink, byteCount);
                    readCount += read == -1 ? 0 : read;

                    long fixTotalCount = unKnownTotalCount ? (readCount + sink.size()) : totalCount;
                    tryNotifyProgress(readCount, totalCount, fixTotalCount);
                    return read;
                } catch (IOException e) {
                    tryNotifyException(e);
                    throw e;
                }
            }
        });
    }

    private void tryNotifyException(Throwable ex) {
        if (progressListener != null) {
            progressListener.onExceptionProgress(ex);
        }
    }

    private void tryNotifyProgress(long progress, long total, long fixTotal) {
        if (progressListener != null) {
            progressListener.onDownProgress(progress, total, fixTotal);
        }
    }
}