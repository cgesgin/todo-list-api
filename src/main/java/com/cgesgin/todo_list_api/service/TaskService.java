package com.cgesgin.todo_list_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cgesgin.todo_list_api.model.entity.Task;
import com.cgesgin.todo_list_api.model.entity.User;
import com.cgesgin.todo_list_api.model.service.ITaskService;
import com.cgesgin.todo_list_api.repository.TaskRepository;
import com.cgesgin.todo_list_api.repository.UserRepository;

@Service
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<Task> getAll() {
        User user = getAuthenticatedUser();
        if (user != null) {
            return taskRepository.findAll()
                    .stream()
                    .filter(task -> task.getUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public Task save(Task task) {
        User user = getAuthenticatedUser();

        if (task.getId() != null) {
            task.setId(null);
        }

        if (user != null) {
            task.setUser(user);
            return taskRepository.save(task);
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        User user = getAuthenticatedUser();
        taskRepository.findById(id).ifPresent(task -> {
            if (task.getUser().getId().equals(user.getId())) {
                taskRepository.deleteById(id);
            }
        });
    }

    @Override
    public Task getById(Long id) {
        User user = getAuthenticatedUser();
        return taskRepository.findById(id)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .orElse(null);
    }

    @Override
    public Task update(Task task) {
        User user = getAuthenticatedUser();
        if (user != null && taskRepository.findById(task.getId()).isPresent()) {
            task.setUser(user);
            return taskRepository.save(task);
        }
        return null;
    }

    @Override
    public Page<Task> getFilteredAndSortedTasks(String title, Boolean completed, String sortBy, String direction, int page, int limit) {
        Sort sort = Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);

        if (title != null && completed != null) {
            return taskRepository.findByTitleContainingAndCompleted(title, completed, pageable);
        } else if (title != null) {
            return taskRepository.findByTitleContaining(title, pageable);
        } else if (completed != null) {
            return taskRepository.findByCompleted(completed, pageable);
        } else {
            return taskRepository.findAll(pageable);
        }
    }
}
