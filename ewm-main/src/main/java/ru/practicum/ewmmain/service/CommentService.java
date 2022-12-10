package ru.practicum.ewmmain.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.comment.CommentFullDto;
import ru.practicum.ewmmain.dto.comment.CommentShortDto;
import ru.practicum.ewmmain.dto.comment.NewCommentDto;
import ru.practicum.ewmmain.specification.admin_comments.AdminCommentRequestParameters;

import java.util.List;

public interface CommentService {
    CommentShortDto add(Long userId, NewCommentDto newCommentDto);

    @Transactional
    CommentShortDto update(Long userId, Long commentId, NewCommentDto newCommentDto);

    CommentShortDto get(Long userId, Long commentId);

    @Transactional
    void delete(Long userId, Long commentId);

    @Transactional
    void deleteAll(Long userId);

    List<CommentShortDto> getAllByUser(Long userId, int from, int size);

    List<CommentShortDto> getAllByEvent(Long eventId, int from, int size);

    List<CommentFullDto> getAllByEventAdmin(Long eventId, int from, int size);

    List<CommentFullDto> getAllByUserAdmin(Long userId, int from, int size);

    List<CommentFullDto> getAllFilteredAdmin(AdminCommentRequestParameters parameters);

    @Transactional
    CommentFullDto publish(Long commentId);

    @Transactional
    CommentFullDto reject(Long commentId);
}
