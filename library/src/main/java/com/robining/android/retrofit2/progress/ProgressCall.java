package com.robining.android.retrofit2.progress;

import android.os.Handler;
import android.os.Looper;

import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

final class ProgressCall<T> extends EventListener implements Call<T>, ProgressListener {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Call<T> call;
    private ProgressListener progressListener;
    private EventListener eventListener;

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public ProgressCall<T> setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public ProgressCall<T> setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

    ProgressCall(final Call<T> call) {
        this.call = call;
    }

    private void configProgressListener() {
        ProxyProgressListener proxyProgressListener = request().tag(ProxyProgressListener.class);
        if (proxyProgressListener != null) {
            proxyProgressListener.setProgressListener(ProgressCall.this).setEventListener(this);
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
        return new ProgressCall<>(call.clone()).setProgressListener(progressListener).setEventListener(eventListener);
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

    @Override
    public void callStart(final okhttp3.Call call) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.callStart(call);
            }
        });
    }

    @Override
    public void dnsStart(final okhttp3.Call call, final String domainName) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.dnsStart(call, domainName);
            }
        });
    }

    @Override
    public void dnsEnd(final okhttp3.Call call, final String domainName, final List<InetAddress> inetAddressList) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.dnsEnd(call, domainName, inetAddressList);
            }
        });
    }

    @Override
    public void connectStart(final okhttp3.Call call, final InetSocketAddress inetSocketAddress, final Proxy proxy) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.connectStart(call, inetSocketAddress, proxy);
            }
        });
    }

    @Override
    public void secureConnectStart(final okhttp3.Call call) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.secureConnectStart(call);
            }
        });
    }

    @Override
    public void secureConnectEnd(final okhttp3.Call call, final Handshake handshake) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.secureConnectEnd(call, handshake);
            }
        });
    }

    @Override
    public void connectEnd(final okhttp3.Call call, final InetSocketAddress inetSocketAddress, final Proxy proxy, final Protocol protocol) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.connectEnd(call, inetSocketAddress, proxy, protocol);
            }
        });
    }

    @Override
    public void connectFailed(final okhttp3.Call call, final InetSocketAddress inetSocketAddress, final Proxy proxy, final Protocol protocol, final IOException ioe) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
            }
        });

    }

    @Override
    public void connectionAcquired(final okhttp3.Call call, final Connection connection) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.connectionAcquired(call, connection);
            }
        });

    }

    @Override
    public void connectionReleased(final okhttp3.Call call, final Connection connection) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.connectionReleased(call, connection);
            }
        });

    }

    @Override
    public void requestHeadersStart(final okhttp3.Call call) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.requestHeadersStart(call);
            }
        });

    }

    @Override
    public void requestHeadersEnd(final okhttp3.Call call, final Request request) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.requestHeadersEnd(call, request);
            }
        });
    }

    @Override
    public void requestBodyStart(final okhttp3.Call call) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.requestBodyStart(call);
            }
        });
    }

    @Override
    public void requestBodyEnd(final okhttp3.Call call, final long byteCount) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.requestBodyEnd(call, byteCount);
            }
        });
    }

    @Override
    public void responseHeadersStart(final okhttp3.Call call) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.responseHeadersStart(call);
            }
        });
    }

    @Override
    public void responseHeadersEnd(final okhttp3.Call call, final okhttp3.Response response) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.responseHeadersEnd(call, response);
            }
        });
    }

    @Override
    public void responseBodyStart(final okhttp3.Call call) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.responseBodyStart(call);
            }
        });
    }

    @Override
    public void responseBodyEnd(final okhttp3.Call call, final long byteCount) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.responseBodyEnd(call, byteCount);
            }
        });
    }

    @Override
    public void callEnd(final okhttp3.Call call) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.callEnd(call);
            }
        });
    }

    @Override
    public void callFailed(final okhttp3.Call call, final IOException ioe) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (eventListener == null) {
                    return;
                }
                eventListener.callFailed(call, ioe);
            }
        });
    }
}
