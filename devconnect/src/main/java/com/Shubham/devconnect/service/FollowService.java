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
    private final ScoreService scoreService;

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
    public String toggleFollow(Long userId) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        if (currentUser.getId().equals(targetUser.getId()))
            throw new RuntimeException("You cannot follow yourself");

        if (followRepository.existsByFollowerAndFollowing(currentUser, targetUser)) {
            Follow follow = followRepository.findByFollowerAndFollowing(currentUser, targetUser)
                    .orElseThrow(() -> new RuntimeException("Follow not found"));
            followRepository.delete(follow);
            User refreshedTarget = userRepository.findById(targetUser.getId()).orElseThrow();
            User refreshedCurrent = userRepository.findById(currentUser.getId()).orElseThrow();
            scoreService.deductScore(refreshedTarget, 5, "Lost a follower");
            scoreService.deductScore(refreshedCurrent, 1, "Unfollowed a developer");
            return "unfollowed";
        } else {
            Follow follow = Follow.builder().follower(currentUser).following(targetUser).build();
            followRepository.save(follow);
            User refreshedTarget = userRepository.findById(targetUser.getId()).orElseThrow();
            User refreshedCurrent = userRepository.findById(currentUser.getId()).orElseThrow();
            scoreService.addScore(refreshedTarget, 5, "Received a new follower");
            scoreService.addScore(refreshedCurrent, 1, "Followed a developer");
            notificationService.createNotification(currentUser, targetUser,
                    NotificationType.FOLLOW, currentUser.getName() + " started following you", null);
            return "followed";
        }
    }

    // Add isFollowing check
    @Transactional(readOnly = true)
    public boolean isFollowing(Long userId) {
        User currentUser = getCurrentUser();
        User targetUser = userRepository.findById(userId).orElseThrow();
        return followRepository.existsByFollowerAndFollowing(currentUser, targetUser);
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