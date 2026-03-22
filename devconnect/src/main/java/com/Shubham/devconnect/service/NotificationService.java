package com.Shubham.devconnect.service;

import com.Shubham.devconnect.dto.response.NotificationResponse;
import com.Shubham.devconnect.entity.Notification;
import com.Shubham.devconnect.entity.Post;
import com.Shubham.devconnect.entity.User;
import com.Shubham.devconnect.enums.NotificationType;
import com.Shubham.devconnect.repository.NotificationRepository;
import com.Shubham.devconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("User not found"));
    }

    @Transactional
    public void createNotification(User sender, User receiver, NotificationType type, String message, Post post) {
        if (sender.getId().equals(receiver.getId())) return;

        Notification notification = Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .type(type)
                .message(message)
                .post(post)
                .build();
        Notification saved = notificationRepository.save(notification);

        NotificationResponse response = mapToNotificationResponse(saved);
        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/notifications",
                response
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = getCurrentUser();
        return notificationRepository
                .findByReceiverOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        List<Notification> notifications = notificationRepository
                .findByReceiverOrderByCreatedAtDesc(currentUser);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User currentUser = getCurrentUser();
        return notificationRepository
                .countByReceiverAndIsRead(currentUser, false);
    }

    @Transactional
    public String deleteNotification(Long notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));
        notificationRepository.delete(notification);
        return "Notification deleted successfully";
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .senderName(notification.getSender().getName())
                .senderUsername(notification.getSender().getActualUsername())
                .postId(notification.getPost() != null ?
                        notification.getPost().getId() : null)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}