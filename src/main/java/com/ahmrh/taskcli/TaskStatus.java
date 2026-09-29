package com.ahmrh.taskcli;

public enum TaskStatus {
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