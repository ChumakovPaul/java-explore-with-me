package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.AdminCommentService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/comments")
public class AdminCommentController {

    final AdminCommentService adminCommentService;

    @GetMapping("/user/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getUserComments(@PathVariable Long authorId) {
        log.info("Getting comments by user Id={}", authorId);
        return adminCommentService.getUserComments(authorId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.info("Delete comment Id={}", commentId);
        adminCommentService.deleteComment(commentId);
    }
}