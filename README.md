# Playwright + Java solution

[![Tests](https://github.com/vb1lyi/playwright-java-sol/actions/workflows/test.yml/badge.svg)](https://github.com/vb1lyi/playwright-java-sol/actions/workflows/test.yml)

A test automation project using Playwright for Java, covering UI and API testing.

## Stack

| Layer                | Technology     |
|----------------------|----------------|
| Browser automation   | Playwright for Java |
| API testing          | Playwright `APIRequestContext` |
| Assertions           | JUnit 5        |
| JSON deserialization | Jackson        |
| Reporting            | Allure         |
| Build                | Gradle/Java 21 |
| CI                   | GitHub Actions |

## Project structure

```
src/
├── main/java/com/example/
│   ├── api/        ApiClient.java       -  thin HTTP wrapper with JSON helpers
│   ├── config/     ConfigManager.java   -  properties + system-property overrides
│   ├── model/      Post.java, Comment.java - Jackson record POJOs
│   └── pages/      TodoPage.java        - Page Object for TodoMVC
└── test/java/com/example/
    ├── api/        PostsApiTest.java    - typed REST assertions against JSONPlaceholder
    ├── base/       BaseUiTest.java      - browser lifecycle + Playwright tracing
    └── ui/         TodoTest.java        - end-to-end TodoMVC tests
```

## Running locally

```bash
# Install Playwright browsers (once)
./gradlew installPlaywrightBrowsers

# Run all tests
./gradlew test

# Run tests then open Allure report in browser
./gradlew allureServe
```

### Override config at runtime

```bash
./gradlew test -Dbrowser=firefox -Dheadless=false
```

### Run tests by tag

```bash
# Smoke tests only
./gradlew test -Ptags=smoke

# Smoke + regression
./gradlew test -Ptags=smoke,regression

# Screenshot demo (deliberate failure)
./gradlew test -Ptags=demo
```

## Test strategy

Tests are tagged for selective execution:

| Tag          | Purpose                                      |
|-------------|----------------------------------------------|
| `smoke`     | Critical path — runs in CI on every push     |
| `regression`| Full suite — all tests                       |
| `demo`      | Deliberate failure for screenshot attachment |

API tests use `assertAll()` for grouped assertions,
measure response time against a 5-second threshold, and include negative-path tests
for non-existent resources and malformed requests.

## CI

GitHub Actions runs the full suite on every push and pull request to `main`.

- **Allure report** - published to GitHub Pages after every run: https://vb1lyi.github.io/playwright-java-sol/
- **Playwright traces** - uploaded as a workflow artifact (retained 7 days)
