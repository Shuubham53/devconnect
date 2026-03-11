package com.Shubham.devconnect.repository;


import com.Shubham.devconnect.entity.Bookmark;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    boolean existsByUserAndPost(User user, Post post);
    Optional<Bookmark> findByUserAndPost(User user, Post post);
    List<Bookmark> findByUser(User user);
    long countByPost(Post post);
}
