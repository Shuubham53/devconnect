package com.Shubham.devconnect.repository;


import com.Shubham.devconnect.entity.Comment;
import com.Shubham.devconnect.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Get top level comments only (no replies)
    List<Comment> findByPostAndParentCommentIsNull(Post post);

    // Count comments for a post
    long countByPost(Post post);
}