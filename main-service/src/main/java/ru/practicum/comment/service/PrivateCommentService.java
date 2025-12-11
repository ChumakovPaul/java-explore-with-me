package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

public interface PrivateCommentService {
    CommentDto save(NewCommentDto newCommentDto, Long eventId, Long authorId);

    CommentDto update(NewCommentDto newCommentDto, Long authorId, Long commentId);

    void delete(Long authorId, Long commentId);

    CommentDto get(Long authorId, Long commentId);
}
