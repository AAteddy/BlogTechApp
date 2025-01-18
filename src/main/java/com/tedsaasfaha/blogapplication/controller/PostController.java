
package com.tedsaasfaha.blogapplication.controller;


import com.tedsaasfaha.blogapplication.dto.PostCreationRequestDTO;
import com.tedsaasfaha.blogapplication.dto.PostResponseDTO;
import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.PostStatus;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.service.CustomUserPrinciple;
import com.tedsaasfaha.blogapplication.service.PostService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @GetMapping("/all")
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        User currentUser = customUserPrinciple.getUser();

        Page<PostResponseDTO> posts = postService.getAllPosts(pageable, currentUser);
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
            @Valid @RequestBody PostCreationRequestDTO updatedPostDTO,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User currentUser = customUserPrinciple.getUser();
        PostResponseDTO post = postService.updatePost(postId, updatedPostDTO, currentUser);

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}/status")
    public ResponseEntity<PostResponseDTO> updatePostStatus(
            @PathVariable Long postId,
            @RequestParam("newStatus") PostStatus newStatus,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        User currentUser = customUserPrinciple.getUser();
        PostResponseDTO post = postService.updatePostStatus(postId, newStatus, currentUser);

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

    @PatchMapping("/{postId}")
    public ResponseEntity<String> restorePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        User currentUser = customUserPrinciple.getUser();
        postService.restorePost(postId, currentUser);

        return ResponseEntity.ok("Post restored successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDTO>> searchPosts(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(postService.searchPosts(keyword, pageable));
    }

    @GetMapping("/filter")
        public ResponseEntity<Page<PostResponseDTO>> filterPosts(
                @RequestParam(required = false) String keyword,
                @RequestParam(required = false) PostStatus status,
                @RequestParam(required = false) Long authorId,
                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                @RequestParam int page,
                @RequestParam int size,
                @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                postService.searchAndFilterPosts(
                        keyword, status, authorId, startDateTime, endDateTime, pageable));
        }

    @GetMapping("/filter-status")
    public ResponseEntity<Page<PostResponseDTO>> filterPostsByStatus(
            @RequestParam PostStatus status,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(postService.filterPostsByStatus(status, pageable));
    }

    @GetMapping("/filter-date")
    public ResponseEntity<Page<PostResponseDTO>> filterPostsByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                postService.filterPostsByDateRange(
                        startDateTime, endDateTime, pageable));
    }

    @GetMapping("/filter-author")
    public ResponseEntity<Page<PostResponseDTO>> filterPostsByAuthor(
            @RequestParam Long authorId,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal CustomUserPrinciple customUserPrinciple) {

        if (customUserPrinciple == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(postService.filterPostsByAuthor(authorId, pageable));
    }

}
//