package com.cgesgin.todo_list_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgesgin.todo_list_api.model.dto.DataResponse;
import com.cgesgin.todo_list_api.model.entity.Task;
import com.cgesgin.todo_list_api.model.service.ITaskService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

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
    public ResponseEntity<DataResponse<List<Task>>> getAll() {
        List<Task> tasks = taskService.getAll();
        DataResponse<List<Task>> response = new DataResponse<>();

        if (tasks == null || tasks.isEmpty()) {
            response.setMessage(HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setData(tasks);
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
