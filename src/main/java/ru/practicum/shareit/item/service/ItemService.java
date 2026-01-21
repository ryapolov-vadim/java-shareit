package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.List;

public interface ItemService {

    ItemDto getByIdItem(Long itemId);

    ItemDto createItem(NewItemRequestDto newItemRequestDto, Long userId);

    ItemDto updateItem(UpdateItemRequestDto updateItemRequestDto, Long userId, Long itemId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchItem(String text);

    void deleteItem(Long itemId, Long userId);
}
