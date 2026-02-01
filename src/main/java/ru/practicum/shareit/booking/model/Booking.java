package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
    private Long id;
    @NotNull(message = "Дата и время начала бронирования не может быть пустым")
    private LocalDateTime start;    //дата и время начала бронирования
    @NotNull(message = "Дата и время конца бронирования не может быть пустым")
    private LocalDateTime end;      //дата и время конца бронирования
    private Item item;              //вещь, которую пользователь бронирует
    private User booker;            //пользователь, который осуществляет бронирование
    private Status status;          //статус бронирования

    private enum Status {
        WAITING,    //новое бронирование, ожидает одобрения
        APPROVED,   //бронирование подтверждено владельцем
        REJECTED,   //бронирование отклонено владельцем
        CANCELED   //бронирование отменено создателем
    }
}
