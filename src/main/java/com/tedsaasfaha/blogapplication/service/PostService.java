
package com.tedsaasfaha.blogapplication.service;

import com.tedsaasfaha.blogapplication.dto.PostCreationRequestDTO;
import com.tedsaasfaha.blogapplication.dto.PostResponseDTO;
import com.tedsaasfaha.blogapplication.entity.PostStatus;
import com.tedsaasfaha.blogapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface PostService {

    PostResponseDTO createPost(PostCreationRequestDTO postCreationRequestDTO,
                               User currentUser);

    Page<PostResponseDTO> getAllPublishedPosts(Pageable pageable);

    Page<PostResponseDTO> getAllPosts(Pageable pageable, User currentUser);

    Page<PostResponseDTO> getPostByAuthor(User author, Pageable pageable);

    PostResponseDTO getPostById(Long postId);

    PostResponseDTO updatePost(Long postId, PostCreationRequestDTO updatedPostDTO, User currentUser);

    PostResponseDTO updatePostStatus(Long postId, PostStatus newStatus, User currentUser);

    void deletePost(Long postId, User currentUser);

    void restorePost(Long postId, User currentUser);

    Page<PostResponseDTO> searchPosts(String keyword, Pageable pageable);

    Page<PostResponseDTO> filterPostsByStatus(PostStatus status, Pageable pageable);



}
//