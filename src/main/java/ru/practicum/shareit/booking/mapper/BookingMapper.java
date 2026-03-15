package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.dto.UpdateBookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapToBooking(NewBookingRequestDto newBookingRequest) {
        Booking booking = Booking.builder().start(newBookingRequest.getStart()).end(newBookingRequest.getEnd()).build();
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = BookingDto.builder().id(booking.getId()).start(booking.getStart()).end(booking.getEnd()).item(booking.getItem()).booker(booking.getBooker()).status(booking.getStatus()).build();
        return bookingDto;
    }

    public static Booking updateBooking(UpdateBookingRequestDto updateBookingRequestDto, Booking booking) {
        Booking bookingUpdate = Booking.builder().start(updateBookingRequestDto.getStart()).end(updateBookingRequestDto.getEnd()).build();
        return bookingUpdate;
    }
}
