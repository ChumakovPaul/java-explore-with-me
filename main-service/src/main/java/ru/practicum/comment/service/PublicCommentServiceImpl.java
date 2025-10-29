package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DataNotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {

    final EventRepository eventRepository;
    final CommentRepository commentRepository;
    final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getEventComments(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Event with id=" + eventId + " was not found"));
        List<Comment> comments = commentRepository.findByEventId(eventId);
        return comments.stream().map(commentMapper::toCommentDto).toList();
    }
}
