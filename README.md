# Playwright-Java-sol

[![Tests](https://github.com/vb1lyi/playwright-java-sol/actions/workflows/test.yml/badge.svg)](https://github.com/vb1lyi/playwright-java-sol/actions/workflows/test.yml)

A test automation project using **Playwright for Java**, covering UI and API testing with a clean Page Object Model, typed response models, and Allure reporting.

## Stack

| Layer | Technology |
|---|---|
| Browser automation | Playwright for Java 1.52 |
| API testing | Playwright `APIRequestContext` |
| Assertions | JUnit 5 |
| JSON deserialization | Jackson 2.17 |
| Reporting | Allure 2.29 |
| Build | Gradle 8 / Java 21 |
| CI | GitHub Actions |

## Project structure

```
src/
├── main/java/com/example/
│   ├── api/        ApiClient.java       – thin HTTP wrapper with JSON helpers
│   ├── config/     ConfigManager.java   – properties + system-property overrides
│   ├── model/      Post.java, Comment.java – Jackson record POJOs
│   └── pages/      TodoPage.java        – Page Object for TodoMVC
└── test/java/com/example/
    ├── api/        PostsApiTest.java    – typed REST assertions against JSONPlaceholder
    ├── base/       BaseTest.java        – browser/context lifecycle + Playwright tracing
    └── ui/         TodoTest.java        – end-to-end TodoMVC tests
```

## Running locally

```bash
# Install Playwright browsers (once)
./gradlew installPlaywrightBrowsers

# Run all tests
./gradlew test

# Run tests then open Allure report in browser (serves on localhost, avoids file:// CORS issues)
./gradlew allureServe
```

### Override config at runtime

```bash
./gradlew test -Dbrowser=firefox -Dheadless=false
```

## CI

GitHub Actions runs the full suite on every push and pull request to `main`.  
Allure HTML report and Playwright traces are uploaded as artifacts (retained 30 days and 7 days respectively).
