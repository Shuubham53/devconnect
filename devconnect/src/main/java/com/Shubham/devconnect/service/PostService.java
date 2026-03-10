package com.Shubham.devconnect.service;

import com.Shubham.devconnect.dto.request.PostRequest;
import com.Shubham.devconnect.dto.response.PostResponse;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.PostStatus;
import com.Shubham.devconnect.enums.PostType;
import com.Shubham.devconnect.repository.PostRepository;
import com.Shubham.devconnect.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }


    public PostResponse createPost(PostRequest request) {
        User user = getCurrentUser();
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .tags(request.getTags())
                .postType(request.getPostType())
                .viewCount(0)
                .user(user)
                .build();
        post = postRepository.save(post);
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
                .authorUsername(post.getUser().getActualUsername())
                .authorId(post.getUser().getId())
                .likesCount(0)
                .commentsCount(0)
                .bookmarksCount(0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}