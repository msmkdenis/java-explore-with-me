package ru.practicum.ewmmain.controllers.public_api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmmain.service.CommentService;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/comments")
public class PublicCommentController {


    private final CommentService commentService;

    @GetMapping
    public Map<Long, Double> getAllEvents(
            //PublicEventsRequestParameters parameters
    ) {
        return commentService.getAllByPublicUserWithRating();
    }
}
