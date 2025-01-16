
package com.tedsaasfaha.blogapplication.service;


import com.tedsaasfaha.blogapplication.entity.Post;
import com.tedsaasfaha.blogapplication.repository.PostRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(PostCleanupService.class);

    @Value("${post.retention.period:1}")
    private int retentionPeriod;

    private final PostRepo postRepo;

    public PostCleanupService(PostRepo postRepo) {
        this.postRepo = postRepo;
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2:00 AM
    public void deleteOldDeletedPosts() {
        logger.info("Starting scheduled job to clean up soft-deleted posts.");

        // Calculate the threshold date for deletion
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(retentionPeriod);

        // Fetch posts eligible for hard deletion
        List<Post> postsToDelete = postRepo.findPostsForHardDeletion(thresholdDate);

        if (!postsToDelete.isEmpty()) {
            logger.info("Found {} posts eligible for hard deletion.", postsToDelete.size());
            postRepo.deleteAll(postsToDelete);
            logger.info("Successfully deleted {} posts.", postsToDelete.size());
        } else {
            logger.info("No posts found for hard deletion.");
        }

        logger.info("Completed scheduled job to clean up soft-deleted posts.");
    }
}
//
//