package com.robining.android.retrofit2.progress;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ProgressCallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(final Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != ProgressWrapper.class) {
            return null;
        }

        final Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        return new ProgressCallAdapter<>(retrofit.callAdapter(responseType, annotations));
    }

    private class ProgressCallAdapter<R, T> implements CallAdapter<R, ProgressWrapper> {
        private CallAdapter<R, T> callAdapter;

        ProgressCallAdapter(CallAdapter<R, T> callAdapter) {
            this.callAdapter = callAdapter;
        }


        @Override
        public Type responseType() {
            return callAdapter.responseType();
        }


        @Override
        public ProgressWrapper adapt(Call<R> call) {
            ProgressCall<R> progressCall = new ProgressCall<>(call);
            T adaptResult = callAdapter.adapt(progressCall);
            return new ProgressWrapper<>(progressCall, adaptResult);
        }
    }
}
