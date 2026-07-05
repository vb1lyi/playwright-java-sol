package com.example.base;

import com.example.api.ApiClient;
import com.example.config.ConfigManager;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for tests that drive both the browser and REST endpoints.
 * Extends {@link BaseUiTest} and adds an {@link ApiClient} that shares
 * the same Playwright instance as the browser.
 *
 * @see BaseUiTest  if your test only needs a browser
 * @see BaseApiTest if your test only needs API access
 */
public abstract class BaseHybridTest extends BaseUiTest {

    protected APIRequestContext apiContext;
    protected ApiClient api;

    @BeforeEach
    void setUpApi() {
        apiContext = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL(ConfigManager.get().apiBaseUrl()));
        api = new ApiClient(apiContext);
    }

    @AfterEach
    void tearDownApi() {
        if (apiContext != null) apiContext.dispose();
    }
}
