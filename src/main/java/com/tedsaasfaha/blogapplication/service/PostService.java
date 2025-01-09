package com.tedsaasfaha.blogapplication.service;

import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Post createPost(Post post);

    Page<Post> getAllPublishedPosts(Pageable pageable);

    Page<Post> getPostByAuthor(User author, Pageable pageable);

    Post getPostById(Long postId);

    Post updatePost(Long postId, Post updatedPost, User currentUser);

    void deletePost(Long postId, User currentUser);
}