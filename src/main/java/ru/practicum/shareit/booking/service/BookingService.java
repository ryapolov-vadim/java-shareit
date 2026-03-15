package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(NewBookingRequestDto newBookingRequestDto, Long booker);

    BookingDto processBookingDecision(Long bookingId, Long userId, Boolean decision);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookingsByBooker(Long bookerId, BookingState bookingState);

    List<BookingDto> getBookingsByOwner(Long ownerId, BookingState bookingState);
}
