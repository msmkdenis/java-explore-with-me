package ru.practicum.ewmmain.controllers.private_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.comment.CommentShortDto;
import ru.practicum.ewmmain.dto.comment.NewCommentDto;
import ru.practicum.ewmmain.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentShortDto add(
            @Positive @PathVariable("userId") long userId,
            @RequestBody @Valid NewCommentDto newCommentDto
            ) {
        log.info("PrivateCommentController POST add получен newCommentDto: {}", newCommentDto);
        return commentService.add(userId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentShortDto update(
            @Positive @PathVariable("userId") long userId,
            @Positive @PathVariable("commentId") long commentId,
            @RequestBody @Valid NewCommentDto newCommentDto
    ) {
        log.info("PrivateCommentController PATCH update получен newCommentDto: {}", newCommentDto);
        return commentService.update(userId, commentId, newCommentDto);
    }

    @GetMapping("/{commentId}")
    public CommentShortDto get(
            @Positive @PathVariable("userId") long userId,
            @Positive @PathVariable("commentId") long commentId
    ) {
        log.info("PrivateCommentController GET get commentId {}", commentId);
        return commentService.get(userId, commentId);
    }

    @GetMapping
    public List<CommentShortDto> getAllByUser(
            @Positive @PathVariable("userId") long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("PrivateCommentController GET getAll");
        return commentService.getAllByUser(userId, from, size);
    }

    @DeleteMapping("/{commentId}")
    public void delete(
            @Positive @PathVariable("userId") long userId,
            @Positive @PathVariable("commentId") long commentId
    ) {
        log.info("PrivateCommentController DELETE delete commentId: {}", commentId);
        commentService.delete(userId, commentId);
    }

    @DeleteMapping
    public void deleteAll(
            @Positive @PathVariable("userId") long userId
    ) {
        log.info("PrivateCommentController DELETE deleteAll by userId: {}", userId);
        commentService.deleteAll(userId);
    }
}
