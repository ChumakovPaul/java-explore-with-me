package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {
    final UserRepository userRepository;
    final EventRepository eventRepository;
    final CommentRepository commentRepository;
    final CommentMapper commentMapper;


    @Override
    public CommentDto save(NewCommentDto newCommentDto, Long eventId, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + authorId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        Comment comment = commentRepository.save(commentMapper.toComment(newCommentDto,
                eventId,
                authorId,
                LocalDateTime.now(),
                null));
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto update(NewCommentDto newCommentDto, Long authorId, Long commentId) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId,authorId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Comment with id=" + commentId + " by user id=" + authorId + " was not found"));
        comment.setText(newCommentDto.getText());
        comment.setUpdated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long authorId, Long commentId) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId,authorId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Comment with id=" + commentId + " by user id=" + authorId + " was not found"));
        commentRepository.delete(comment);
    }

    @Override
    public CommentDto get(Long authorId, Long commentId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + authorId + " was not found"));
        Comment comment = commentRepository.findByIdAndAuthorId(commentId,authorId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Comment with id=" + commentId + " by user id=" + authorId + " was not found"));
        return commentMapper.toCommentDto(comment);
    }
}
