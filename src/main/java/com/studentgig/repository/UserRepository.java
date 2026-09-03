package com.studentgig.repository;

import com.studentgig.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<Long, User> usersById = new HashMap<>();
    private final Map<String, Long> usersByEmail = new HashMap<>();
    private long nextId = 1;

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail().toLowerCase(), user.getId());
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public Optional<User> findByEmail(String email) {
        Long id = usersByEmail.get(email.toLowerCase());
        if (id == null) return Optional.empty();
        return Optional.ofNullable(usersById.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email.toLowerCase());
    }

    public void setNextId(long id) {
        this.nextId = id;
    }
}
