package com.Shubham.devconnect.service;

import com.Shubham.devconnect.dto.request.CommentRequest;
import com.Shubham.devconnect.dto.response.CommentResponse;
import com.Shubham.devconnect.entity.Comment;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.NotificationType;
import com.Shubham.devconnect.repository.CommentRepository;
import com.Shubham.devconnect.repository.PostRepository;
import com.Shubham.devconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
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

    // Add comment on post
    public CommentResponse addComment(Long postId, CommentRequest request) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new RuntimeException("post not found"));
        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(currentUser)
                .post(post)
                .parentComment(null)
                .build();
        comment = commentRepository.save(comment);
        scoreService.addScore(post.getUser(), 2, "Received a comment on post");
        scoreService.addScore(currentUser, 3, "Wrote a comment");
        notificationService.createNotification(
                currentUser,
                post.getUser(),
                NotificationType.COMMENT,
                currentUser.getName() + " commented on your post",
                post
        );
        return mapToCommentResponse(comment);

    }

    // Reply to a comment
    public CommentResponse replyToComment(Long commentId, CommentRequest request) {
        User currentUser = getCurrentUser();
        Comment parentComment = commentRepository.findById(commentId).orElseThrow(()->
                new RuntimeException("comment not found"));
        Comment comment = Comment.builder()
                .post(parentComment.getPost())
                .content(request.getContent())
                .user(currentUser)
                .parentComment(parentComment)
                .build();
        comment = commentRepository.save(comment);
        notificationService.createNotification(
                currentUser,
                parentComment.getUser(),
                NotificationType.REPLY,
                currentUser.getName() + " replied to your comment",
                parentComment.getPost()
        );
        return mapToCommentResponse(comment);

    }

    // Get all comments for a post
    public List<CommentResponse> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new RuntimeException("post not found"));
        List<Comment> topComments = commentRepository.findByPostAndParentCommentIsNull(post);
        return topComments.stream().map(this::mapToCommentResponse).toList();
    }

    // Edit comment
    public CommentResponse editComment(Long commentId, CommentRequest request) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->
                new RuntimeException("comment not found"));
        if(!currentUser.getId().equals(comment.getUser().getId())){
            throw new RuntimeException("Unauthorized — you cannot edit this comment");
        }
        comment.setContent(request.getContent());
        comment.setIsEdited(true);
        comment = commentRepository.save(comment);
        return mapToCommentResponse(comment);
    }

    // Delete comment
    public String deleteComment(Long commentId) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->
                new RuntimeException("comment not found"));
        if(!currentUser.getId().equals(comment.getUser().getId())){
            throw new RuntimeException("Unauthorized — you cannot edit this comment");
        }
        commentRepository.delete(comment);
        return "comment deleted successfully";

    }

    // Map Comment to CommentResponse
    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .isEdited(comment.getIsEdited())
                .authorName(comment.getUser().getName())
                .authorUsername(comment.getUser().getActualUsername())
                .authorId(comment.getUser().getId())
                .postId(comment.getPost().getId())
                .parentCommentId(comment.getParentComment() != null ?
                        comment.getParentComment().getId() : null)
                .replies(comment.getReplies().stream()
                        .map(this::mapToCommentResponse)
                        .collect(Collectors.toList()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}