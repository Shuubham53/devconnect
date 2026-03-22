package com.Shubham.devconnect.service;

import com.Shubham.devconnect.entity.Like;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.NotificationType;
import com.Shubham.devconnect.repository.LikeRepository;
import com.Shubham.devconnect.repository.PostRepository;
import com.Shubham.devconnect.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ScoreService scoreService;

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public String toggleLike(Long postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new RuntimeException("Post not found"));

        Optional<Like> existing = likeRepository.findByUserAndPost(currentUser, post);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            User postOwner = userRepository.findById(post.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            scoreService.deductScore(postOwner, 5, "Like removed from post");
            return "Post unliked successfully";
        } else {
            Like like = Like.builder().post(post).user(currentUser).build();
            likeRepository.save(like);
            scoreService.addScore(post.getUser(), 5, "Received a like on post");
            notificationService.createNotification(
                    currentUser, post.getUser(),
                    NotificationType.LIKE,
                    currentUser.getName() + " liked your post",
                    post
            );
            return "Post liked successfully";
        }
    }
    public long getLikesCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()->
                new RuntimeException("post not found"));
        return likeRepository.countByPost(post);
    }
}