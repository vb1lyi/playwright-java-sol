package com.example.ui;

import com.example.base.BaseUiTest;
import com.example.config.ConfigManager;
import com.example.pages.TodoPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Todo UI")
@Tag("regression")
class TodoTest extends BaseUiTest {

    private TodoPage todoPage;

    @BeforeEach
    void openApp() {
        todoPage = new TodoPage(page);
        todoPage.navigate(ConfigManager.get().baseUrl());
    }

    @ParameterizedTest
    @Tag("smoke")
    @CsvSource({"Buy milk", "Walk the dog", "Read a book"})
    @Story("Add todo")
    @Description("Verify single todo can be added for various text values")
    @Severity(SeverityLevel.CRITICAL)
    void addSingleTodo(String todoText) {
        todoPage.addTodo(todoText);
        assertThat(todoPage.todoCount()).isOne();
    }

    @Test
    @Tag("smoke")
    @Story("Add todo")
    @Description("Verify multiple todos can be added sequentially and count is correct")
    @Severity(SeverityLevel.CRITICAL)
    void addMultipleTodos() {
        todoPage.addTodo("Buy milk");
        todoPage.addTodo("Walk the dog");
        assertThat(todoPage.todoCount()).isEqualTo(2);
    }

    @Test
    @Tag("smoke")
    @Story("Complete todo")
    @Description("Verify a todo can be marked as completed and its state is reflected in the DOM")
    @Severity(SeverityLevel.CRITICAL)
    void completeTodo() {
        todoPage.addTodo("Write tests");
        todoPage.completeTodo("Write tests");
        assertThat(todoPage.isTodoCompleted("Write tests")).isTrue();
    }

    @Test
    @Tag("demo")
    @Story("Screenshot demo")
    @Description("Deliberate failure to demonstrate Allure screenshot attachment — run with ./gradlew test -Ptags=demo")
    void demoFailureForScreenshot() {
        todoPage.addTodo("This todo exists");
        assertThat(todoPage.todoCount())
                .as("intentional failure to demo Allure screenshot attachment")
                .isEqualTo(99);
    }
}
