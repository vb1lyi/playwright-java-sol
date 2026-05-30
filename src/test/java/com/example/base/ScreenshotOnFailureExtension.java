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
        if (instance instanceof BaseTest base && base.page != null) {
            try {
                byte[] screenshot = base.page.screenshot(
                        new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment(
                        "Screenshot on failure",
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        "png");
            } catch (Exception ignored) {
            }
        }
    }
}
