package com.example.api;

import com.example.base.BaseTest;
import com.example.model.Comment;
import com.example.model.Post;
import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Feature("Posts API")
class PostsApiTest extends BaseTest {

    @Test
    @Story("Get all posts")
    void getAllPosts() {
        APIResponse response = api.get("/posts");
        assertEquals(200, response.status());

        List<Post> posts = api.asList(response, Post.class);
        assertEquals(100, posts.size());
        posts.forEach(p -> {
            assertTrue(p.id() > 0);
            assertNotNull(p.title());
        });
    }

    @Test
    @Story("Get post by id")
    void getPostById() {
        APIResponse response = api.get("/posts/1");
        assertEquals(200, response.status());

        Post post = api.as(response, Post.class);
        assertEquals(1, post.id());
        assertEquals(1, post.userId());
        assertFalse(post.title().isBlank());
    }

    @Test
    @Story("Create post")
    void createPost() {
        APIResponse response = api.post("/posts", Map.of(
                "title", "Test Post",
                "body", "Test body content",
                "userId", 1
        ));
        assertEquals(201, response.status());

        Post created = api.as(response, Post.class);
        assertTrue(created.id() > 0);
        assertEquals("Test Post", created.title());
        assertEquals(1, created.userId());
    }

    @Test
    @Story("Filter posts by user")
    void getPostsFilteredByUser() {
        APIResponse response = api.get("/posts", Map.of("userId", "1"));
        assertEquals(200, response.status());

        List<Post> posts = api.asList(response, Post.class);
        assertFalse(posts.isEmpty());
        posts.forEach(p -> assertEquals(1, p.userId()));
    }

    @Test
    @Story("Get comments for a post")
    void getCommentsForPost() {
        APIResponse response = api.get("/posts/1/comments");
        assertEquals(200, response.status());

        List<Comment> comments = api.asList(response, Comment.class);
        assertFalse(comments.isEmpty());
        comments.forEach(c -> {
            assertEquals(1, c.postId());
            assertTrue(c.email().contains("@"));
        });
    }
}
