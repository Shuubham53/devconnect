package com.Shubham.devconnect.repository;


import com.Shubham.devconnect.entity.Follow;
import com.Shubham.devconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findByFollowing(User following); // get followers of a user

    List<Follow> findByFollower(User follower);   // get following of a user

    long countByFollowing(User following); // followers count

    long countByFollower(User follower);   // following count
}