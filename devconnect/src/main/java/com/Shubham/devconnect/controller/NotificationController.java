package com.Shubham.devconnect.controller;

import com.Shubham.devconnect.dto.response.NotificationResponse;
import com.Shubham.devconnect.exception.ApiResponse;
import com.Shubham.devconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.lang.String;

import java.util.List;



@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>>getMyNotifications(){
        List<NotificationResponse> responses = notificationService.getMyNotifications();
        return ResponseEntity.ok(ApiResponse.success("All my notification",responses));
    }
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>>markAsRead(@PathVariable Long id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>>markAllAsRead(){
        notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>>getUnreadCount(){
        long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(ApiResponse.success("unread count",count));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>>deleteNotification(@PathVariable Long id){
        String response = notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


}
