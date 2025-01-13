
package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.dto.PostCreationRequestDTO;
import com.tedsaasfaha.blogapplication.dto.PostResponseDTO;
import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.PostStatus;
import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.exception.PostNotFoundException;
import com.tedsaasfaha.blogapplication.repository.PostRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;

    public PostServiceImpl(PostRepo postRepo) {
        this.postRepo = postRepo;
    }

    @Override
    public PostResponseDTO createPost(PostCreationRequestDTO postCreationRequestDTO,
                                      User currentUser) {

        // Only allow Post creation if the user has WRITER or ADMIN role
        if(currentUser.getRole().equals(Role.READER))
            throw new BadCredentialsException("You are not authorized to create a Post");

        // Map DTO to entity
        Post post = new Post();
        post.setTitle(postCreationRequestDTO.getTitle());
        post.setContent(postCreationRequestDTO.getContent());
        post.setStatus(PostStatus.PUBLISHED); // Default status
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        // Set the authenticated user as the author
        post.setAuthor(currentUser);

        // Save the post
        Post savedPost = postRepo.save(post);

        // Map entity to response DTO
        return mapToPostResponseDTO(savedPost);
    }

    @Override
    public Page<PostResponseDTO> getAllPublishedPosts(Pageable pageable) {
        Page<Post> posts = postRepo.findByStatus(PostStatus.PUBLISHED, pageable);

        return posts.map(this::mapToPostResponseDTO);
    }

    @Override
    public Page<PostResponseDTO> getAllPosts(Pageable pageable,
                                             User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN))
            throw new BadCredentialsException("You are not authorized to access this endpoint.");

        Page<Post> posts = postRepo.findAll(pageable);

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
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        return mapToPostResponseDTO(post);
    }

    @Override
    public PostResponseDTO updatePost(Long postId, Post updatedPost, User currentUser) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        // Only allow update if the user is the author or has admin role
        if (!(post.getAuthor().getId().equals(currentUser.getId())
                && currentUser.getRole().equals(Role.WRITER))
                &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new BadCredentialsException("You are not authorized to update this post");
        }

        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setStatus(PostStatus.PUBLISHED);
        post.setCreatedAt(post.getCreatedAt());
        post.setUpdatedAt(LocalDateTime.now());

        Post postResponse = postRepo.save(post);

        return mapToPostResponseDTO(postResponse);
    }

    @Override
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        // Only allow to delete if the user is the author or has admin role
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new BadCredentialsException("You are not authorized to delete this post");
        }

        postRepo.delete(post);
    }

    private PostResponseDTO mapToPostResponseDTO(Post post) {
        PostResponseDTO responseDTO = new PostResponseDTO();
        responseDTO.setId(post.getId());
        responseDTO.setTitle(post.getTitle());
        responseDTO.setContent(post.getContent());
        responseDTO.setStatus(String.valueOf(post.getStatus()));
        responseDTO.setCreatedAt(post.getCreatedAt());
        responseDTO.setUpdatedAt(post.getUpdatedAt());

        // Set author details
        PostResponseDTO.AuthorResponseDTO authorDTO = new PostResponseDTO.AuthorResponseDTO();
        authorDTO.setId(post.getAuthor().getId());
        authorDTO.setEmail(post.getAuthor().getEmail());
        authorDTO.setName(post.getAuthor().getName());
        authorDTO.setRole(post.getAuthor().getRole().name());
        responseDTO.setAuthor(authorDTO);

        return responseDTO;
    }

}
//