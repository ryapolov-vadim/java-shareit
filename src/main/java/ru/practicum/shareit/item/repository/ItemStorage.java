package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getAll();

    Item getById(Long itemId);

    Item create(Item item);

    Item update(Item item);

    void delete(Long itemId);
}
