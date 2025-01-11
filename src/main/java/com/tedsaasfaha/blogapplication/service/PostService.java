
package com.tedsaasfaha.blogapplication.service;

import com.tedsaasfaha.blogapplication.dto.PostCreationRequestDTO;
import com.tedsaasfaha.blogapplication.dto.PostResponseDTO;
import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostResponseDTO createPost(PostCreationRequestDTO postCreationRequestDTO,
                               CustomUserPrinciple customUserPrinciple);

    Page<PostResponseDTO> getAllPublishedPosts(Pageable pageable);

    Page<PostResponseDTO> getPostByAuthor(User author, Pageable pageable);

    PostResponseDTO getPostById(Long postId);

    PostResponseDTO updatePost(Long postId, Post updatedPost, User currentUser);

    void deletePost(Long postId, User currentUser);
}
//