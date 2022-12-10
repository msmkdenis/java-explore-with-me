package ru.practicum.ewmmain.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.comment.CommentFullDto;
import ru.practicum.ewmmain.dto.comment.CommentShortDto;
import ru.practicum.ewmmain.dto.comment.NewCommentDto;
import ru.practicum.ewmmain.dto.mapper.CommentMapper;
import ru.practicum.ewmmain.entity.*;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.exception.ForbiddenError;
import ru.practicum.ewmmain.repository.CommentRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.repository.UserRepository;
import ru.practicum.ewmmain.service.CommentService;
import ru.practicum.ewmmain.specification.admin_comments.AdminCommentRequestParameters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentShortDto add(Long userId, NewCommentDto newCommentDto) {
        checkCommentExistence(userId, newCommentDto.getEventId());
        User author = findUserOrThrow(userId);
        Event event = findEventOrThrow(newCommentDto.getEventId());
        validateEventForComment(event);
        Comment comment = commentRepository.save(CommentMapper.toComment(newCommentDto, author, event));
        comment.setStatus(CommentStatus.PENDING);
        return CommentMapper.toCommentShortDto(comment);
    }

    @Override
    @Transactional
    public CommentShortDto update(Long userId, Long commentId, NewCommentDto newCommentDto) {

        Comment commentToUpdate = findByIdAndAuthorOrThrow(commentId, userId);
        commentToUpdate.setText(newCommentDto.getText());
        commentToUpdate.setEventScore(newCommentDto.getScore());
        commentToUpdate.setUpdated(LocalDateTime.now());
        commentToUpdate.setStatus(CommentStatus.PENDING);

        return CommentMapper.toCommentShortDto(commentToUpdate);
    }

    @Override
    public List<CommentShortDto> getAllByUser(Long userId, int from, int size) {
        findUserOrThrow(userId);
        Page<Comment> comments = commentRepository.findAllByAuthorId(userId, PageRequest.of(getPageNumber(from, size), size));
        return comments.stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentShortDto> getAllByEvent(Long eventId, int from, int size) {
        findEventOrThrow(eventId);
        Page<Comment> comments = commentRepository.findAllByEventId(eventId, CommentStatus.PUBLISHED, PageRequest.of(getPageNumber(from, size), size));
        return comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentShortDto get(Long userId, Long commentId) {
        Comment comment = findByIdAndAuthorOrThrow(commentId, userId);
        return CommentMapper.toCommentShortDto(comment);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment commentToDelete = findByIdAndAuthorOrThrow(commentId, userId);
        commentRepository.delete(commentToDelete);
    }

    @Override
    @Transactional
    public void deleteAll(Long userId) {
        findUserOrThrow(userId);
        commentRepository.deleteAllByAuthorId(userId);
    }

    @Override
    public List<CommentFullDto> getAllFilteredAdmin(AdminCommentRequestParameters parameters) {
        List<Comment> comments = commentRepository.findAll(parameters.toSpecification(), parameters.toPageable())
                .stream().collect(Collectors.toList());
        return comments.stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }

    @Override
    public List<CommentFullDto> getAllByEventAdmin(Long eventId, int from, int size) {
        findEventOrThrow(eventId);
        return commentRepository.findAllByEventIdAdmin(eventId, PageRequest.of(getPageNumber(from, size), size))
                .stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentFullDto> getAllByUserAdmin(Long userId, int from, int size) {
        findUserOrThrow(userId);
        return commentRepository.findAllByAuthorId(userId, PageRequest.of(getPageNumber(from, size), size))
                .stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentFullDto publish(Long commentId) {
        Comment comment = findCommentOrThrow(commentId);
        comment.setStatus(CommentStatus.PUBLISHED);
        comment.setModerated(LocalDateTime.now());
        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    @Transactional
    public CommentFullDto reject(Long commentId) {
        Comment comment = findCommentOrThrow(commentId);
        comment.setStatus(CommentStatus.REJECTED);
        comment.setModerated(LocalDateTime.now());
        return CommentMapper.toCommentFullDto(comment);
    }

    private void validateEventForComment(Event event) {
        if (!(event.getEventStatus().equals(EventStatus.PUBLISHED))) {
            throw new ForbiddenError(String.format("Событие id=%d еще не опубликовано", event.getId()));
        }
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Пользователь id=%d не найден!", id)));
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event id=%d не найден!", id)));
    }

    private void checkCommentExistence(Long authorId, Long eventId) {
        Optional<Comment> comment = commentRepository.findCommentByIdAndAuthorId(authorId, eventId);
        if (comment.isPresent()) {
            throw new ForbiddenError(String.format("Пользователь id=%d уже оставил отзыв на событие id=%d", authorId, eventId));
        }
    }

    private Comment findCommentOrThrow(Long id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Комментарий id=%d не найден!", id)));
    }

    private Comment findByIdAndAuthorOrThrow(Long commentId, Long userId) {
        return commentRepository.findCommentByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User id=%d не является автором комментария id=%d!", userId, commentId)));

    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}
