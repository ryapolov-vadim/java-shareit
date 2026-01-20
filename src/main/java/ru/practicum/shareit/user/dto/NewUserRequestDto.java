package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequestDto {
    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Email
    @NotBlank(message = "Email не может быть пустым")
    private String email;
}
