package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.PostStatus;
import com.tedsaasfaha.blogapplication.entity.Role;
import com.tedsaasfaha.blogapplication.entity.User;
import com.tedsaasfaha.blogapplication.repository.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepo postRepo;

    @Override
    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepo.save(post);
    }

    @Override
    public Page<Post> getAllPublishedPosts(Pageable pageable) {
        return postRepo.findByStatus(PostStatus.PUBLISHED, pageable);
    }

    @Override
    public Page<Post> getPostByAuthor(User author, Pageable pageable) {
        return postRepo.findByAuthor(author, pageable);
    }

    @Override
    public Post getPostById(Long postId) {
        return postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    @Override
    public Post updatePost(Long postId, Post updatedPost, User currentUser) {
        Post post = getPostById(postId);

        // Only allow update if the user is the author or has admin role
        if (!post.getAuthor().equals(currentUser) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("You are not authorized to update this post");
        }

        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setStatus(updatedPost.getStatus());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepo.save(post);
    }

    @Override
    public void deletePost(Long postId, User currentUser) {

        Post post = getPostById(postId);

        // Only allow delete if the user is the author or has admin role
        if (!post.getAuthor().equals(currentUser) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("You are not authorized to delete this post");
        }

        postRepo.delete(post);
    }
}
