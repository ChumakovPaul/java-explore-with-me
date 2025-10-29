package ru.practicum.comment.service;


import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface AdminCommentService {

    List<CommentDto> getUserComments(Long authorId);

    void deleteComment(Long commentId);
}
