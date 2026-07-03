package com.example.base;

import com.example.api.ApiClient;
import com.example.config.ConfigManager;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Paths;

/**
 * Base class for UI tests. Launches a browser and creates a page per test,
 * with Playwright tracing enabled. Also provides an API client for tests
 * that need both UI and API interactions.
 *
 * @see BaseApiTest use this instead when you only need API testing
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseUiTest {

    protected Playwright playwright;
    protected Browser browser;

    protected BrowserContext context;
    protected Page page;
    protected APIRequestContext apiContext;
    protected ApiClient api;

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        ConfigManager cfg = ConfigManager.get();
        BrowserType browserType = switch (cfg.browser().toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit"  -> playwright.webkit();
            default        -> playwright.chromium();
        };
        browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(cfg.headless()));
    }

    @AfterAll
    void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));
        page = context.newPage();
        apiContext = playwright.request().newContext(
                new APIRequest.NewContextOptions().setBaseURL(ConfigManager.get().apiBaseUrl()));
        api = new ApiClient(apiContext);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        String safeName = testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9._-]", "_");
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("build/traces/" + safeName + ".zip")));
        context.close();
        if (apiContext != null) apiContext.dispose();
    }
}
