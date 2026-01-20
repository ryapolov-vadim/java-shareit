package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .owner(UserMapper.mapToUserDto(item.getOwner()))
            .request(item.getRequest())
            .build();

        return itemDto;
    }

    public static Item mapToItem(NewItemRequestDto newItemRequest) {
        Item item = Item.builder()
            .name(newItemRequest.getName())
            .description(newItemRequest.getDescription())
            .owner(new User())
            .request(new ItemRequest())
            .build();

        return item;
    }
}
