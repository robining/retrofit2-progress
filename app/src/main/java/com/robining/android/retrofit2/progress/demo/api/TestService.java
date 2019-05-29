package com.robining.android.retrofit2.progress.demo.api;

import com.robining.android.retrofit2.progress.ProgressWrapper;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TestService {
    @GET("https://juejin.im/entry/58aba6d6ac502e007e87cac9")
    ProgressWrapper<Observable<String>> baiduRx();

    @GET("https://www.cnblogs.com/zdz8207/p/Android-28-http-https.html")
    ProgressWrapper<Call<String>> baiduCall();
}
