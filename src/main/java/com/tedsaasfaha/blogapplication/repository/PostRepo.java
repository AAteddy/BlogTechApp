
package com.tedsaasfaha.blogapplication.repository;

import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.PostStatus;
import com.tedsaasfaha.blogapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepo extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    Page<Post> findAllActivePosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.isDeleted = false")
    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author = :author AND p.isDeleted = false")
    Page<Post> findByAuthor(User author, Pageable pageable);


//    Page<Post> findByAuthorAndStatus(User author, PostStatus status, Pageable pageable);
}
//