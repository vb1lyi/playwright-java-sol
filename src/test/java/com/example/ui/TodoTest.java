package com.example.ui;

import com.example.base.BaseTest;
import com.example.config.ConfigManager;
import com.example.pages.TodoPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoTest extends BaseTest {

    private TodoPage todoPage;

    @BeforeEach
    void openApp() {
        todoPage = new TodoPage(page);
        todoPage.navigate(ConfigManager.get().baseUrl());
    }

    @Test
    void addSingleTodo() {
        todoPage.addTodo("Buy milk");
        assertEquals(1, todoPage.todoCount());
    }

    @Test
    void addMultipleTodos() {
        todoPage.addTodo("Buy milk");
        todoPage.addTodo("Walk the dog");
        assertEquals(2, todoPage.todoCount());
    }

    @Test
    void completeTodo() {
        todoPage.addTodo("Write tests");
        todoPage.completeTodo("Write tests");
        assertTrue(todoPage.isTodoCompleted("Write tests"));
    }
}
