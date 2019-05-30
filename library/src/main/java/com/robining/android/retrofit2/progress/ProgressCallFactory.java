package com.robining.android.retrofit2.progress;

import okhttp3.*;
import okio.Timeout;

import java.io.IOException;

public class ProgressCallFactory implements Call.Factory {
    private Call.Factory realFactory;

    public ProgressCallFactory(Call.Factory realFactory) {
        this.realFactory = realFactory;
    }

    public ProgressCallFactory(OkHttpClient client) {
        this.realFactory = client;
    }

    @Override
    public Call newCall(final Request oldRequest) {
        ProxyProgressListener proxyProgressListener = new ProxyProgressListener();
        //必须在此处进行Request加工,因为在内部调用并不会走Call.request()方法，而是直接调用内部变量
        Request.Builder builder = oldRequest.newBuilder().tag(ProxyProgressListener.class, proxyProgressListener);
        Request request = null;
        if (oldRequest.body() == null) {
            if ("GET".equals(oldRequest.method())) {
                request = builder.get().build();
            } else if ("HEAD".equals(oldRequest.method())) {
                request = builder.head().build();
            } else if ("DELETE".equals(oldRequest.method())) {
                request = builder.delete().build();
            }
        }

        if (request == null) {
            request = builder.method(oldRequest.method(), new ProgressRequestBody(oldRequest.body(), proxyProgressListener)).build();
        }

        return new ProxyCall(realFactory.newCall(request), proxyProgressListener);
    }

    private class ProxyCall implements Call {
        private Call realCall;
        private ProxyProgressListener proxyProgressListener;

        ProxyCall(Call realCall, ProxyProgressListener proxyProgressListener) {
            this.realCall = realCall;
            this.proxyProgressListener = proxyProgressListener;
        }

        @Override
        public Request request() {
            return realCall.request();
        }

        @Override
        public Response execute() throws IOException {
            try {
                if (realCall.request().body() == null) {
                    onEmptyRequestBodyProgress();
                }

                Response oldResponse = realCall.execute();
                //对响应进行加工
                return oldResponse.newBuilder().body(new ProgressResponseBody(oldResponse.body(), proxyProgressListener)).build();
            } catch (IOException e) {
                onFailedProgress(realCall.request(), e);
                throw e;
            }
        }

        private void onEmptyRequestBodyProgress() {
            if (proxyProgressListener != null) {
                proxyProgressListener.onUpProgress(1, 1);
            }
        }

        private void onFailedProgress(Request request, Throwable ex) {
            ProxyProgressListener proxyProgressListener = request.tag(ProxyProgressListener.class);
            if (proxyProgressListener != null) {
                proxyProgressListener.onExceptionProgress(ex);
            }
        }

        @Override
        public void enqueue(final Callback responseCallback) {
            if (realCall.request().body() == null) {
                onEmptyRequestBodyProgress();
            }

            realCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onFailedProgress(call.request(), e);
                    responseCallback.onFailure(call, e);
                }

                @Override
                public void onResponse(Call call, Response oldResponse) throws IOException {
                    //对响应进行加工
                    responseCallback.onResponse(call, oldResponse.newBuilder().body(new ProgressResponseBody(oldResponse.body(), proxyProgressListener)).build());
                }
            });
        }

        @Override
        public void cancel() {
            realCall.cancel();
        }

        @Override
        public boolean isExecuted() {
            return realCall.isExecuted();
        }

        @Override
        public boolean isCanceled() {
            return realCall.isCanceled();
        }


        @Override
        public Timeout timeout() {
            return realCall.timeout();
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")

        @Override
        public Call clone() {
            return new ProxyCall(realCall.clone(), proxyProgressListener);
        }
    }
}
