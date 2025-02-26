package com.cgesgin.todo_list_api.model.service;

import java.util.List;

import com.cgesgin.todo_list_api.model.entity.Task;

public interface ITaskService {

    public List<Task> getAll();
    public Task save(Task task);
    public void deleteById(Long id);
    public Task getById(Long id);
    public Task update(Task task);
    public List<Task> getFilteredAndSortedTasks(String title, Boolean completed, String sortBy, String direction);

}