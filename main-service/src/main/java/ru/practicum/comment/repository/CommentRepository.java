package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndAuthorId(Long eventId, Long authorId);

    List<Comment> findByEventId(Long eventId);

    List<Comment> findByAuthorId(Long authorId);
}
