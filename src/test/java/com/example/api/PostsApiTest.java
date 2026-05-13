package com.example.api;

import com.example.base.BaseTest;
import com.microsoft.playwright.APIResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PostsApiTest extends BaseTest {

    @Test
    void getAllPosts() {
        APIResponse response = api.get("/posts");
        assertEquals(200, response.status());
        assertTrue(response.text().contains("\"userId\""));
    }

    @Test
    void getPostById() {
        APIResponse response = api.get("/posts/1");
        assertEquals(200, response.status());
        assertTrue(response.text().contains("\"id\": 1"));
    }

    @Test
    void createPost() {
        APIResponse response = api.post("/posts", Map.of(
                "title", "Test Post",
                "body", "Test body content",
                "userId", 1
        ));
        assertEquals(201, response.status());
        assertTrue(response.text().contains("\"id\""));
    }

    @Test
    void getPostsFilteredByUser() {
        APIResponse response = api.get("/posts", Map.of("userId", "1"));
        assertEquals(200, response.status());
    }
}
