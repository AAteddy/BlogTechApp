
package com.tedsaasfaha.blogapplication.controller;


import com.tedsaasfaha.blogapplication.dto.PostCreationRequestDTO;
import com.tedsaasfaha.blogapplication.dto.PostResponseDTO;
import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.service.CustomUserPrinciple;
import com.tedsaasfaha.blogapplication.service.PostService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    @PostMapping()
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostCreationRequestDTO postCreationRequestDTO,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Return 401 if user is not authenticated
        }

        // Pass only the User ID or CustomUserPrinciple to the service
        PostResponseDTO postResponseDTO = postService.createPost(postCreationRequestDTO, customUserPrinciple);
        return ResponseEntity.ok(postResponseDTO);
    }

    @GetMapping()
    public ResponseEntity<Page<Post>> getAllPublishedPosts(
            Pageable pageable) {

        Page<Post> posts = postService.getAllPublishedPosts(pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/author")
    public ResponseEntity<Page<Post>> getPostsByAuthor(
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple,
            Pageable pageable) {

        if (customUserPrinciple == null) {
            throw new IllegalStateException("No authenticated user found.");
        }

        User currentUser = customUserPrinciple.getUser();
        Page<Post> posts = postService.getPostByAuthor(currentUser, pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(
            @PathVariable Long postId) {

        Post post = postService.getPostById(postId);

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId,
            @RequestBody Post updatedPost,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null) {
            throw new IllegalStateException("No authenticated user found.");
        }

        User currentUser = customUserPrinciple.getUser();
        Post post = postService.updatePost(postId, updatedPost, currentUser);

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null) {
            throw new IllegalStateException("No authenticated user found.");
        }

        User currentUser = customUserPrinciple.getUser();
        postService.deletePost(postId, currentUser);

        return ResponseEntity.ok("Post deleted successfully");
    }
}
//