package com.cgesgin.todo_list_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cgesgin.todo_list_api.model.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    
}
