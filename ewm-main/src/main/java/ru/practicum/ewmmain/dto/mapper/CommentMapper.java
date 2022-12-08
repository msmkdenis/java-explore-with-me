package ru.practicum.ewmmain.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmmain.dto.comment.CommentFullDto;
import ru.practicum.ewmmain.dto.comment.CommentShortDto;
import ru.practicum.ewmmain.dto.comment.NewCommentDto;
import ru.practicum.ewmmain.entity.Comment;
import ru.practicum.ewmmain.entity.Event;
import ru.practicum.ewmmain.entity.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .author(author)
                .event(event)
                .created(LocalDateTime.now())
                .eventScore(newCommentDto.getScore())
                .build();
    }

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .moderated(comment.getModerated())
                .edited(comment.getUpdated())
                .status(comment.getStatus())
                .score(comment.getEventScore())
                .build();
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .status(comment.getStatus())
                .score(comment.getEventScore())
                .build();
    }
}
