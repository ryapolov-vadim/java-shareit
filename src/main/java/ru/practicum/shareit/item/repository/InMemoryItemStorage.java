package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getAll() {
        log.info("Вызван метод getAll в InMemoryItemStorage");
        List<Item> itemList = items.values().stream().toList();
        log.info("Обработан метод getAll в InMemoryItemStorage");
        return itemList;
    }

    @Override
    public Item getById(Long itemId) {
        log.info("Вызван метод getById в InMemoryItemStorage");
        Item item = items.get(itemId);
        log.info("Обработан метод getById в InMemoryItemStorage");
        return item;
    }

    @Override
    public Item create(Item item) {
        log.info("Вызван метод create в InMemoryItemStorage");
        item.setId(counter());
        items.put(item.getId(), item);
        log.info("Обработан метод create в InMemoryItemStorage");
        return item;
    }

    @Override
    public Item update(Item item) {
        log.info("Вызван метод update в InMemoryItemStorage");
        items.put(item.getId(), item);
        log.info("Обработан метод update в InMemoryItemStorage");
        return item;
    }

    @Override
    public void delete(Long itemId) {
        log.info("Вызван метод delete в InMemoryItemStorage");
        items.remove(itemId);
        log.info("Обработан метод delete в InMemoryItemStorage");
    }

    private Long counter() {
        Long currentMaxId = items.keySet().stream().mapToLong(id -> id).max().orElse(0);

        return ++currentMaxId;
    }
}
