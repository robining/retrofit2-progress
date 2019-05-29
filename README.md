# retrofit2-progress
#### 在项目中添加依赖
```Gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

```Gradle
dependencies {
	  implementation 'com.github.robining:retrofit2-progress:v0.1-SNAPSHOT'
}
```

#### 第一步：
添加ProgressCallFactory和ProgressCallAdapterFactory
```Java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://localhost/") //这里没有使用，因为在Service里面配置的是全路径
        //.client(okHttpClient) //使用callFactory代替
        .callFactory(new ProgressCallFactory(okHttpClient))
        .addCallAdapterFactory(new ProgressCallAdapterFactory())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(new StringConvertFactory())
        .build();
 ```       
 
 #### 第二步：
 使用ProgressWrapper封装响应
 
 ```Java
 @GET("https://github.com/robining/retrofit2-progress/new/master?readme=1")
 ProgressWrapper<Observable<String>> baiduRx();
 
 @GET("https://github.com/robining/retrofit2-progress/new/master?readme=1")
 ProgressWrapper<Call<String>> baiduCall();
 ```
 
 #### 第三步：
 设置进度监听器
 
 ##### Retrofit原生请求示例
 ```Java
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
 ```
 
 ##### RxJava请求示例
 ```Java
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
 ```
 
 ##### 其他类型请求类似
