package com.example.api;

import com.example.base.BaseApiTest;
import com.example.model.Comment;
import com.example.model.Post;
import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Feature("Posts API")
@Tag("regression")
class PostsApiTest extends BaseApiTest {

    private static final Duration MAX_RESPONSE_TIME = Duration.ofSeconds(5);

    // --- happy path ---

    @Test
    @Tag("smoke")
    @Story("Get all posts")
    @Description("Verify GET /posts returns 100 posts with valid ids and non-blank titles within 5 seconds")
    @Severity(SeverityLevel.CRITICAL)
    void getAllPosts() {
        Instant start = Instant.now();
        APIResponse response = api.get("/posts");
        Duration elapsed = Duration.between(start, Instant.now());

        List<Post> posts = api.asList(response, Post.class);
        assertAll("GET /posts",
            () -> assertThat(response.status()).as("status code").isEqualTo(200),
            () -> assertThat(elapsed).as("response time").isLessThan(MAX_RESPONSE_TIME),
            () -> assertThat(posts).as("post list").hasSize(100),
            () -> assertThat(posts).allMatch(p -> p.id() > 0, "every post id > 0"),
            () -> assertThat(posts).allMatch(p -> p.title() != null && !p.title().isBlank(),
                    "every post has non-blank title")
        );
    }

    @ParameterizedTest
    @Tag("smoke")
    @CsvSource({"1", "2", "3", "10"})
    @Story("Get post by id")
    @Description("Verify GET /posts/{id} returns the correct post with matching id and non-blank title")
    @Severity(SeverityLevel.CRITICAL)
    void getPostById(int postId) {
        Instant start = Instant.now();
        APIResponse response = api.get("/posts/" + postId);
        Duration elapsed = Duration.between(start, Instant.now());

        Post post = api.as(response, Post.class);
        assertAll("GET /posts/" + postId,
            () -> assertThat(response.status()).as("status code").isEqualTo(200),
            () -> assertThat(elapsed).as("response time").isLessThan(MAX_RESPONSE_TIME),
            () -> assertThat(post.id()).as("post id matches request").isEqualTo(postId),
            () -> assertThat(post.title()).as("title is non-blank").isNotBlank()
        );
    }

    @Test
    @Tag("smoke")
    @Story("Create post")
    @Description("Verify POST /posts returns 201 with created post reflecting submitted fields")
    @Severity(SeverityLevel.CRITICAL)
    void createPost() {
        Instant start = Instant.now();
        APIResponse response = api.post("/posts", Map.of(
                "title", "Test Post",
                "body", "Test body content",
                "userId", 1
        ));
        Duration elapsed = Duration.between(start, Instant.now());

        Post created = api.as(response, Post.class);
        assertAll("POST /posts",
            () -> assertThat(response.status()).as("status code").isEqualTo(201),
            () -> assertThat(elapsed).as("response time").isLessThan(MAX_RESPONSE_TIME),
            () -> assertThat(created.id()).as("new post has positive id").isPositive(),
            () -> assertThat(created.title()).as("title matches submitted value").isEqualTo("Test Post"),
            () -> assertThat(created.userId()).as("userId matches submitted value").isEqualTo(1)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "3", "5"})
    @Story("Filter posts by user")
    @Description("Verify GET /posts?userId={0} returns only posts belonging to that user")
    @Severity(SeverityLevel.NORMAL)
    void getPostsFilteredByUser(String userId) {
        APIResponse response = api.get("/posts", Map.of("userId", userId));
        assertThat(response.status()).as("status code").isEqualTo(200);

        List<Post> posts = api.asList(response, Post.class);
        int expectedUserId = Integer.parseInt(userId);
        assertAll("GET /posts?userId=" + userId,
            () -> assertThat(posts).as("result set").isNotEmpty(),
            () -> assertThat(posts).allMatch(p -> p.userId() == expectedUserId,
                    "every post belongs to user " + userId)
        );
    }

    @Test
    @Tag("smoke")
    @Story("Get comments for a post")
    @Description("Verify GET /posts/1/comments returns comments with valid emails and matching postId")
    @Severity(SeverityLevel.NORMAL)
    void getCommentsForPost() {
        APIResponse response = api.get("/posts/1/comments");
        assertThat(response.status()).as("status code").isEqualTo(200);

        List<Comment> comments = api.asList(response, Comment.class);
        assertAll("GET /posts/1/comments",
            () -> assertThat(comments).as("comments list").isNotEmpty(),
            () -> assertThat(comments).allMatch(c -> c.postId() == 1,
                    "every comment has postId=1"),
            () -> assertThat(comments).allMatch(c -> c.email() != null && c.email().contains("@"),
                    "every comment has email containing @")
        );
    }

    // --- negative / edge cases ---

    @Test
    @Story("Get non-existent post")
    @Description("Verify GET /posts/999999 returns 200 with default-constructed record — no real post exists")
    @Severity(SeverityLevel.NORMAL)
    void getNonExistentPost() {
        APIResponse response = api.get("/posts/999999");
        assertThat(response.status()).as("status code").isEqualTo(200);

        Post post = api.as(response, Post.class);
        assertAll("non-existent post",
            () -> assertThat(post.id()).as("id defaults to 0 for non-existent").isZero(),
            () -> assertThat(post.title()).as("title is null for non-existent").isNull()
        );
    }

    @Test
    @Story("Create post with missing fields")
    @Description("Verify POST /posts with empty body still returns 201 — tests API leniency")
    @Severity(SeverityLevel.MINOR)
    void createPostWithEmptyBody() {
        APIResponse response = api.post("/posts", Map.of());
        assertThat(response.status()).as("status code").isEqualTo(201);

        Post created = api.as(response, Post.class);
        assertAll("empty body post",
            () -> assertThat(created.id()).as("id is still assigned").isPositive(),
            () -> assertThat(created.title()).as("title is null when not provided").isNull()
        );
    }
}
