package com.Shubham.devconnect.repository;


import com.Shubham.devconnect.entity.Like;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndPost(User user, Post post);
    Optional<Like> findByUserAndPost(User user, Post post);
    long countByPost(Post post);
}
