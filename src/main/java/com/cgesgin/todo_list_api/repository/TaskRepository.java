package com.cgesgin.todo_list_api.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cgesgin.todo_list_api.model.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTitleContaining(String title, Sort sort);

    List<Task> findByCompleted(Boolean completed, Sort sort);

    List<Task> findByTitleContainingAndCompleted(String title, Boolean completed, Sort sort);
}
