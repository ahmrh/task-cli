package com.ahmrh.taskcli.repository.impl;

import com.ahmrh.taskcli.Task;
import com.ahmrh.taskcli.TaskStatus;
import com.ahmrh.taskcli.repository.TaskRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class JsonTaskRepository implements TaskRepository {

    private int lastId;

    private static final Log log = LogFactory.getLog(JsonTaskRepository.class);

    private final JsonMapper mapper = JsonMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    private final Path file =
            Path.of(System.getProperty("user.home"), ".task-cli", "tasks.json");


    public JsonTaskRepository() {

        // load() creates the file if it's missing and returns an empty list,
        // so a fresh install gives lastId = 0
        this.lastId = load().stream()
                .mapToInt(Task::id)
                .max()
                .orElse(0);
    }

    @Override
    public Task addTask(String description) {
        List<Task> tasks = load();
        int id = ++lastId;
        Date now = new Date();
        Task task = new Task(id, description, TaskStatus.TODO, now, now);

        tasks.add(task);
        write(tasks);
        return task;
    }

    @Override
    public void deleteTask(int id) {
        List<Task> tasks = load();
        boolean removed = tasks.removeIf(t -> t.id() == id);
        if(removed) write(tasks);
    }

    @Override
    public void updateTask(int id, String description, TaskStatus taskStatus) {
        List<Task> tasks = load();

        for (int i = 0; i < tasks.size(); i++) {
            Task current = tasks.get(i);
            if (current.id() == id) {
                Task updated = new Task(
                        id,
                        description != null ? description : current.description(),
                        taskStatus != null ? taskStatus : current.taskStatus(),
                        current.createdAt(),
                        new Date());
                tasks.set(i, updated);
                write(tasks);
            }
        }

    }

    @Override
    public List<Task> getListTask(TaskStatus taskStatus) {
        List<Task> tasks = load();
        if (taskStatus == null) {
            return tasks;
        }
        return tasks.stream()
                .filter(t -> t.taskStatus() == taskStatus)
                .toList();
    }

    private List<Task> load() {
        if (!Files.exists(file)) {
            List<Task> empty = new ArrayList<>();
            write(empty);
            return empty;
        }
        return new ArrayList<>(
                mapper.readValue(file.toFile(), new TypeReference<List<Task>>() {}));
    }

    public void write(List<Task> tasks){
        try{
            Files.createDirectories(file.getParent());

            Path temp = file.resolveSibling("tasks.json.tmp");
            mapper.writeValue(temp.toFile(), tasks);
            Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE);

        } catch(IOException e) {
            log.error(e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }


}
