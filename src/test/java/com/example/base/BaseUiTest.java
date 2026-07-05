package com.example.base;

import com.example.config.ConfigManager;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Paths;

/**
 * Base class for UI-only tests. Launches a browser and creates a page per test,
 * with Playwright tracing enabled.
 *
 * @see BaseHybridTest if your test also needs an API client alongside the browser
 * @see BaseApiTest if your test only exercises REST endpoints
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseUiTest {

    protected Playwright playwright;
    protected Browser browser;

    protected BrowserContext context;
    protected Page page;

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
    }

    @AfterEach
    void tearDown(TestInfo testInfo, ExtensionContext extensionContext) {
        String safeName = testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9._-]", "_");
        boolean failed = extensionContext.getExecutionException().isPresent();
        Tracing.StopOptions stopOptions = failed
                ? new Tracing.StopOptions().setPath(Paths.get("build/traces/" + safeName + ".zip"))
                : new Tracing.StopOptions();
        context.tracing().stop(stopOptions);
        context.close();
    }
}
