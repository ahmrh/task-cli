package com.ahmrh.taskcli;

import com.ahmrh.taskcli.repository.TaskRepository;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// Requirements:
//Add, Update, and Delete tasks
//
//Mark a task as in progress or done
//
//List all tasks
//
//List all tasks that are done
//
//List all tasks that are not done
//
//List all tasks that are in progress
@Component
public class TaskCliCommands {

    private final TaskRepository repository;

    public TaskCliCommands(TaskRepository repository) {
        this.repository = repository;
    }
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    @Command(name = "add", description = "Adding a new task")
    public void add(
            @Argument(index = 0, description = "Task title") String description
    ) {
        Task task = repository.addTask(description);

        String output = String.format("Task added successfully (ID: %d)", task.id());
        System.out.println(output);
    }

    @Command(name = "update", description = "Updating task")
    public void update(
            @Argument(index = 0, description = "Task id") int id,
            @Argument(index = 1, description = "New task title") String description
    ) {
        repository.updateTask(id, description, null);

        String output = String.format("Task %d is updated to \"%s\"", id, description);
        System.out.println(output);
    }

    @Command(name = "mark-in-progress", description = "Marking a task as in progress")
    public void markInProgress(
            @Argument(index = 0, description = "Task id") int id
    ) {
        repository.updateTask(id, null, TaskStatus.IN_PROGRESS);

        String output = String.format("Task %d is updated to in progress", id);
        System.out.println(output);
    }

    @Command(name = "mark-done", description = "Marking a task as done")
    public void markDone(
            @Argument(index = 0, description = "Task id") int id
    ) {
        repository.updateTask(id, null, TaskStatus.DONE);

        String output = String.format("Task %d is updated to done", id);
        System.out.println(output);
    }

    @Command(name = "delete", description = "Deleting task")
    public void delete(
            @Argument(index = 0, description = "Task id") int id
    ) {

        repository.deleteTask(id);

        String output = String.format("Task deleted successfully (ID: %d)", id);
        System.out.println(output);
    }
    @Command(name = "list", description = "Listing all tasks, optionally filtered by status")
    public void list(
            @Option(shortName = 's', longName = "status", defaultValue = "all",
                    description = "Task status (all | todo | in-progress | done)") String status
    ) {
        TaskStatus filter;
        try {
            filter = "all".equalsIgnoreCase(status) ? null : TaskStatus.fromValue(status);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        List<Task> tasks = repository.getListTask(filter);
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        StringBuilder output = new StringBuilder();
        output.append("Tasks (%s): %d\n\n".formatted(status.toLowerCase(), tasks.size()));
        output.append("%-4s %-12s %-16s %s%n".formatted("ID", "STATUS", "UPDATED", "DESCRIPTION"));

        for (Task task : tasks) {
            output.append("%-4d %-12s %-16s %s%n".formatted(
                    task.id(),
                    task.taskStatus(),
                    DATE_FORMAT.format(task.updatedAt().toInstant()),
                    task.description()));
        }

        System.out.println(output.toString().stripTrailing());
    }


    @Command(name = "list", description = "Listing tasks by status")
    public void listByStatus(

            @Argument(index = 0, description = "Task status") String status
    ) {
        TaskStatus taskStatus = TaskStatus.fromValue(status);

        String output = String.format("Here's the list of task with this status");

        System.out.println(output);
    }

}