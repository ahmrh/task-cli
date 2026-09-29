package com.ahmrh.taskcli;

import java.util.Date;

public record Task(
        int id,
        String description,
        TaskStatus taskStatus,
        Date createdAt,
        Date updatedAt
) {



}
