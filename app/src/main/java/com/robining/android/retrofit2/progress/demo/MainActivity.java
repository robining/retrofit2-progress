package com.robining.android.retrofit2.progress.demo;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.robining.android.retrofit2.progress.ProgressCallAdapterFactory;
import com.robining.android.retrofit2.progress.ProgressCallFactory;
import com.robining.android.retrofit2.progress.ProgressListener;
import com.robining.android.retrofit2.progress.demo.api.StringConvertFactory;
import com.robining.android.retrofit2.progress.demo.api.TestService;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarUp = findViewById(R.id.pbProgressUp);
        progressBarDown = findViewById(R.id.pbProgressDown);
        tvResp = findViewById(R.id.tvResp);

        initServices();
    }

    private void initServices() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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
        service.baiduCall().setProgressListener(this)
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
                    final Response<String> response = service.baiduCall().setProgressListener(MainActivity.this)
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
        service.baiduRx().setProgressListener(this)
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
                service.baiduRx().setProgressListener(MainActivity.this)
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
