package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewBookingRequestDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @FutureOrPresent(message = "Дата не может быть в прошлом")
    private LocalDateTime start;

    @Future(message = "Дата не может быть в прошлом")
    private LocalDateTime end;

    private Long itemId;            // Возможно объект

    @AssertTrue(message = "Даты не могут быть пустыми и дата конца бронирования должна быть позже даты начала")
    public boolean isEndAfterStart() {
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
