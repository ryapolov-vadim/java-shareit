package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingLast;
import ru.practicum.shareit.booking.model.BookingNext;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Qualifier("ItemServiceImpl")
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto getByIdItem(Long itemId, Long userId) {
        log.info("Вызван метод getByIdItem в ItemServiceImpl");
        Item item = itemRepository.findByWithOwner(itemId).orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " ненайдена"));
        List<Comment> comments = commentRepository.getCommentByItemId(itemId);
        if (item.getOwner().getId().equals(userId)) {
            List<Long> itemIds = List.of(item.getId());
            List<BookingLast> bookingLasts = bookingRepository.getBookingsByLast(itemIds, LocalDateTime.now(), BookingStatus.APPROVED);
            List<BookingNext> bookingNexts = bookingRepository.getBookingsByNext(itemIds, LocalDateTime.now(), BookingStatus.APPROVED);
            ItemDto itemDto = ItemMapper.mapToItemDto(item);
            itemDto.setOwner(UserMapper.mapToUserDto(item.getOwner()));

            if (!bookingLasts.isEmpty()) {
                itemDto.setLastBooking(bookingLasts.getFirst().getLastBooking());
            }

            if (!bookingNexts.isEmpty()) {
                itemDto.setNextBooking(bookingNexts.getFirst().getNextBooking());
            }
            itemDto.setComments(comments.stream().filter(Objects::nonNull).map(CommentMapper::toCommentDto).collect(Collectors.toList()));
            return itemDto;
        }
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        itemDto.setOwner(UserMapper.mapToUserDto(item.getOwner()));
        itemDto.setComments(comments.stream().filter(Objects::nonNull).map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        log.info("Обработан метод getByIdItem в ItemServiceImpl");
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto createItem(NewItemRequestDto newItemRequestDto, Long userId) {
        log.info("Вызван метод createItem в ItemServiceImpl");
        User user = validateUser(userId);
        Item item = ItemMapper.mapToItem(newItemRequestDto);
        item.setOwner(user);
        Item itemResult = itemRepository.save(item);
        log.info("Обработан метод createItem в ItemServiceImpl");
        return toItemDtoWithUser(item, user);
    }

    @Override
    @Transactional
    public ItemDto updateItem(UpdateItemRequestDto updateItemRequestDto, Long userId, Long itemId) {
        log.info("Вызван метод updateItem в ItemServiceImpl");
        User user = validateUser(userId);
        Item item = validItem(itemId, userId);
        Item itemUpdate = ItemMapper.updateItemField(item, updateItemRequestDto);
        Item itemResult = itemRepository.save(itemUpdate);
        log.info("Обработан метод updateItem в ItemServiceImpl");
        return toItemDtoWithUser(itemResult, user);
    }

    @Override
    public List<ItemOwnerDto> getUserItems(Long userId) {
        log.info("Вызван метод getUserItems в ItemServiceImpl");
        User user = validateUser(userId);

        List<ItemOwnerDto> itemOwnerDtoList = itemRepository.findByOwner_Id(userId).stream().filter(Objects::nonNull).map(ItemMapper::mapToItemOwnerDto).toList();
        if (itemOwnerDtoList.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = itemOwnerDtoList.stream().filter(Objects::nonNull).map(ItemOwnerDto::getId).collect(Collectors.toList());
        List<BookingLast> bookingLasts = bookingRepository.getBookingsByLast(itemIds, LocalDateTime.now(), BookingStatus.APPROVED);
        List<BookingNext> bookingNexts = bookingRepository.getBookingsByNext(itemIds, LocalDateTime.now(), BookingStatus.APPROVED);
        List<ItemOwnerDto> itemOwnerDto = setBookingDates(itemOwnerDtoList, bookingLasts, bookingNexts);

        List<Comment> comments = commentRepository.getCommentByAuthorIdAndItemId(itemIds);
        List<ItemOwnerDto> itemOwnerDtoResult = setComments(itemOwnerDto, comments);

        log.info("Обработан метод getUserItems в ItemServiceImpl");
        return itemOwnerDtoResult;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Вызван метод searchItem в ItemServiceImpl");
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String searchText = text.trim().toLowerCase(Locale.ROOT);

        List<ItemDto> itemList = itemRepository.findBySearchItem(searchText).stream().filter(Objects::nonNull).map(item -> toItemDtoWithUser(item, item.getOwner())).toList();
        log.info("Обработан метод searchItem в ItemServiceImpl");
        return itemList;
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        log.info("Вызван метод addComment в ItemServiceImpl");
        Optional<Booking> booking = bookingRepository
            .getByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (!booking.isPresent()) {
            String error = "Оставить отзыв может только пользователь, " +
                "который брал эту вещь в аренду, и только после окончания срока аренды";
            log.warn(error);
            throw new ValidationException(error);
        }

        Comment comment = CommentMapper.toComment(commentDto, booking.get().getBooker(), booking.get().getItem());
        Comment commentResult = commentRepository.save(comment);
        log.info("Обработан метод addComment в ItemServiceImpl");
        return CommentMapper.toCommentDto(commentResult);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        log.info("Вызван метод deleteItem в ItemServiceImpl");
        User user = validateUser(userId);
        Item item = validItem(itemId, userId);
        itemRepository.delete(item);
        log.info("Обработан метод deleteItem в ItemServiceImpl");
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

    private Item validItem(Long itemId, Long ownerId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (!optionalItem.isPresent()) {
            String error = String.format("Вещь с ID: %d ненайдена", itemId);
            log.warn(error);
            throw new NotFoundException(error);
        }

        if (!optionalItem.get().getOwner().getId().equals(ownerId)) {
            String error = "Пользователь не является хозяином вещи!";
            log.error(error);
            throw new ValidationException(error);
        }
        return optionalItem.get();
    }

    private ItemDto toItemDtoWithUser(Item item, User user) {
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        UserDto userDto = UserMapper.mapToUserDto(user);
        itemDto.setOwner(userDto);
        return itemDto;
    }

    private List<ItemOwnerDto> setBookingDates(List<ItemOwnerDto> ownerDto, List<BookingLast> bookingLasts, List<BookingNext> bookingNexts) {
        Map<Long, LocalDateTime> bookingLastsMap = bookingLasts.stream()
            .filter(Objects::nonNull).collect(Collectors.toMap(BookingLast::getId, BookingLast::getLastBooking));
        Map<Long, LocalDateTime> bookingNextMap = bookingNexts.stream()
            .filter(Objects::nonNull).collect(Collectors.toMap(BookingNext::getId, BookingNext::getNextBooking));

        for (ItemOwnerDto ownerDtoItem : ownerDto) {
            if (bookingLastsMap.containsKey(ownerDtoItem.getId())) {
                ownerDtoItem.setLastBooking(bookingLastsMap.get(ownerDtoItem.getId()));
            }
            if (bookingNextMap.containsKey(ownerDtoItem.getId())) {
                ownerDtoItem.setNextBooking(bookingNextMap.get(ownerDtoItem.getId()));
            }
        }
        return ownerDto;
    }

    private List<ItemOwnerDto> setComments(List<ItemOwnerDto> itemOwnerDtos, List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return itemOwnerDtos;
        }
        Map<Long, List<Comment>> commentsMap = comments.stream()
            .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        for (ItemOwnerDto ownerDtoItem : itemOwnerDtos) {
            ownerDtoItem.setComments(commentsMap.get(ownerDtoItem.getId()).stream()
                .filter(Objects::nonNull).map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }
        return itemOwnerDtos;
    }
}
