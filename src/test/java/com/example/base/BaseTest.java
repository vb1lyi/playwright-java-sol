package com.example.base;

import com.example.api.ApiClient;
import com.example.config.ConfigManager;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.nio.file.Paths;

public abstract class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;
    protected APIRequestContext apiContext;
    protected ApiClient api;

    @BeforeAll
    static void launchBrowser() {
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
    static void closeBrowser() {
        browser.close();
        playwright.close();
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
        apiContext.dispose();
    }
}
