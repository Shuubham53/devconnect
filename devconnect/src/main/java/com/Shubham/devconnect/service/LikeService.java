package com.Shubham.devconnect.service;

import com.Shubham.devconnect.entity.Like;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.repository.LikeRepository;
import com.Shubham.devconnect.repository.PostRepository;
import com.Shubham.devconnect.repository.UserRepository;
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

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }

    public String likePost(Long postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(()->
                new RuntimeException("post not found"));
        if(likeRepository.existsByUserAndPost(currentUser,post)){
            throw new RuntimeException("Already liked");
        }
        Like like = Like.builder()
                .post(post)
                .user(currentUser)
                .build();
        likeRepository.save(like);
        return "Post liked successfully";
    }

    public String unlikePost(Long postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(()->
                new RuntimeException("post not found"));
        Like like = likeRepository.findByUserAndPost(currentUser,post).orElseThrow(()->
                new RuntimeException("You haven't liked this post"));
        likeRepository.delete(like);
        return "Post Unliked successfully";

    }

    public long getLikesCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()->
                new RuntimeException("post not found"));
        return likeRepository.countByPost(post);
    }
}