package ru.practicum.shareit.item.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Комментарий обязателен к заполнению")
    @Size(max = 255, message = "Количество символов недолжно быть больше 255 символов")
    private String text;
    private Long itemId;
    private String authorName;
    private Long authorId;
    private LocalDateTime created;
}
