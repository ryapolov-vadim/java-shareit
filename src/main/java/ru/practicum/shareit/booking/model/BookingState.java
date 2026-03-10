package ru.practicum.shareit.booking.model;

import java.util.Locale;

public enum BookingState {
    ALL,        // все

    CURRENT,    // текущие

    PAST,       // завершённые

    FUTURE,     // будущие

    WAITING,    // ожидающие подтверждения

    REJECTED;    // отклонённые

    public static BookingState from(String state) {
        switch (state.toUpperCase(Locale.ROOT)) {
            case "CURRENT":
                return CURRENT;
            case "PAST":
                return PAST;
            case "FUTURE":
                return FUTURE;
            case "WAITING":
                return WAITING;
            case "REJECTED":
                return REJECTED;
            default:
                return ALL;
        }
    }
}
