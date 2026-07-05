package com.example.base;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotOnFailureExtension.class);

    @Override
    public void afterTestExecution(ExtensionContext context) {
        boolean failed = context.getExecutionException().isPresent();
        if (!failed) return;

        Object instance = context.getRequiredTestInstance();
        if (!(instance instanceof BaseUiTest base)) return;

        base.testFailed = true;

        if (base.page != null) {
            try {
                byte[] screenshot = base.page.screenshot(
                        new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment(
                        "Screenshot on failure",
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        "png");
            } catch (Exception e) {
                log.warn("Failed to capture screenshot", e);
            }
        }
    }
}
