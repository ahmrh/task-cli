package com.ahmrh.taskcli;

import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;


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

    @Command(name = "hello", description = "Say hello to a given name", group = "Greetings",
            help = "A command that greets the user with 'Hello ${name}!'. Usage: hello [-n | --name]=<name>")
    public void sayHello(@Option(shortName = 'n', longName = "name", description = "the name of the person to greet",
            defaultValue = "World") String name) {
        System.out.println("Hello " + name + "!");
    }

    @Command(name = "add", description = "Adding a new task")
    public void add(
            @Argument(index = 0, description = "Task title") String title
    ) {
        String output = String.format("Task added successfully (ID: %d)", -1);
        System.out.println(output);
    }

    @Command(name = "update", description = "Updating task")
    public void update(
            @Argument(index = 0, description = "Task id") int id,
            @Argument(index = 1, description = "New task title") String title
    ) {
        String output = String.format("Task updated to \"%s\"", title);
        System.out.println(output);
    }

    @Command(name = "delete", description = "Deleting task")
    public void update(
            @Argument(index = 0, description = "Task id") int id
    ) {
        String output = String.format("Task deleted successfully (ID: %d)", -1);
        System.out.println(output);
    }

    @Command(name = "list", description = "Listing all tasks")
    public void list() {
        String output = String.format("Here's the list of task");
        System.out.println(output);
    }

    @Command(name = "list", description = "Listing tasks by status")
    public void listByStatus(

            @Argument(index = 0, description = "Task status") String status
    ) {
        TaskStatus taskStatus = TaskStatus.fromValue(status);

        String output = String.format("Here's the list of task with this status");

        System.out.println(output);
    }

    enum TaskStatus {
        DONE("done"),
        IN_PROGRESS("in-progress"),
        TODO("todo");


        private String value;

        TaskStatus(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static TaskStatus fromValue(String value) {
            for (TaskStatus status : values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }
}