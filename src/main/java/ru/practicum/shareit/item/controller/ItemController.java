package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getByIdItem(@PathVariable("itemId") Long itemId) {
        log.info("Вызван метод getByIdItem в ItemController");
        return itemService.getByIdItem(itemId);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody NewItemRequestDto newItemRequestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вызван метод createItem в ItemController");
        return itemService.createItem(newItemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestBody UpdateItemRequestDto updateItemRequestDto, @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
        log.info("Вызван метод updateItem в ItemController");
        return itemService.updateItem(updateItemRequestDto, userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Вызван метод getUserItems в ItemController");
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(value = "text", required = false) String text) {
        log.info("Вызван метод searchItem в ItemController");
        return itemService.searchItem(text);
    }
}
