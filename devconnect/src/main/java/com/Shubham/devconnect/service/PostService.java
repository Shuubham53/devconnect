package com.Shubham.devconnect.service;

import com.Shubham.devconnect.dto.request.PostRequest;
import com.Shubham.devconnect.dto.response.PostResponse;
import com.Shubham.devconnect.entity.Follow;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.PostStatus;
import com.Shubham.devconnect.enums.PostType;
import com.Shubham.devconnect.repository.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
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

    public List<PostResponse> getFeed() {
        User currentUser = getCurrentUser();
        List<User> followingUsers = followRepository.findByFollower(currentUser)
                .stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());
        List<Post> posts = postRepository.findFeedPosts(followingUsers);
        return posts.stream()
                .map(this::mapToPostResponse)
                .toList();
    }
    @Cacheable("trending")
    public List<PostResponse> getTrending() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return postRepository.findTrendingPosts(since)
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }


    @CacheEvict(value = "trending", allEntries = true)
    public PostResponse createPost(PostRequest request) {
        User currentUser = getCurrentUser();
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .tags(request.getTags())
                .postType(request.getPostType())
                .imageUrl(request.getImageUrl()) // ADD THIS
                .viewCount(0)
                .user(currentUser)
                .build();
        post = postRepository.save(post);
        User refreshedUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        scoreService.addScore(refreshedUser, 10, "Created a post");
        return mapToPostResponse(post);



    }

    // Get all posts paginated
    public Page<PostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
         return postRepository.findByStatus(PostStatus.ACTIVE, pageable)
               .map(this::mapToPostResponse);
    }

    // Get single post by id
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Post not found with id: "+id));

        if(post.getStatus() != PostStatus.ACTIVE){
            throw new RuntimeException("Post not found");
        }
        post.setViewCount(post.getViewCount()+1);
        post = postRepository.save(post);

        return mapToPostResponse(post);
    }

    // Get posts by username
    public List<PostResponse> getPostsByUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Post> posts = postRepository.findByUserAndStatus(user,PostStatus.ACTIVE);
        return posts.stream().map( this::mapToPostResponse).toList();

    }

    // Update post
    public PostResponse updatePost(Long id, PostRequest request) {
        User currentuser = getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Post not found"));
        if(!post.getUser().getId().equals(currentuser.getId())){
            throw new  RuntimeException("Unauthorized, you cannot update post");
        }

        if(request.getTitle() != null){
            post.setTitle(request.getTitle());
        }
        if(request.getContent() != null){
            post.setContent(request.getContent());
        }
        if(request.getTags()!= null){
            post.setTags(request.getTags());
        }
        if(request.getPostType()!= null){
            post.setPostType(request.getPostType());
        }
        post = postRepository.save(post);
        return mapToPostResponse(post);

    }

    // Delete post
    @CacheEvict(value = "trending", allEntries = true)
    public String deletePost(Long id) {
        User currentuser = getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Post not found"));
        if(!post.getUser().getId().equals(currentuser.getId())){
            throw new  RuntimeException("Unauthorized, you cannot delete post");
        }
        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);
         return "Post deleted successfully";
    }

    // Search posts
    public List<PostResponse> searchPosts(String query) {
        List<Post> posts = postRepository.searchPosts(query);
        return posts.stream().map(this::mapToPostResponse).toList();
    }

    // Get posts by tag
    public List<PostResponse> getPostsByTag(String tag) {
        List<Post> posts = postRepository.findByTag(tag);
        return posts.stream().map(this::mapToPostResponse).toList();
    }

    // Map Post to PostResponse — boilerplate, I'll give this
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
                .imageUrl(post.getImageUrl())
                .authorUsername(post.getUser().getActualUsername())
                .authorId(post.getUser().getId())
                .likesCount((int) likeRepository.countByPost(post))
                .commentsCount((int) commentRepository.countByPost(post))
                .bookmarksCount((int) bookmarkRepository.countByPost(post))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
    // Get ALL posts including flagged/deleted — admin only
    public List<PostResponse> getAllPostsAdmin() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    // Flag a post
    public String flagPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Post not found"));
        post.setStatus(PostStatus.FLAGGED);
        postRepository.save(post);
        return "Post flagged successfully";
    }

    // Delete any post — admin
    public String deletePostAdmin(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Post not found"));
        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);
        return "Post deleted successfully";
    }
}