package ru.practicum.ewmmain.controllers.admin_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.comment.CommentFullDto;
import ru.practicum.ewmmain.service.CommentService;
import ru.practicum.ewmmain.specification.admin_comments.AdminCommentRequestParameters;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentFullDto> getAllFiltered(AdminCommentRequestParameters parameters) {
        log.info("AdminCommentController GET getAllFiltered parameters: {}", parameters);
        return commentService.getAllFilteredAdmin(parameters);
    }

    @GetMapping("/user/{userId}")
    public List<CommentFullDto> getAllCommentsByAuthor(
            @Positive @PathVariable("userId") long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("AdminEventController GET getAllCommentsByAuthor userId: {}", userId);
        return commentService.getAllByUserAdmin(userId, from, size);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentFullDto> getAllCommentsByEvent(
            @Positive @PathVariable("eventId") long eventId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("AdminEventController GET getAllCommentsByEvent eventId: {}", eventId);
        return commentService.getAllByEventAdmin(eventId, from, size);
    }

    @PatchMapping("/publish/{commentId}")
    public CommentFullDto publish(
            @Positive @PathVariable("commentId") long commentId
    ) {
        log.info("AdminEventController PATCH publish commentId: {}", commentId);
        return commentService.publish(commentId);
    }

    @PatchMapping("/reject/{commentId}")
    public CommentFullDto reject(
            @Positive @PathVariable("commentId") long commentId
    ) {
        log.info("AdminEventController PATCH reject commentId: {}", commentId);
        return commentService.reject(commentId);
    }
}
