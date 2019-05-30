package com.robining.android.retrofit2.progress;

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

public class ProxyEventListener extends EventListener {
    private EventListener eventListener;

    public ProxyEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void callStart(Call call) {
        eventListener.callStart(call);
    }

    @Override
    public void dnsStart(Call call, String domainName) {
        eventListener.dnsStart(call, domainName);
    }

    @Override
    public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
        eventListener.dnsEnd(call, domainName, inetAddressList);
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        eventListener.connectStart(call, inetSocketAddress, proxy);
    }

    @Override
    public void secureConnectStart(Call call) {
        eventListener.secureConnectStart(call);
    }

    @Override
    public void secureConnectEnd(Call call, Handshake handshake) {
        eventListener.secureConnectEnd(call, handshake);
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        eventListener.connectEnd(call, inetSocketAddress, proxy, protocol);
    }

    @Override
    public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol, IOException ioe) {
        eventListener.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
    }

    @Override
    public void connectionAcquired(Call call, Connection connection) {
        eventListener.connectionAcquired(call, connection);
    }

    @Override
    public void connectionReleased(Call call, Connection connection) {
        eventListener.connectionReleased(call, connection);
    }

    @Override
    public void requestHeadersStart(Call call) {
        eventListener.requestHeadersStart(call);
    }

    @Override
    public void requestHeadersEnd(Call call, Request request) {
        eventListener.requestHeadersEnd(call, request);
    }

    @Override
    public void requestBodyStart(Call call) {
        eventListener.requestBodyStart(call);
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount) {
        eventListener.requestBodyEnd(call, byteCount);
    }

    @Override
    public void responseHeadersStart(Call call) {
        eventListener.responseHeadersStart(call);
    }

    @Override
    public void responseHeadersEnd(Call call, Response response) {
        eventListener.responseHeadersEnd(call, response);
    }

    @Override
    public void responseBodyStart(Call call) {
        eventListener.responseBodyStart(call);
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        eventListener.responseBodyEnd(call, byteCount);
    }

    @Override
    public void callEnd(Call call) {
        eventListener.callEnd(call);
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        eventListener.callFailed(call, ioe);
    }
}
