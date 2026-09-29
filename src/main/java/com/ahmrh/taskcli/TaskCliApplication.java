package com.ahmrh.taskcli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.core.command.annotation.EnableCommand;

@SpringBootApplication
public class TaskCliApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskCliApplication.class, args);
    }
}