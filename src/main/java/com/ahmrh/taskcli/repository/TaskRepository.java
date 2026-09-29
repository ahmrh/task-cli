package com.ahmrh.taskcli.repository;

import com.ahmrh.taskcli.Task;
import com.ahmrh.taskcli.TaskStatus;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface TaskRepository {
    Task addTask(String description);
    void deleteTask(int id);
    void updateTask(int id, String description, TaskStatus taskStatus);
    List<Task> getListTask(TaskStatus taskStatus);
}
