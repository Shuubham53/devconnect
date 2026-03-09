package com.Shubham.devconnect.entity;


import com.Shubham.devconnect.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 500)
    private String bio;

    private String avatarUrl;
    private String githubUrl;
    private String linkedinUrl;
    private String skills;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Followers - users who follow me
    @ManyToMany
    @JoinTable(
            name = "follows",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    @Builder.Default
    private Set<User> followers = new HashSet<>();

    // Following - users I follow
    @ManyToMany(mappedBy = "followers")
    @Builder.Default
    private Set<User> following = new HashSet<>();
}