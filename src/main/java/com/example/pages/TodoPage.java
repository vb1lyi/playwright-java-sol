package com.example.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class TodoPage {

    private final Page page;
    private final Locator newTodoInput;
    private final Locator todoItems;

    public TodoPage(Page page) {
        this.page = page;
        this.newTodoInput = page.getByPlaceholder("What needs to be done?");
        this.todoItems = page.locator(".todo-list li");
    }

    @Step("Navigate to {0}")
    public void navigate(String baseUrl) {
        page.navigate(baseUrl);
    }

    @Step("Add todo: '{0}'")
    public void addTodo(String text) {
        newTodoInput.fill(text);
        newTodoInput.press("Enter");
    }

    @Step("Get todo count")
    public int todoCount() {
        return todoItems.count();
    }

    @Step("Complete todo: '{0}'")
    public void completeTodo(String text) {
        todoItems.filter(new Locator.FilterOptions().setHasText(text))
                 .locator(".toggle")
                 .check();
    }

    @Step("Check if todo is completed: '{0}'")
    public boolean isTodoCompleted(String text) {
        return todoItems.filter(new Locator.FilterOptions().setHasText(text))
                        .and(page.locator(".completed"))
                        .count() > 0;
    }
}
