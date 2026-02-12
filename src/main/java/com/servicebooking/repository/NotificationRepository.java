package com.servicebooking.repository;

import com.servicebooking.entity.Notification;
import com.servicebooking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser(User user, Pageable pageable);
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    Page<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead, Pageable pageable);
}
