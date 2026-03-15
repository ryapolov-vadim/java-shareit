package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.List;

public interface ItemService {

    ItemDto getByIdItem(Long itemId, Long userId);

    ItemDto createItem(NewItemRequestDto newItemRequestDto, Long userId);

    ItemDto updateItem(UpdateItemRequestDto updateItemRequestDto, Long userId, Long itemId);

    List<ItemOwnerDto> getUserItems(Long userId);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(CommentDto commentDto, Long userId, Long itemId);

    void deleteItem(Long itemId, Long userId);
}
