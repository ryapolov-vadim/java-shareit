package ru.practicum.shareit.item.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = CommentDto.builder().id(comment.getId()).text(comment.getText()).itemId(comment.getItem().getId()).authorName(comment.getAuthor().getName()).authorId(comment.getAuthor().getId()).created(comment.getCreated()).build();
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto, User author, Item item) {
        Comment comment = Comment.builder().text(commentDto.getText()).author(author).item(item).created(LocalDateTime.now()).build();
        return comment;
    }
}
