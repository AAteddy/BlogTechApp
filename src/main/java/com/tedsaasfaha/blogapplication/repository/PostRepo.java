
package com.tedsaasfaha.blogapplication.repository;

import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.PostStatus;
import com.tedsaasfaha.blogapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Post, Long> {

    Page<Post> findByAuthor(User author, Pageable pageable);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    Page<Post> findByAuthorAndStatus(User author, PostStatus status, Pageable pageable);
}
//