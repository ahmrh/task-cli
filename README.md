# task-cli

A simple command-line task tracker built with **Spring Shell**, **Java** and **Gradle**. Tasks are stored locally as JSON, so they persist between runs.

```bash
task-cli add "Buy groceries"
task-cli list
task-cli mark 1 in-progress
```

## Features

- Add, list, update, delete tasks
- Track status: `todo`, `in-progress`, `done`
- Filter the task list by status
- Aligned table output with last-updated time
- Persistent storage in a local JSON file (written atomically)

## Requirements

- JDK 25
- Gradle (the included wrapper `./gradlew` is enough)

## Tech stack

| Component | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.1.1 |
| Spring Shell | 4.x |
| Jackson | 3 (`spring-boot-starter-jackson`) |
| Build tool | Gradle (Kotlin DSL) |

## Build

```bash
./gradlew bootJar
```

The jar is created at `build/libs/task-cli-0.0.1-SNAPSHOT.jar`.

## Run

Make sure you run the jar with Java 25. An older `java` on your `PATH` fails with `UnsupportedClassVersionError`.

**One-shot mode** (runs a command and exits):

```bash
java -jar build/libs/task-cli-0.0.1-SNAPSHOT.jar add "Buy groceries"
```

**Interactive mode** (no arguments, opens a `shell:>` prompt):

```bash
java -jar build/libs/task-cli-0.0.1-SNAPSHOT.jar
```

> `./gradlew bootRun` does not forward stdin, so interactive mode does not work through it. Use the jar. To run a single command through Gradle: `./gradlew bootRun --args='list'`.

### Use it as `task-cli`

Create a wrapper script at `~/.local/bin/task-cli`:

```bash
#!/usr/bin/env bash
exec /path/to/jdk-25/bin/java -jar /path/to/task-cli/build/libs/task-cli-0.0.1-SNAPSHOT.jar "$@"
```

```bash
chmod +x ~/.local/bin/task-cli
```

Make sure `~/.local/bin` is on your `PATH`. Rebuild with `./gradlew bootJar` after code changes and the script picks up the new jar.

## Commands

| Command | Description | Example |
|---|---|---|
| `add <description>` | Add a new task (status starts as `todo`) | `task-cli add "Buy groceries"` |
| `list [-s, --status <status>]` | List tasks, optionally filtered by status (`all`, `todo`, `in-progress`, `done`) | `task-cli list --status todo` |
| `update <id> <description>` | Change a task's description | `task-cli update 1 "Buy groceries and cook"` |
| `mark <id> <status>` | Set a task's status | `task-cli mark 1 in-progress` |
| `delete <id>` | Delete a task | `task-cli delete 1` |

Wrap descriptions containing spaces in quotes.

### Example output

```
$ task-cli list
Tasks (all): 3

ID   STATUS       UPDATED          DESCRIPTION
1    todo         2026-09-29 14:05 Buy groceries
2    in-progress  2026-09-29 14:10 Write report
3    done         2026-09-29 14:12 Fix build
```

## Data storage

Tasks are saved to:

```
~/.task-cli/tasks.json
```

The folder is hidden (it starts with a dot), so use `ls -a` to see it. The file is created automatically on first use. To reset all tasks, delete it:

```bash
rm ~/.task-cli/tasks.json
```

Each task is stored like this:

```json
{
  "id": 1,
  "description": "Buy groceries",
  "taskStatus": "todo",
  "createdAt": "2026-09-29T07:05:00.000+00:00",
  "updatedAt": "2026-09-29T07:05:00.000+00:00"
}
```

Saves write to a temporary file first and then rename it over `tasks.json`, so a crash mid-write cannot corrupt existing data.

## Project structure

```
src/main/java/com/ahmrh/taskcli/
├── TaskCliApplication.java        # Spring Boot entry point
├── TaskCliCommands.java           # Spring Shell commands
├── Task.java                      # Task record
├── TaskStatus.java                # todo / in-progress / done
└── repository/
    ├── TaskRepository.java        # Storage interface
    └── JsonTaskRepository.java    # JSON file implementation
```

Commands depend only on the `TaskRepository` interface, so the storage backend can be swapped (for example, for SQLite or an in-memory version for tests) without changing the commands.

## Known limitations

- **IDs can be reused across runs.** `lastId` is rebuilt from the highest ID in the file on each start, so deleting the newest task frees its ID for the next `add`. Within a single interactive session, IDs are never reused.
- **No concurrent-write protection.** Running two `task-cli` commands at exactly the same time could overwrite each other's changes.

## Possible next steps

- Persist the ID counter so deleted IDs are never reused
- Make the storage path configurable
- Switch `Date` to `java.time.Instant`
- Add unit tests using an in-memory repository
- Build a native binary with GraalVM for faster startup
