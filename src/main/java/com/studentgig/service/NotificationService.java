package com.studentgig.service;

import com.studentgig.model.Notification;
import com.studentgig.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void notify(Long userId, String message) {
        notificationRepository.save(new Notification(userId, message));
    }

    public List<Map<String, Object>> getNotifications(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUserId().equals(userId)) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.findByUserId(userId).forEach(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    private Map<String, Object> toResponse(Notification n) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", n.getId());
        map.put("message", n.getMessage());
        map.put("read", n.isRead());
        map.put("createdAt", n.getCreatedAt());
        return map;
    }
}
