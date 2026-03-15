package com.Shubham.devconnect.service;

import com.Shubham.devconnect.dto.response.ScoreHistoryResponse;
import com.Shubham.devconnect.entity.ScoreHistory;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.repository.ScoreHistoryRepository;
import com.Shubham.devconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final UserRepository userRepository;
    private final ScoreHistoryRepository scoreHistoryRepository;

    // Core method — add points to user
    @CacheEvict(value = "leaderboard", allEntries = true)
    public void addScore(User user, Integer points, String reason) {

        user.setScore(user.getScore()+points);
        updateBadge(user);
        user = userRepository.save(user);
        ScoreHistory history = ScoreHistory.builder()
                .reason(reason)
                .points(points)
                .user(user)
                .build();
        scoreHistoryRepository.save(history);
    }

    // Update badge based on score
    private void updateBadge(User user) {
        if(user.getScore() >= 1000){
            user.setBadge("LEGEND");
        }
        else if(user.getScore() >= 500){
            user.setBadge("EXPERT");
        }
        else if(user.getScore() >= 200){
            user.setBadge("INTERMEDIATE");
        }
        else if(user.getScore() >= 50){
            user.setBadge("BEGINNER");
        }else{
            user.setBadge("NEWCOMER");
        }
    }

    // Get score history for logged in user
    public List<ScoreHistoryResponse> getMyScoreHistory(User user) {
        return scoreHistoryRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(history -> ScoreHistoryResponse.builder()
                        .id(history.getId())
                        .points(history.getPoints())
                        .reason(history.getReason())
                        .createdAt(history.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

}
