package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Slf4j
@Qualifier("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage, @Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto getByIdItem(Long itemId) {
        log.info("Вызван метод getByIdItem в ItemServiceImpl");
        Item item = itemStorage.getById(itemId);
        log.info("Обработан метод getByIdItem в ItemServiceImpl");
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto createItem(NewItemRequestDto newItemRequestDto, Long userId) {
        log.info("Вызван метод createItem в ItemServiceImpl");
        User user = validateUser(userId);
        Item item = ItemMapper.mapToItem(newItemRequestDto);
        item.setOwner(user);
        Item itemResult = itemStorage.create(item);
        log.info("Обработан метод createItem в ItemServiceImpl");
        return ItemMapper.mapToItemDto(itemResult);
    }

    @Override
    public ItemDto updateItem(UpdateItemRequestDto updateItemRequestDto, Long userId, Long itemId) {
        log.info("Вызван метод updateItem в ItemServiceImpl");
        User user = validateUser(userId);
        Item item = validItem(itemId);
        Item itemUpdate = ItemMapper.updateItemField(item, updateItemRequestDto);
        Item itemResult = itemStorage.update(itemUpdate);
        log.info("Обработан метод updateItem в ItemServiceImpl");
        return ItemMapper.mapToItemDto(itemResult);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        log.info("Вызван метод getUserItems в ItemServiceImpl");
        User user = validateUser(userId);
        List<ItemDto> itemList = itemStorage.getAll().stream().filter(Objects::nonNull).filter(item -> item.getOwner().equals(user)).map(ItemMapper::mapToItemDto).toList();
        log.info("Обработан метод getUserItems в ItemServiceImpl");
        return itemList;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Вызван метод searchItem в ItemServiceImpl");
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        String searchText = text.trim().toLowerCase(Locale.ROOT);

        List<ItemDto> itemList = itemStorage.getAll().stream().filter(Objects::nonNull).filter(item -> item.getAvailable()).filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(searchText) || item.getDescription().toLowerCase(Locale.ROOT).contains(searchText)).map(ItemMapper::mapToItemDto).toList();
        log.info("Обработан метод searchItem в ItemServiceImpl");
        return itemList;
    }

    @Override
    public void deleteItem(Long itemId, Long userId) {
        log.info("Вызван метод deleteItem в ItemServiceImpl");
        User user = validateUser(userId);
        Item item = validItem(itemId);
        itemStorage.delete(itemId);
        log.info("Обработан метод deleteItem в ItemServiceImpl");
    }

    private User validateUser(Long userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " ненайден");
        }
        return user;
    }

    private Item validItem(Long itemId) {
        Item item = itemStorage.getById(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь с ID: " + itemId + " ненайден");
        }
        return item;
    }
}
