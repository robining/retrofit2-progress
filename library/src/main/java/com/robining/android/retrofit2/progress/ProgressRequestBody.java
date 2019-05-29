package com.robining.android.retrofit2.progress;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

import java.io.IOException;

public class ProgressRequestBody extends RequestBody {
    private RequestBody realBody;
    private ProgressListener progressListener;

    public ProgressRequestBody(RequestBody realBody, ProgressListener progressListener) {
        this.realBody = realBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return realBody == null ? null : realBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try {
            BufferedSink proxyBufferedSink = Okio.buffer(new ForwardingSink(sink) {
                private long writedCount = 0;
                private long totalCount = contentLength();

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    writedCount += byteCount == -1 ? 0 : byteCount;
                    tryNotifyProgress(writedCount, totalCount);
                }
            });
            if (realBody != null) {
                realBody.writeTo(proxyBufferedSink);
                proxyBufferedSink.flush(); //必须加 不然要报错
            }
        } catch (IOException e) {
            tryNotifyException(e);
            throw e;
        }
    }

    @Override
    public long contentLength() throws IOException {
        return realBody == null ? super.contentLength() : realBody.contentLength();
    }

    private void tryNotifyException(Throwable ex) {
        if (progressListener != null) {
            progressListener.onExceptionProgress(ex);
        }
    }

    private void tryNotifyProgress(long progress, long total) {
        if (progressListener != null) {
            progressListener.onUpProgress(progress, total);
        }
    }
}