package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.HttpHeadersConstants;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody NewBookingRequestDto newBookingRequestDto, @RequestHeader(HttpHeadersConstants.USER_ID_HEADER) Long booker) {
        log.info("Вызван метод createBooking в BookingController от пользователя с ID: {}", booker);
        return bookingService.createBooking(newBookingRequestDto, booker);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto processBookingDecision(@PathVariable("bookingId") Long bookingId, @RequestHeader(HttpHeadersConstants.USER_ID_HEADER) Long userId, @RequestParam(value = "approved") Boolean approved) {
        log.info("Вызван метод processBookingDecision в BookingController от пользователя с ID: {}", userId);
        return bookingService.processBookingDecision(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") Long bookingId, @RequestHeader(HttpHeadersConstants.USER_ID_HEADER) Long userId) {
        log.info("Вызван метод getBooking в BookingController от пользователя с ID: {}", userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByBooker(@RequestHeader(HttpHeadersConstants.USER_ID_HEADER) Long bookerId, @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("Вызван метод getBookingsByBooker в BookingController от пользователя с ID: {}", bookerId);
        return bookingService.getBookingsByBooker(bookerId, BookingState.from(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(HttpHeadersConstants.USER_ID_HEADER) Long ownerId, @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("Вызван метод getBookingsByOwner в BookingController от пользователя с ID: {}", ownerId);
        return bookingService.getBookingsByOwner(ownerId, BookingState.from(state));
    }
}
