package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class ItemOwnerDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime lastBooking; // дата последнего бронирования
    private LocalDateTime nextBooking; // дата ближайшего бронирования
    private Boolean available;
    private ItemRequest request;
    private List<CommentDto> comments;
}
