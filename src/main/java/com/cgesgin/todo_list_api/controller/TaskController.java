package com.cgesgin.todo_list_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgesgin.todo_list_api.model.dto.DataResponse;
import com.cgesgin.todo_list_api.model.entity.Task;
import com.cgesgin.todo_list_api.model.service.ITaskService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Task", description = "API for task operations")
public class TaskController {

    private ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<DataResponse<List<Task>>> getAll(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Boolean completed,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int limit)
    {
        DataResponse<List<Task>> response = new DataResponse<>();
        Page<Task> taskPage;
        
        if (title == null && completed == null) {
            taskPage = taskService.getFilteredAndSortedTasks(null, null, sortBy, direction, page, limit);
        } else {
            taskPage = taskService.getFilteredAndSortedTasks(title, completed, sortBy, direction, page, limit);
        }
        
        response.setData(taskPage.getContent());
        response.setPage(taskPage.getNumber());
        response.setLimit(taskPage.getSize());
        response.setTotal(taskPage.getTotalElements());
        response.setMessage(HttpStatus.OK.toString());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks")
    public ResponseEntity<DataResponse<Task>> save(@RequestBody Task entity) {
        var task = taskService.save(entity);
        DataResponse<Task> response = new DataResponse<>();
        if (task == null) {
            response.setMessage(HttpStatus.BAD_REQUEST.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.setData(task);
        response.setMessage(HttpStatus.CREATED.toString());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<DataResponse<Task>> update(@PathVariable Long id, @RequestBody Task entity) {
        var task = taskService.getById(id);
        DataResponse<Task> response = new DataResponse<>();
        if (task == null) {
            response.setMessage(HttpStatus.BAD_REQUEST.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        entity.setId(id);
        var updatedTask = taskService.update(entity);
        response.setData(updatedTask);
        response.setMessage(HttpStatus.ACCEPTED.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<DataResponse<Task>> getById(@PathVariable Long id) {
        var task = taskService.getById(id);
        DataResponse<Task> response = new DataResponse<>();
        if (task == null) {
            response.setMessage(HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.setData(task);
        response.setMessage(HttpStatus.OK.toString());
        return ResponseEntity.ok(response);
    }

}
