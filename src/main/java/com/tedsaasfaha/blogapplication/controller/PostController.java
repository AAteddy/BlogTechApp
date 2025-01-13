
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
import org.springframework.security.access.prepost.PreAuthorize;
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
//    @PreAuthorize("hasRole('WRITER') or hasRole('ADMIN')")
    public ResponseEntity<PostResponseDTO> createPost(
            @Valid @RequestBody PostCreationRequestDTO postCreationRequestDTO,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Return 401 if user is not authenticated
        }

        User currentUser = customUserPrinciple.getUser();
        // Pass only the User ID or CustomUserPrinciple to the service
        PostResponseDTO postResponseDTO = postService.createPost(postCreationRequestDTO, currentUser);

        return ResponseEntity.ok(postResponseDTO);
    }

    @GetMapping()
    public ResponseEntity<Page<PostResponseDTO>> getAllPublishedPosts(
            Pageable pageable) {

        Page<PostResponseDTO> posts = postService.getAllPublishedPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/author")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByAuthor(
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple,
            Pageable pageable) {

        if (customUserPrinciple == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = customUserPrinciple.getUser();
        Page<PostResponseDTO> posts = postService.getPostByAuthor(currentUser, pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(
            @PathVariable Long postId) {

        PostResponseDTO post = postService.getPostById(postId);

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
//    @PreAuthorize("hasRole('WRITER') or hasRole('ADMIN')")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody Post updatedPost,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = customUserPrinciple.getUser();
        PostResponseDTO post = postService.updatePost(postId, updatedPost, currentUser);

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = customUserPrinciple.getUser();
        postService.deletePost(postId, currentUser);

        return ResponseEntity.ok("Post deleted successfully");
    }
}
//