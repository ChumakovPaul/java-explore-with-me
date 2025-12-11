package ru.practicum.comment.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    final CommentRepository commentRepository;
    final CommentMapper commentMapper;
    final UserRepository userRepository;


    @Override
    public List<CommentDto> getUserComments(Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + authorId + " was not found"));
        List<Comment> comments = commentRepository.findByAuthorId(authorId);
        return comments.stream().map(commentMapper::toCommentDto).toList();
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Comment with id=" + commentId + " was not found"));
        commentRepository.delete(comment);
    }
}
