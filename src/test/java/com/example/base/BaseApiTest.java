package com.example.base;

import com.example.api.ApiClient;
import com.example.config.ConfigManager;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

/**
 * Base class for API-only tests. Creates a lightweight Playwright HTTP client
 * without launching any browser. Use this instead of {@link BaseUiTest} when
 * your test only exercises REST endpoints via APIRequestContext.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest {

    protected Playwright playwright;
    protected APIRequestContext apiContext;
    protected ApiClient api;

    @BeforeAll
    void createPlaywright() {
        playwright = Playwright.create();
    }

    @AfterAll
    void closePlaywright() {
        playwright.close();
    }

    @BeforeEach
    void createApiContext() {
        apiContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(ConfigManager.get().apiBaseUrl()));
        api = new ApiClient(apiContext);
    }

    @AfterEach
    void disposeApiContext() {
        if (apiContext != null) {
            apiContext.dispose();
        }
    }
}
