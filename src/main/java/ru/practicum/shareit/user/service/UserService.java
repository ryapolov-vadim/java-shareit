package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(Long userId);

    UserDto create(NewUserRequestDto newUserRequestDto);

    UserDto update(UpdateUserRequestDto updateUserRequestDto, Long userId);

    void delete(Long userId);
}
