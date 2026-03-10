package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@Qualifier("UserServiceImpl")
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequestDto newUserRequestDto) {
        log.info("Вызван метод create в UserServiceImpl");
        validateEmail(newUserRequestDto.getEmail());
        User userResult = UserMapper.mapToUser(newUserRequestDto);
        User userCreate = userRepository.save(userResult);
        log.info("Обработан метод create в UserServiceImpl");
        return UserMapper.mapToUserDto(userCreate);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        log.info("Вызван метод delete в UserServiceImpl");
        userRepository.deleteById(userId);
        log.info("Обработан метод delete в UserServiceImpl");
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Вызван метод getAll в UserServiceImpl");
        List<UserDto> userDtoList = userRepository.findAll().stream().filter(Objects::nonNull).map(UserMapper::mapToUserDto).toList();
        log.info("Обработан метод getAll в UserServiceImpl");
        return userDtoList;
    }

    @Override
    public UserDto getById(Long userId) {
        log.info("Вызван метод getById в UserServiceImpl");
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            String error = String.format("Пользователь с ID: %d ненайден", userId);
            log.warn(error);
            throw new NotFoundException(error);
        }
        log.info("Обработан метод getById в UserServiceImpl");
        return UserMapper.mapToUserDto(user.get());
    }

    @Override
    @Transactional
    public UserDto update(UpdateUserRequestDto updateUserRequestDto, Long userId) {
        log.info("Вызван метод update в UserServiceImpl");
        validateEmail(updateUserRequestDto.getEmail());
        User userResult = userRepository.findById(userId).orElseThrow(() -> new ValidationException("Пользователь с ID: " + updateUserRequestDto.getId() + " ненайден"));

        User userUpdate = UserMapper.updateUserField(userResult, updateUserRequestDto);
        User userUpdateResult = userRepository.save(userUpdate);

        log.info("Обработан метод update в UserServiceImpl");
        return UserMapper.mapToUserDto(userUpdateResult);
    }

    private void validateEmail(String email) {
        Optional<User> user = userRepository.findByEmailIgnoreCase(email);
        if (user.isPresent()) {
            String error = String.format("Email: %s, уже занят другим пользователем", email);
            log.warn(error);
            throw new ConflictException(error);
        }
    }
}
