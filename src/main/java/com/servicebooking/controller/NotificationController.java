package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.Notification;
import com.servicebooking.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification management APIs")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // ================= GET NOTIFICATIONS =================
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get user notifications",
            description = "Fetch paginated notifications for the currently logged-in user"
    )
    public ResponseEntity<ApiResponse<PageResponse<Notification>>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                notificationService.getUserNotifications(page, size)
        );
    }

    // ================= MARK AS READ =================
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Mark notification as read",
            description = "Mark a specific notification as read for the logged-in user"
    )
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.markAsRead(id)
        );
    }
}