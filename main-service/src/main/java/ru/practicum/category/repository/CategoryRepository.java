package ru.practicum.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
