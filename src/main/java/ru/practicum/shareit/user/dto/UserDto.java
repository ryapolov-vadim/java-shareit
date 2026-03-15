package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
