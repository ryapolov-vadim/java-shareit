package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Qualifier("UserServiceImpl")
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto create(NewUserRequestDto newUserRequestDto) {
        log.info("Вызван метод create в UserServiceImpl");
        Optional<User> user = userStorage.getAll().stream().filter(Objects::nonNull).filter(user1 -> user1.getEmail().equals(newUserRequestDto.getEmail())).findFirst();
        if (user.isPresent()) {
            throw new ConflictException("Email: " + newUserRequestDto.getEmail() + ", уже занят другим пользователем");
        }
        User userResult = UserMapper.mapToUser(newUserRequestDto);
        User userCreate = userStorage.create(userResult);
        log.info("Обработан метод create в UserServiceImpl");
        return UserMapper.mapToUserDto(userCreate);
    }

    @Override
    public void delete(Long userId) {
        log.info("Вызван метод delete в UserServiceImpl");
        userStorage.delete(userId);
        log.info("Обработан метод delete в UserServiceImpl");
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Вызван метод getAll в UserServiceImpl");
        List<UserDto> userDtoList = userStorage.getAll().stream().filter(Objects::nonNull).map(UserMapper::mapToUserDto).toList();
        log.info("Обработан метод getAll в UserServiceImpl");
        return userDtoList;
    }

    @Override
    public UserDto getById(Long userId) {
        log.info("Вызван метод getById в UserServiceImpl");
        User user = userStorage.getById(userId);
        log.info("Обработан метод getById в UserServiceImpl");
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(UpdateUserRequestDto updateUserRequestDto, Long userId) {
        log.info("Вызван метод update в UserServiceImpl");
        Optional<User> user = userStorage.getAll().stream().filter(Objects::nonNull).filter(user1 -> user1.getId() != userId && user1.getEmail().equals(updateUserRequestDto.getEmail())).findFirst();
        if (user.isPresent()) {
            throw new ConflictException("Email: " + updateUserRequestDto.getEmail() + ", уже занят другим пользователем");
        }
        User userResult = userStorage.getById(userId);
        if (userResult == null) {
            throw new ValidationException("Пользователь с ID: " + updateUserRequestDto.getId() + " ненайден");
        }
        User userUpdate = UserMapper.updateUserField(userResult, updateUserRequestDto);
        log.info("Обработан метод update в UserServiceImpl");
        return UserMapper.mapToUserDto(userUpdate);
    }
}
