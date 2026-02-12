package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.Notification;
import com.servicebooking.entity.User;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.NotificationRepository;
import com.servicebooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void createNotification(Long userId, String title, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    public ApiResponse<PageResponse<Notification>> getUserNotifications(int page, int size) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByUser(currentUser, pageable);

        PageResponse<Notification> response = new PageResponse<>(
                notificationPage.getContent(),
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages(),
                notificationPage.isLast()
        );

        return ApiResponse.success("Notifications fetched successfully", response);
    }

    @Transactional
    public ApiResponse<String> markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setIsRead(true);
        notificationRepository.save(notification);

        return ApiResponse.success("Notification marked as read");
    }
}
