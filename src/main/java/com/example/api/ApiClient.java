package com.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.List;
import java.util.Map;

public class ApiClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    public <T> T as(APIResponse response, Class<T> type) {
        try {
            return MAPPER.readValue(response.body(), type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response as " + type.getSimpleName(), e);
        }
    }

    public <T> List<T> asList(APIResponse response, Class<T> elementType) {
        try {
            return MAPPER.readValue(response.body(),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response as List<" + elementType.getSimpleName() + ">", e);
        }
    }
}
