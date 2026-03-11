package com.Shubham.devconnect.service;

import com.Shubham.devconnect.dto.response.PostResponse;
import com.Shubham.devconnect.entity.Bookmark;
import com.Shubham.devconnect.entity.Like;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookMarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }

    public String bookmarkPost(Long postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(()->
                new RuntimeException("post not found"));
        if(bookMarkRepository.existsByUserAndPost(currentUser,post)){
            throw new RuntimeException("Already marked");
        }
        Bookmark bookmark = Bookmark.builder()
                .post(post)
                .user(currentUser)
                .build();
        bookMarkRepository.save(bookmark);

        return "Post book marked successfully";
    }

    public String removeBookmark(Long postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(()->
                new RuntimeException("post not found"));
        Bookmark bookmark = bookMarkRepository.findByUserAndPost(currentUser,post).orElseThrow(()->
                new RuntimeException("You have not book marked"));
        bookMarkRepository.delete(bookmark);
        return "Post removed from bookmark successfully";

    }

    public List<PostResponse> getMyBookmarks() {
        User currentUser = getCurrentUser();
        return bookMarkRepository.findByUser(currentUser)
                .stream()
                .map(bookmark -> mapToPostResponse(bookmark.getPost()))
                .collect(Collectors.toList());

    }

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags())
                .postType(post.getPostType())
                .status(post.getStatus())
                .viewCount(post.getViewCount())
                .authorName(post.getUser().getName())
                .authorUsername(post.getUser().getActualUsername())
                .authorId(post.getUser().getId())
                .likesCount((int) likeRepository.countByPost(post))
                .commentsCount((int) commentRepository.countByPost(post))
                .bookmarksCount((int) bookMarkRepository.countByPost(post))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}