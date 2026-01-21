package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();

        return userDto;
    }

    public static User mapToUser(NewUserRequestDto newUserRequest) {
        User user = User.builder()
            .name(newUserRequest.getName())
            .email(newUserRequest.getEmail())
            .build();

        return user;
    }

    public static User updateUserField(User user, UpdateUserRequestDto updateUserRequestDto) {
        if (updateUserRequestDto.hasName()) {
            user.setName(updateUserRequestDto.getName());
        }

        if (updateUserRequestDto.hasEmail()) {
            user.setEmail(updateUserRequestDto.getEmail());
        }

        return user;
    }
}
