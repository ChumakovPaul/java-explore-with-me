package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.PublicCommentService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {

    final PublicCommentService publicCommentService;

    @GetMapping("/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getEventComments(@PathVariable Long eventId) {
        log.info("Getting comments for eventId={}", eventId);
        return publicCommentService.getEventComments(eventId);
    }
}
