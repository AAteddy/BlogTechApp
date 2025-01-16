
package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.dto.PostCreationRequestDTO;
import com.tedsaasfaha.blogapplication.dto.PostResponseDTO;
import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.PostStatus;
import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.exception.PostNotFoundException;
import com.tedsaasfaha.blogapplication.repository.PostRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostRepo postRepo;

    public PostServiceImpl(PostRepo postRepo) {
        this.postRepo = postRepo;
    }

    @Override
    public PostResponseDTO createPost(PostCreationRequestDTO postCreationRequestDTO,
                                      User currentUser) {

        // Only allow Post creation if the user has WRITER or ADMIN role
        if(currentUser.getRole().equals(Role.READER)) {
            getWarn(currentUser);
            throw new BadCredentialsException("You are not authorized to create a Post");
        }

        // Map DTO to entity
        Post post = new Post();
        post.setTitle(postCreationRequestDTO.getTitle());
        post.setContent(postCreationRequestDTO.getContent());
//        post.setStatus(PostStatus.PUBLISHED); // Default status
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setAuthor(currentUser); // Set the authenticated user as the author

        logger.info("Creating post with title: {}", post.getTitle());
        // Save the post
        Post savedPost = postRepo.save(post);
        logger.info("Post created successfully with ID: {}", savedPost.getId());

        // Map entity to response DTO
        return mapToPostResponseDTO(savedPost);
    }

    @Override
    @Cacheable(value = "publishedPosts", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<PostResponseDTO> getAllPublishedPosts(Pageable pageable) {
        Page<Post> posts = postRepo.findByStatus(PostStatus.PUBLISHED, pageable);

        return posts.map(this::mapToPostResponseDTO);
    }

    @Override
    public Page<PostResponseDTO> getAllPosts(Pageable pageable,
                                             User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            getWarn(currentUser);
            throw new BadCredentialsException("You are not authorized to access this endpoint.");
        }

        Page<Post> posts = postRepo.findAllActivePosts(pageable);

        return posts.map(this::mapToPostResponseDTO);
    }


    @Override
    public Page<PostResponseDTO> getPostByAuthor(User author, Pageable pageable) {
        Page<Post> posts = postRepo.findByAuthor(author, pageable);

        return posts.map(this::mapToPostResponseDTO);
    }

    @Override
    public PostResponseDTO getPostById(Long postId) {
        Post post = postRepo.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        return mapToPostResponseDTO(post);
    }

    @Override
    @CacheEvict(value = {"publishedPosts", "postById"}, key = "#postId", allEntries = true)
    public PostResponseDTO updatePost(Long postId, PostCreationRequestDTO updatedPostDTO, User currentUser) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        // Only allow update if the user is the author or has admin role
        if (!(post.getAuthor().getId().equals(currentUser.getId())
                && currentUser.getRole().equals(Role.WRITER))
                &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            getWarn(currentUser);
            throw new BadCredentialsException("You are not authorized to update this post");
        }

        post.setTitle(updatedPostDTO.getTitle());
        post.setContent(updatedPostDTO.getContent());
//        post.setStatus(PostStatus.PUBLISHED);
        post.setCreatedAt(post.getCreatedAt());
        post.setUpdatedAt(LocalDateTime.now());

        Post updatePost = postRepo.save(post);

        return mapToPostResponseDTO(updatePost);
    }

    @Override
    @CacheEvict(value = {"publishedPosts", "postById"}, key = "#postId", condition = "#newStatus == 'PUBLISHED'", allEntries = true)
    public PostResponseDTO updatePostStatus(Long postId, PostStatus newStatus, User currentUser) {
        logger.info("Updating status of post with ID: {} to {}", postId, newStatus);
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with Post Id: " + postId));

        // Only allow update if the user is the author or has admin role
        if (!(post.getAuthor().getId().equals(currentUser.getId())
                && currentUser.getRole().equals(Role.WRITER))
                &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            getWarn(currentUser);
            throw new BadCredentialsException("You are not authorized to update this post");
        }

        if (PostStatus.PUBLISHED.equals(newStatus)) {
            post.setStatus(PostStatus.PUBLISHED);
        }
        else if (PostStatus.ARCHIVED.equals(newStatus)) {
            post.setStatus(PostStatus.ARCHIVED);
        }
        else if (PostStatus.DRAFT.equals(newStatus)) {
            post.setStatus(PostStatus.DRAFT);
        }

        post.setUpdatedAt(LocalDateTime.now());
        Post updatedPost = postRepo.save(post);
        logger.info("Post status updated successfully.");

        return mapToPostResponseDTO(updatedPost);
    }


    @Override
    @CacheEvict(value = {"publishedPosts", "postById"}, key = "#postId", allEntries = true)
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        // Only allow to delete if the user is the author or has admin role
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            getWarn(currentUser);
            throw new BadCredentialsException("You are not authorized to delete this post");
        }

        logger.info("Deleting post with ID: {}",postId);
        post.setDeleted(true);
        postRepo.save(post);
        logger.info("Post deleted successfully.");
    }

    // method for restoring soft-deleted posts
    @Override
    @CacheEvict(value = "publishedPosts", allEntries = true)
    public void restorePost(Long postId, User currentUser) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        // Only allow to delete if the user is the author or has admin role
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            getWarn(currentUser);
            throw new BadCredentialsException("You are not authorized to delete this post");
        }

        post.setDeleted(false);
        postRepo.save(post);
    }

    private PostResponseDTO mapToPostResponseDTO(Post post) {
        PostResponseDTO responseDTO = new PostResponseDTO();
        responseDTO.setId(post.getId());
        responseDTO.setTitle(post.getTitle());
        responseDTO.setContent(post.getContent());
        responseDTO.setStatus(String.valueOf(post.getStatus()));
        responseDTO.setCreatedAt(post.getCreatedAt());
        responseDTO.setUpdatedAt(post.getUpdatedAt());
        responseDTO.setCreatedBy(post.getCreatedBy());
        responseDTO.setUpdatedBy(post.getUpdatedBy());


        // Set author details
        PostResponseDTO.AuthorResponseDTO authorDTO = new PostResponseDTO.AuthorResponseDTO();
        authorDTO.setId(post.getAuthor().getId());
        authorDTO.setEmail(post.getAuthor().getEmail());
        authorDTO.setName(post.getAuthor().getName());
        authorDTO.setRole(post.getAuthor().getRole().name());
        responseDTO.setAuthor(authorDTO);

        return responseDTO;
    }

    private void getWarn(User currentUser) {
        logger.warn("Unauthorized access attempt by user ID: {}, Role: {}",
                currentUser.getId(), currentUser.getRole());
    }

}
//