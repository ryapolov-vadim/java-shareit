package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@Qualifier("BookingServiceImpl")
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(NewBookingRequestDto newBookingRequestDto, Long booker) {
        log.info("Вызван метод createBooking в BookingServiceImpl");
        User user = validateUser(booker);
        Item item = validItem(newBookingRequestDto.getItemId());

        if (user.equals(item.getOwner())) {
            String error = "Владелец вещи не может забронировать её сам у себя";
            log.error(error);
            throw new ValidationException(error);
        }

        if (!item.getAvailable()) {
            String error = "Вещь на данный момент недоступна для бронирования";
            log.error(error);
            throw new ValidationException(error);
        }

        if (validBookingOfDate(newBookingRequestDto)) {
            String error = "Бронирование отклонено - на эти даты и время вещь забронирована";
            log.error(error);
            throw new ValidationException(error);
        }

        Booking booking = BookingMapper.mapToBooking(newBookingRequestDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        Booking bookingResult = bookingRepository.save(booking);
        log.info("Обработан метод createBooking в BookingServiceImpl");
        return BookingMapper.mapToBookingDto(bookingResult);
    }

    @Override
    @Transactional
    public BookingDto processBookingDecision(Long bookingId, Long userId, Boolean decision) {
        log.info("Вызван метод processBookingDecision в BookingServiceImpl");
        if (decision == null) {
            String error = "Параметр запроса approved, должен иметь значения true или false";
            log.warn(error);
            throw new NotFoundException(error);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ValidationException("Пользователь с ID: " + userId + " ненайден"));

        Booking booking = bookingRepository.findByIdWithItemAndOwner(bookingId).orElseThrow(() -> new NotFoundException(String.format("Бронирование с ID: %d не существует", bookingId)));

        if (booking.getItem().getOwner().equals(user)) {
            if (booking.getStatus().equals(BookingStatus.WAITING)) {
                if (!bookingRepository.checkBookingTimeOverlap(booking.getStart(), booking.getEnd(), booking.getItem().getId(), BookingStatus.APPROVED)) {
                    booking.setStatus(decision ? BookingStatus.APPROVED : BookingStatus.REJECTED);
                    bookingRepository.save(booking);
                } else {
                    if (decision) {
                        String error = "Ошибка: на эту дату уже есть бронь!";
                        log.error(error);
                        throw new ValidationException(error);
                    } else {
                        booking.setStatus(BookingStatus.REJECTED);
                        bookingRepository.save(booking);
                    }
                }
            } else {
                String error = "Ошибка: статус уже изменён!";
                log.error(error);
                throw new ValidationException(error);
            }
        } else {
            String error = String.format("Операция изменения статуса отклонена, пользователь с ID: %d не является хозяином вещи!", userId);
            log.warn(error);
            throw new AccessDeniedException(error);
        }
        log.info("Обработан метод processBookingDecision в BookingServiceImpl");
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        log.info("Вызван метод getBooking в BookingServiceImpl");
        Booking booking = bookingRepository.findByIdWithItemAndOwner(bookingId).orElseThrow(() -> new NotFoundException(String.format("Бронирование с ID: %d не существует", bookingId)));
        if ((!booking.getBooker().getId().equals(userId)) && (!booking.getItem().getOwner().getId().equals(userId))) {
            String error = "Просматривать бронь может только хозяин вещи или автор брони";
            log.warn(error);
            throw new AccessDeniedException(error); // Возможно не то исключение
        }
        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
        log.info("Обработан метод getBooking в BookingServiceImpl");
        return bookingDto;
    }

    @Override
    public List<BookingDto> getBookingsByBooker(Long bookerId, BookingState bookingState) {
        log.info("Вызван метод getBookingsByBooker в BookingServiceImpl");
        validateUser(bookerId);
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        Boolean booker = true;

        List<Booking> bookings = spinTheWheel(bookerId, bookingState, pageable, booker);

        log.info("Обработан метод getBookingsByBooker в BookingServiceImpl");
        return bookings.stream().filter(Objects::nonNull).map(BookingMapper::mapToBookingDto).toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId, BookingState bookingState) {
        log.info("Вызван метод getBookingsByOwner в BookingServiceImpl");
        validateUser(ownerId);
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        Boolean owner = false;

        List<Booking> bookings = spinTheWheel(ownerId, bookingState, pageable, owner);

        log.info("Обработан метод getBookingsByOwner в BookingServiceImpl");
        return bookings.stream().filter(Objects::nonNull).map(BookingMapper::mapToBookingDto).toList();
    }

    private User validateUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            String error = String.format("Пользователь с ID: %d ненайден", userId);
            log.warn(error);
            throw new NotFoundException(error);
        }
        return optionalUser.get();
    }

    private Item validItem(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findByWithOwner(itemId);
        if (!optionalItem.isPresent()) {
            String error = String.format("Вещь с ID: %d ненайдена", itemId);
            log.warn(error);
            throw new NotFoundException(error);
        }
        return optionalItem.get();
    }

    private Boolean validBookingOfDate(NewBookingRequestDto newBookingRequestDto) {
        return bookingRepository.checkBookingTimeOverlap(newBookingRequestDto.getStart(), newBookingRequestDto.getEnd(), newBookingRequestDto.getItemId(), BookingStatus.APPROVED);
    }

    private List<Booking> spinTheWheel(Long userId, BookingState state, Pageable pageable, Boolean BookerAndOwner) {
        if (userId == null) {
            String error = "ID пользователя должен быть указан";
            log.warn(error);
            throw new NotFoundException(error);
        }

        switch (state) {
            case ALL:
                return BookerAndOwner ? bookingRepository.getBookingsAllByBookerId(userId, pageable) : bookingRepository.getBookingsAllByOwnerId(userId, pageable);
            case CURRENT:
                return BookerAndOwner ? bookingRepository.getBookingsCurrentByBookerId(userId, LocalDateTime.now(), BookingStatus.APPROVED, pageable) : bookingRepository.getBookingsCurrentByOwnerId(userId, LocalDateTime.now(), BookingStatus.APPROVED, pageable);
            case PAST:
                return BookerAndOwner ? bookingRepository.getBookingsPastByBookerId(userId, LocalDateTime.now(), BookingStatus.APPROVED, pageable) : bookingRepository.getBookingsPastByOwnerId(userId, LocalDateTime.now(), BookingStatus.APPROVED, pageable);
            case FUTURE:
                return BookerAndOwner ? bookingRepository.getBookingsFutureByBookerId(userId, LocalDateTime.now(), BookingStatus.APPROVED, pageable) : bookingRepository.getBookingsFutureByOwnerId(userId, LocalDateTime.now(), BookingStatus.APPROVED, pageable);
            case WAITING:
                return BookerAndOwner ? bookingRepository.getBookingsWaitingAndRejectedByBookerId(userId, BookingStatus.WAITING, pageable) : bookingRepository.getBookingsWaitingAndRejectedByOwnerId(userId, BookingStatus.WAITING, pageable);
            case REJECTED:
                return BookerAndOwner ? bookingRepository.getBookingsWaitingAndRejectedByBookerId(userId, BookingStatus.REJECTED, pageable) : bookingRepository.getBookingsWaitingAndRejectedByOwnerId(userId, BookingStatus.REJECTED, pageable);
        }
        return List.of();
    }
}
