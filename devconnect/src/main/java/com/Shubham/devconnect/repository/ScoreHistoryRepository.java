package com.Shubham.devconnect.repository;


import com.Shubham.devconnect.entity.ScoreHistory;
import com.Shubham.devconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, Long> {
    List<ScoreHistory> findByUserOrderByCreatedAtDesc(User user);
}