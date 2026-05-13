package com.example.api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;

public class ApiClient {

    private final APIRequestContext context;

    public ApiClient(APIRequestContext context) {
        this.context = context;
    }

    public APIResponse get(String path) {
        return context.get(path);
    }

    public APIResponse get(String path, Map<String, String> params) {
        RequestOptions options = RequestOptions.create();
        params.forEach(options::setQueryParam);
        return context.get(path, options);
    }

    public APIResponse post(String path, Object body) {
        return context.post(path, RequestOptions.create().setData(body));
    }

    public APIResponse put(String path, Object body) {
        return context.put(path, RequestOptions.create().setData(body));
    }

    public APIResponse delete(String path) {
        return context.delete(path);
    }
}
