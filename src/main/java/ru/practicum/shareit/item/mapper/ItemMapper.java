package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder().id(item.getId()).name(item.getName()).description(item.getDescription()).available(item.getAvailable()).build();

        return itemDto;
    }

    public static Item mapToItem(NewItemRequestDto newItemRequest) {
        Item item = Item.builder().name(newItemRequest.getName()).description(newItemRequest.getDescription()).available(newItemRequest.getAvailable()).owner(new User()).request(new ItemRequest()).build();

        return item;
    }

    public static Item updateItemField(Item item, UpdateItemRequestDto itemRequestDto) {
        if (itemRequestDto.hasName()) {
            item.setName(itemRequestDto.getName());
        }

        if (itemRequestDto.hasDescription()) {
            item.setDescription(itemRequestDto.getDescription());
        }

        if (itemRequestDto.hasAvailable()) {
            item.setAvailable(itemRequestDto.getAvailable());
        }
        return item;
    }
}
