
package com.tedsaasfaha.blogapplication.repository;

import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.entity.PostStatus;
import com.tedsaasfaha.blogapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PostRepo extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    Page<Post> findAllActivePosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.isDeleted = false")
    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.author = :author AND p.isDeleted = false")
    Page<Post> findByAuthor(User author, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = true AND p.deletedAt <= :thresholdDate")
    List<Post> findPostsForHardDeletion(@Param("thresholdDate") LocalDateTime thresholdDate);

    // Search by title or content (case-insensitive)
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isDeleted = false")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Filter by status
    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.isDeleted = false")
    Page<Post> filterByStatus(@Param("status") PostStatus status, Pageable pageable);

    // Filter by author
    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId AND p.isDeleted = false")
    Page<Post> filterByAuthor(@Param("authorId") Long authorId, Pageable pageable);

    // Filter by date range
    @Query("SELECT p FROM Post p WHERE p.createdAt BETWEEN :startDate AND :endDate AND p.isDeleted = false")
    Page<Post> filterByDateRange(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Combine filters
    @Query("SELECT p FROM Post p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:authorId IS NULL OR p.author.id = :authorId) " +
            "AND (:startDate IS NULL OR p.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR p.createdAt <= :endDate) AND p.isDeleted = false")
    Page<Post> searchAndFilter(@Param("keyword") String keyword,
                               @Param("status") PostStatus status,
                               @Param("authorId") Long authorId,
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate,
                               Pageable pageable);

}
//