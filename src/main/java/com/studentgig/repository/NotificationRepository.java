package com.studentgig.repository;

import com.studentgig.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository {
    private final Map<Long, Notification> notificationsById = new HashMap<>();
    private long nextId = 1;

    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(nextId++);
        }
        notificationsById.put(notification.getId(), notification);
        return notification;
    }

    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(notificationsById.get(id));
    }

    public List<Notification> findByUserId(Long userId) {
        return notificationsById.values().stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public long countUnreadByUserId(Long userId) {
        return notificationsById.values().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .count();
    }
}
