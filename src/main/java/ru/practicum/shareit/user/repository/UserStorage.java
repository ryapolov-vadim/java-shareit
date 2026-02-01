package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User getById(Long userId);

    User create(User user);

    User update(User user);

    void delete(Long userId);
}
