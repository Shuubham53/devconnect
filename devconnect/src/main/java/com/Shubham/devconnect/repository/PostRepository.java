package com.Shubham.devconnect.repository;

import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Get all active posts with pagination
    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    // Get posts by user
    List<Post> findByUserAndStatus(User user, PostStatus status);

    // Search posts by keyword in title or content
    @Query("SELECT p FROM Post p WHERE p.status = 'ACTIVE' AND (" +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.tags) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Post> searchPosts(@Param("query") String query);

    // Get posts by tag
    @Query("SELECT p FROM Post p WHERE p.status = 'ACTIVE' AND " +
            "LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%'))")
    List<Post> findByTag(@Param("tag") String tag);

    // Get posts from followed users (feed)
    @Query("SELECT p FROM Post p WHERE p.user IN :users " +
            "AND p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    List<Post> findFeedPosts(@Param("users") List<User> users);
}