package com.cgesgin.todo_list_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cgesgin.todo_list_api.model.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByTitleContaining(String title, Pageable pageable);
    Page<Task> findByCompleted(Boolean completed, Pageable pageable);
    Page<Task> findByTitleContainingAndCompleted(String title, Boolean completed, Pageable pageable);
}
