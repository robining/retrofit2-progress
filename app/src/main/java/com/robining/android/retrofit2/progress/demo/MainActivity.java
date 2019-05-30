package com.robining.android.retrofit2.progress.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.robining.android.retrofit2.progress.ProgressCallAdapterFactory;
import com.robining.android.retrofit2.progress.ProgressCallFactory;
import com.robining.android.retrofit2.progress.ProgressEventListenerFactory;
import com.robining.android.retrofit2.progress.ProgressListener;
import com.robining.android.retrofit2.progress.ProxyEventListener;
import com.robining.android.retrofit2.progress.StatisticEventListener;
import com.robining.android.retrofit2.progress.demo.api.StringConvertFactory;
import com.robining.android.retrofit2.progress.demo.api.TestService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ProgressListener {
    TestService service;

    ProgressBar progressBarUp, progressBarDown;
    TextView tvResp;
    private EventListener eventListener = new ProxyEventListener(new StatisticEventListener()) {
        private void printInfo(okhttp3.Call call) {
            System.out.println(">>>" + Thread.currentThread().getStackTrace()[3].getMethodName());
        }

        @Override
        public void callStart(okhttp3.Call call) {
            super.callStart(call);
            printInfo(call);
        }

        @Override
        public void dnsStart(okhttp3.Call call, String domainName) {
            super.dnsStart(call, domainName);
            printInfo(call);
        }

        @Override
        public void dnsEnd(okhttp3.Call call, String domainName, List<InetAddress> inetAddressList) {
            super.dnsEnd(call, domainName, inetAddressList);
            printInfo(call);
        }

        @Override
        public void connectStart(okhttp3.Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
            super.connectStart(call, inetSocketAddress, proxy);
            printInfo(call);
        }

        @Override
        public void secureConnectStart(okhttp3.Call call) {
            super.secureConnectStart(call);
            printInfo(call);
        }

        @Override
        public void secureConnectEnd(okhttp3.Call call, Handshake handshake) {
            super.secureConnectEnd(call, handshake);
            printInfo(call);
        }

        @Override
        public void connectEnd(okhttp3.Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
            super.connectEnd(call, inetSocketAddress, proxy, protocol);
            printInfo(call);
        }

        @Override
        public void connectFailed(okhttp3.Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
            super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
            printInfo(call);
        }

        @Override
        public void connectionAcquired(okhttp3.Call call, Connection connection) {
            super.connectionAcquired(call, connection);
            printInfo(call);
        }

        @Override
        public void connectionReleased(okhttp3.Call call, Connection connection) {
            super.connectionReleased(call, connection);
            printInfo(call);
        }

        @Override
        public void requestHeadersStart(okhttp3.Call call) {
            super.requestHeadersStart(call);
            printInfo(call);
        }

        @Override
        public void requestHeadersEnd(okhttp3.Call call, Request request) {
            super.requestHeadersEnd(call, request);
            printInfo(call);
        }

        @Override
        public void requestBodyStart(okhttp3.Call call) {
            super.requestBodyStart(call);
            printInfo(call);
        }

        @Override
        public void requestBodyEnd(okhttp3.Call call, long byteCount) {
            super.requestBodyEnd(call, byteCount);
            printInfo(call);
        }

        @Override
        public void responseHeadersStart(okhttp3.Call call) {
            super.responseHeadersStart(call);
            printInfo(call);
        }

        @Override
        public void responseHeadersEnd(okhttp3.Call call, okhttp3.Response response) {
            super.responseHeadersEnd(call, response);
            printInfo(call);
        }

        @Override
        public void responseBodyStart(okhttp3.Call call) {
            super.responseBodyStart(call);
            printInfo(call);
        }

        @Override
        public void responseBodyEnd(okhttp3.Call call, long byteCount) {
            super.responseBodyEnd(call, byteCount);
            printInfo(call);
        }

        @Override
        public void callEnd(okhttp3.Call call) {
            super.callEnd(call);
            printInfo(call);
        }

        @Override
        public void callFailed(okhttp3.Call call, IOException ioe) {
            super.callFailed(call, ioe);
            printInfo(call);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarUp = findViewById(R.id.pbProgressUp);
        progressBarDown = findViewById(R.id.pbProgressDown);
        tvResp = findViewById(R.id.tvResp);
        eventListener = EventListener.NONE;
        initServices();
    }

    private void initServices() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .eventListenerFactory(new ProgressEventListenerFactory())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost/") //这里没有使用，因为在Service里面配置的是全路径
                .callFactory(new ProgressCallFactory(okHttpClient))
                .addCallAdapterFactory(new ProgressCallAdapterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(new StringConvertFactory())
                .build();

        service = retrofit.create(TestService.class);
    }

    public void retrofitAsync(View view) {
        resetProgress();
        tvResp.setText("正在使用Retrofit Call方式异步请求数据\n");
        service.baiduCall().setProgressListener(this).setEventListener(eventListener)
                .getCall()
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            tvResp.append(response.body() + "\n");
                        } else {
                            tvResp.append("请求失败:" + response.code() + "\n");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        tvResp.append("请求失败:" + t.getMessage() + "\n");
                    }
                });
    }

    public void retrofitSync(View view) {
        resetProgress();
        tvResp.setText("正在使用Retrofit Call方式同步请求数据\n");
        new Thread() {
            @Override
            public void run() {
                try {
                    final Response<String> response = service.baiduCall().setProgressListener(MainActivity.this).setEventListener(eventListener)
                            .getCall().execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (response.isSuccessful()) {
                                tvResp.append(response.body() + "\n");
                            } else {
                                tvResp.append("请求失败:" + response.code() + "\n");
                            }
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResp.append("请求失败:" + e.getMessage() + "\n");
                        }
                    });
                }
            }
        }.start();
    }

    public void rxJavaAsync(View view) {
        resetProgress();
        tvResp.setText("正在使用RxJava方式异步请求数据\n");
        service.baiduRx().setProgressListener(this).setEventListener(eventListener)
                .getCall()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        tvResp.append(s + "\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvResp.append("请求失败:" + e.getMessage() + "\n");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        System.out.println("-------------------test1-----------------");
    }

    public void rxJavaSync(View view) {
        resetProgress();
        tvResp.setText("正在使用RxJava方式同步请求数据\n");
        new Thread() {
            @Override
            public void run() {
                service.baiduRx().setProgressListener(MainActivity.this).setEventListener(eventListener)
                        .getCall()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .blockingSubscribe(new Consumer<String>() {
                            @Override
                            public void accept(final String s) throws Exception {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvResp.append(s + "\n");
                                    }
                                });
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(final Throwable throwable) throws Exception {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvResp.append("请求失败:" + throwable.getMessage() + "\n");
                                    }
                                });
                            }
                        });

                System.out.println("-------------------test1-----------------");
            }
        }.start();
    }

    private void resetProgress() {
        progressBarUp.setProgress(0);
        progressBarDown.setProgress(0);
    }

    @Override
    public void onUpProgress(long progress, long total) {
        System.out.println(">>>receive up:" + progress + "/" + total);
        progressBarUp.setMax((int) total);
        progressBarUp.setProgress((int) progress);
    }

    @Override
    public void onDownProgress(long progress, long total, long fixTotal) {
        System.out.println(">>>receive down:" + progress + "/" + total + "/" + fixTotal);
        progressBarDown.setMax((int) fixTotal);
        progressBarDown.setProgress((int) progress);

        System.out.println(">>>");
    }

    @Override
    public void onExceptionProgress(Throwable ex) {
        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
