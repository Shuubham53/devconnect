package com.Shubham.devconnect.service;


import com.Shubham.devconnect.dto.request.UpdateProfileRequest;
import com.Shubham.devconnect.dto.response.ScoreHistoryResponse;
import com.Shubham.devconnect.dto.response.UserResponse;
import com.Shubham.devconnect.entity.ScoreHistory;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.repository.FollowRepository;
import com.Shubham.devconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ScoreService scoreService;

    // Get current logged in user from SecurityContext
    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    // Get user profile by username
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("user not found with username "+username));
        return mapToUserResponse(user);
    }

    // Update own profile
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        if(request.getName() != null){
            user.setName(request.getName());
        }
        if(request.getBio() != null){
            user.setBio(request.getBio());
        }
        if(request.getAvatarUrl() != null){
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if(request.getGithubUrl() != null){
            user.setGithubUrl(request.getGithubUrl());
        }
        if(request.getLinkedinUrl() != null){
            user.setLinkedinUrl(request.getLinkedinUrl());
        }
        if(request.getSkills() != null){
            user.setSkills(request.getSkills());
        }


        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    // Search users by name or username
    public List<UserResponse> searchUsers(String query) {
        return userRepository.searchUsers(query)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // Map User entity to UserResponse DTO
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getActualUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .githubUrl(user.getGithubUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .skills(user.getSkills())
                .score(user.getScore())
                .badge(user.getBadge())
                .role(String.valueOf(user.getRole()))
                .followersCount((int) followRepository.countByFollowing(user))
                .followingCount((int) followRepository.countByFollower(user))
                .createdAt(user.getCreatedAt())
                .build();
    }
    // Get all users — admin only
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // Ban user — set isActive false
    public String banUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
        return "User banned successfully";
    }

    // Unban user
    public String unbanUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
        return "User unbanned successfully";
    }

    // Delete user
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        userRepository.delete(user);
        return "User deleted successfully";
    }


    // Get top 10 developers by score
    public List<UserResponse> getLeaderboard() {
        List<User> topDevelopers = userRepository.findTopDevelopers(PageRequest.of(0,10));
        return topDevelopers.stream().map(this::mapToUserResponse).toList();
    }

    // Get score history for logged in user
    public List<ScoreHistoryResponse> getMyScoreHistory() {
        User currentUser = getCurrentUser();
        return scoreService.getMyScoreHistory(currentUser);

    }

    // Get my current score and badge
    public UserResponse getMyScore() {
        User currentUser = getCurrentUser();
        return mapToUserResponse(currentUser);

    }
}