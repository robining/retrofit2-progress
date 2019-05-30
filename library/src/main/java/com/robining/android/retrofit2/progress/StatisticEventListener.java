package com.robining.android.retrofit2.progress;

import android.os.SystemClock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

public class StatisticEventListener extends EventListener {
    private long lastTime, connectStartTime, callStartTime;
    private long waitCallCost, dnsCost, connectCost, tlsCost, sendHeaderCost, sendBodyCost, waitResponseCost, receiveHeaderCost, receiveBodyCost, allCost;

    @Override
    public void callStart(Call call) {
        super.callStart(call);
        this.lastTime = SystemClock.elapsedRealtime();
        this.callStartTime = this.lastTime;
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        super.dnsStart(call, domainName);
        long currentTime = SystemClock.elapsedRealtime();
        waitCallCost = currentTime - lastTime;
        this.lastTime = currentTime;
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        long currentTime = SystemClock.elapsedRealtime();
        dnsCost = currentTime - lastTime;
        this.lastTime = currentTime;
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
        this.lastTime = SystemClock.elapsedRealtime();
        this.connectStartTime = this.lastTime;
    }

    @Override
    public void secureConnectStart(Call call) {
        super.secureConnectStart(call);
        this.lastTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void secureConnectEnd(Call call, Handshake handshake) {
        super.secureConnectEnd(call, handshake);
        long currentTime = SystemClock.elapsedRealtime();
        tlsCost = currentTime - lastTime;
        this.lastTime = currentTime;
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
        long currentTime = SystemClock.elapsedRealtime();
        connectCost = currentTime - connectStartTime;
        this.lastTime = currentTime;
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
        long currentTime = SystemClock.elapsedRealtime();
        connectCost = currentTime - connectStartTime;
        this.lastTime = currentTime;
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        super.connectionAcquired(call, connection);
        this.lastTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        super.connectionReleased(call, connection);
        this.lastTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void requestHeadersStart(Call call) {
        super.requestHeadersStart(call);
        this.lastTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        super.requestHeadersEnd(call, request);
        long currentTime = SystemClock.elapsedRealtime();
        sendHeaderCost = currentTime - lastTime;
        this.lastTime = currentTime;
    }

    @Override
    public void requestBodyStart(Call call) {
        super.requestBodyStart(call);
        this.lastTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        super.requestBodyEnd(call, byteCount);
        long currentTime = SystemClock.elapsedRealtime();
        sendBodyCost = currentTime - lastTime;
        this.lastTime = currentTime;
    }

    @Override
    public void responseHeadersStart(Call call) {
        super.responseHeadersStart(call);
        long currentTime = SystemClock.elapsedRealtime();
        waitResponseCost = currentTime - lastTime;
        this.lastTime = currentTime;
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        super.responseHeadersEnd(call, response);
        long currentTime = SystemClock.elapsedRealtime();
        receiveHeaderCost = currentTime - lastTime;
        this.lastTime = currentTime;
    }

    @Override
    public void responseBodyStart(Call call) {
        super.responseBodyStart(call);
        this.lastTime = SystemClock.elapsedRealtime();
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
        long currentTime = SystemClock.elapsedRealtime();
        receiveBodyCost = currentTime - lastTime;
        this.lastTime = currentTime;
    }

    @Override
    public void callEnd(Call call) {
        super.callEnd(call);
        long currentTime = SystemClock.elapsedRealtime();
        allCost = currentTime - callStartTime;
        this.lastTime = currentTime;

        onCallFinished(call, true, null);
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        super.callFailed(call, ioe);
        long currentTime = SystemClock.elapsedRealtime();
        allCost = currentTime - callStartTime;
        this.lastTime = currentTime;

        onCallFinished(call, false, ioe);
    }


    protected void onCallFinished(Call call, boolean isSuccess, IOException e) {
        System.out.println("-----------------------------------------------------");
        System.out.println(call.request().url().toString());
        System.out.println("阻塞:" + waitCallCost);
        System.out.println("dns解析:" + dnsCost);
        System.out.println("连接:" + connectCost + "(TLS:" + tlsCost + ")");
        System.out.println("发送:" + (sendHeaderCost + sendBodyCost) + "(Header:" + sendHeaderCost + ", Body:" + sendBodyCost + ")");
        System.out.println("等待响应:" + waitResponseCost);
        System.out.println("响应:" + (receiveHeaderCost + receiveBodyCost) + "(Header:" + receiveHeaderCost + ", Body:" + receiveBodyCost + ")");
        System.out.println("总计耗时:" + allCost);
        System.out.println("请求状态:" + (isSuccess ? "成功" : ("失败:" + (e != null ? e.getMessage() : ""))));
    }
}
