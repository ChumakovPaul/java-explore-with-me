package ru.practicum.comment.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentMapper {

    public Comment toComment(NewCommentDto newCommentDto, long eventId, long authorId, LocalDateTime created, LocalDateTime updated) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEventId(eventId);
        comment.setAuthorId(authorId);
        comment.setCreated(created);
        comment.setUpdated(updated);
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setEventId(comment.getEventId());
        commentDto.setAuthorId(comment.getAuthorId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setUpdated(comment.getUpdated());
        return commentDto;
    }
}
