package com.example.base;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayInputStream;

public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        boolean testFailed = context.getExecutionException().isPresent();
        if (!testFailed) return;

        Object instance = context.getRequiredTestInstance();
        if (instance instanceof BaseUiTest base && base.page != null) {
            try {
                byte[] screenshot = base.page.screenshot(
                        new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment(
                        "Screenshot on failure",
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        "png");
            } catch (Exception e) {
                // page may be null if the test failed during @BeforeEach
                System.err.println("[ScreenshotOnFailureExtension] Failed to capture screenshot: " + e.getMessage());
            }
        }
    }
}
