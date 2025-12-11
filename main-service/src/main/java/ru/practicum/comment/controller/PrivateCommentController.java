package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.PrivateCommentService;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PrivateCommentController {
    final PrivateCommentService privateCommentService;

    @PostMapping("/{authorId}/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @PathVariable Long authorId,
            @PathVariable Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {

        log.info("Creating new comment by userId={} for eventId={}", authorId, eventId);
        return privateCommentService.save(newCommentDto, eventId, authorId);
    }

    @PatchMapping("/{authorId}/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(
            @PathVariable Long authorId,
            @PathVariable Long commentId,
            @Valid @RequestBody NewCommentDto newCommentDto) {

        log.info("Updating comment id={} by userId={}", commentId, authorId);
        return privateCommentService.update(newCommentDto, authorId, commentId);
    }

    @DeleteMapping("/{authorId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long authorId,
            @PathVariable Long commentId) {

        log.info("Delete comment id={} by userId={}", commentId, authorId);
        privateCommentService.delete(authorId, commentId);
    }

    @GetMapping("{authorId}/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable Long authorId,
                                 @PathVariable Long commentId) {
        log.info("Getting comment id={} by userId={}", commentId, authorId);
        return privateCommentService.get(authorId, commentId);
    }
}
