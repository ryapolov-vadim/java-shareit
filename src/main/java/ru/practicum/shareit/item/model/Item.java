package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;      //статус о том, доступна или нет вещь для аренды
    private User owner;             //владелец вещи
    private ItemRequest request;    //если вещь была создана по запросу другого пользователя, то в этом поле будет храниться ссылка на соответствующий запрос
}
