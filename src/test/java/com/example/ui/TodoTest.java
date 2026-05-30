package com.example.ui;

import com.example.base.BaseTest;
import com.example.config.ConfigManager;
import com.example.pages.TodoPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Feature("Todo UI")
class TodoTest extends BaseTest {

    private TodoPage todoPage;

    @BeforeEach
    void openApp() {
        todoPage = new TodoPage(page);
        todoPage.navigate(ConfigManager.get().baseUrl());
    }

    @Test
    @Story("Add todo")
    void addSingleTodo() {
        todoPage.addTodo("Buy milk");
        assertEquals(1, todoPage.todoCount());
    }

    @Test
    @Story("Add todo")
    void addMultipleTodos() {
        todoPage.addTodo("Buy milk");
        todoPage.addTodo("Walk the dog");
        assertEquals(2, todoPage.todoCount());
    }

    @Test
    @Story("Complete todo")
    void completeTodo() {
        todoPage.addTodo("Write tests");
        todoPage.completeTodo("Write tests");
        assertTrue(todoPage.isTodoCompleted("Write tests"));
    }

    @Test
    @Story("Add todo")
    void failingTestForScreenshotDemo() {
        todoPage.addTodo("This todo exists");
        assertEquals(99, todoPage.todoCount(), "intentional failure to demo Allure screenshot attachment");
    }
}
