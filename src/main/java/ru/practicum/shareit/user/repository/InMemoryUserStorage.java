package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        log.info("Вызван метод getAll в InMemoryUserStorage");
        List<User> userList = users.values().stream().toList();
        log.info("Обработан метод getAll в InMemoryUserStorage");
        return userList;
    }

    @Override
    public User getById(Long userId) {
        log.info("Вызван метод getById в InMemoryUserStorage");
        User user = users.get(userId);
        log.info("Обработан метод getById в InMemoryUserStorage");
        return user;
    }

    @Override
    public User create(User user) {
        log.info("Вызван метод create в InMemoryUserStorage");
        user.setId(counter());
        users.put(user.getId(), user);
        log.info("Обработан метод create в InMemoryUserStorage");
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Вызван метод update в InMemoryUserStorage");
        users.put(user.getId(), user);
        log.info("Обработан метод update в InMemoryUserStorage");
        return user;
    }

    @Override
    public void delete(Long userId) {
        log.info("Вызван метод delete в InMemoryUserStorage");
        users.remove(userId);
        log.info("Обработан метод delete в InMemoryUserStorage");
    }

    private Long counter() {
        Long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);

        return ++currentMaxId;
    }
}
