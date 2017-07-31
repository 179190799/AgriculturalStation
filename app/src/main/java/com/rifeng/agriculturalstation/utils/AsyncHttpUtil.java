package com.rifeng.agriculturalstation.utils;

import android.content.Context;

import com.loopj.android.http.*;

/**
 * 对AsyncHttpClient的get或post等方法发起的网络请求进行封装
 *
 * Created by chw on 2016/8/11.
 */
public class AsyncHttpUtil {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.post(url, params, responseHandler);
    }

    public static void get(String url, AsyncHttpResponseHandler responseHandler){
        client.get(url, responseHandler);
    }

    public static void post(String url, AsyncHttpResponseHandler responseHandler){
        client.post(url, responseHandler);
    }

    /**
     * 带参数的异步HTTP请求，内置响应解析成JSON
     * @param url
     * @param params
     * @param jsonResponseHandler
     */
    public static void get(String url, RequestParams params, JsonHttpResponseHandler jsonResponseHandler){
        client.get(url, params, jsonResponseHandler);
    }

    /**
     * 带参数的异步HTTP请求，内置响应解析成JSON
     * @param url
     * @param params
     * @param jsonResponseHandler
     */
    public static void post(String url, RequestParams params, JsonHttpResponseHandler jsonResponseHandler){
        client.post(url, params, jsonResponseHandler);

    }

    /**
     * 不带参数的异步HTTP请求，内置响应解析成JSON
     * @param url
     * @param jsonResponseHandler
     */
    public static void get(String url, JsonHttpResponseHandler jsonResponseHandler){
        client.get(url, jsonResponseHandler);
    }

    /**
     * 不带参数的异步HTTP请求，内置响应解析成JSON
     * @param url
     * @param jsonResponseHandler
     */
    public static void post(String url, JsonHttpResponseHandler jsonResponseHandler){
        client.post(url, jsonResponseHandler);
    }

    /**
     * 下载文件
     * @param url
     * @param fileResponseHandler
     */
    public static void get(String url, FileAsyncHttpResponseHandler fileResponseHandler){
        client.setEnableRedirects(true); // 允许重复下载
        client.get(url, fileResponseHandler);
    }

    /**
     * 二进制文件（图片等）的下载
     * @param url
     * @param binaryResponseHandler
     */
    public static void get(String url, BinaryHttpResponseHandler binaryResponseHandler){
        client.get(url, binaryResponseHandler);
    }

    /**
     * 取消请求
     * @param context
     * @param mayInterruptIfRunning
     * @return
     */
    public static boolean cancelRequests(Context context ,boolean mayInterruptIfRunning) {
        client.cancelRequests(context,mayInterruptIfRunning);
        return mayInterruptIfRunning;
    }
}
