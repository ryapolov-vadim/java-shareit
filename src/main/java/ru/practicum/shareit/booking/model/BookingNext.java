package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;

public interface BookingNext {
    Long getId();

    LocalDateTime getNextBooking();
}
