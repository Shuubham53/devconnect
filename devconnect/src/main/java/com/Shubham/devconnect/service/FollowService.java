package com.Shubham.devconnect.service;

import com.Shubham.devconnect.dto.response.UserResponse;
import com.Shubham.devconnect.entity.Follow;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.NotificationType;
import com.Shubham.devconnect.repository.FollowRepository;
import com.Shubham.devconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final FollowRepository followRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    @Transactional
    public String followUser(Long userId) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        if(currentUser.getId().equals(targetUser.getId())){
            throw new RuntimeException("You cannot follow yourself");
        }

        if(followRepository.existsByFollowerAndFollowing(currentUser, targetUser)){
            throw new RuntimeException("Already following this user");
        }

        Follow follow = Follow.builder()
                .follower(currentUser)
                .following(targetUser)
                .build();

        followRepository.save(follow);
        // In FollowService — after followRepository.save(follow)
        notificationService.createNotification(
                currentUser,
                targetUser,
                NotificationType.FOLLOW,
                currentUser.getName() + " started following you",
                null
        );
        return "Successfully followed " + targetUser.getActualUsername();
    }

    @Transactional
    public String unfollowUser(Long userId) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        if(currentUser.getId().equals(targetUser.getId())){
            throw new RuntimeException("You cannot unfollow yourself");
        }

        Follow follow = followRepository
                .findByFollowerAndFollowing(currentUser, targetUser)
                .orElseThrow(() ->
                        new RuntimeException("You are not following this user"));

        followRepository.delete(follow);
        return "Successfully unfollowed " + targetUser.getActualUsername();
    }

    public List<UserResponse> getFollowers(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new RuntimeException("User not found"));

        return followRepository.findByFollowing(user)
                .stream()
                .map(follow -> mapToUserResponse(follow.getFollower()))
                .collect(Collectors.toList());
    }

    public List<UserResponse> getFollowing(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new RuntimeException("User not found"));

        return followRepository.findByFollower(user)
                .stream()
                .map(follow -> mapToUserResponse(follow.getFollowing()))
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getActualUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .skills(user.getSkills())
                .followersCount((int) followRepository.countByFollowing(user))
                .followingCount((int) followRepository.countByFollower(user))
                .build();
    }
}