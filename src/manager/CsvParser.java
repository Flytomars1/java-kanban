package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CsvParser {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    //методы, чтобы эпики сохранялись в файл (либо создавать их не null)
    private static String toString(LocalDateTime time) {
        return time == null ? "" : time.format(formatter);
    }

    private static String toString(Duration duration) {
        return duration == null ? "" : String.valueOf(duration.toMinutes());
    }

    public static String taskToString(Task task) {
        return String.join(",",
                String.valueOf(task.getId()),
                TaskType.TASK.toString(),
                task.getTitle(),
                task.getStatus().toString(),
                task.getDescription(),
                toString(task.getStartTime()),
                toString(task.getDuration()),
                "" // epicId — пусто для Task
        );
    }

    public static String epicToString(Epic epic) {
        return String.join(",",
                String.valueOf(epic.getId()),
                TaskType.EPIC.toString(), // ← Убедись, что здесь EPIC
                epic.getTitle(),
                epic.getStatus().toString(),
                epic.getDescription(),
                toString(epic.getStartTime()),
                toString(epic.getDuration()),
                ""
        );
    }

    public static String subtaskToString(Subtask subtask) {
        return String.join(",",
                String.valueOf(subtask.getId()),
                TaskType.SUBTASK.toString(),
                subtask.getTitle(),
                subtask.getStatus().toString(),
                subtask.getDescription(),
                toString(subtask.getStartTime()),
                toString(subtask.getDuration()),
                String.valueOf(subtask.getEpicId()) // epicId — только у Subtask
        );
    }

    public static Task parseLine(String line) {
        String[] parts = line.split(",");

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        LocalDateTime startTime = null;
        Duration duration = null;

        if (parts.length > 5 && !parts[5].isEmpty()) {
            startTime = LocalDateTime.parse(parts[5], formatter);
        }
        if (parts.length > 6 && !parts[6].isEmpty()) {
            duration = Duration.ofMinutes(Integer.parseInt(parts[6]));
        }

        switch (type) {
            case "TASK": {
                Task task = new Task(title, description, startTime, duration);
                task.setId(id);
                task.setStatus(status);
                return task;
            }

            case "EPIC": {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return epic;
            }

            case "SUBTASK": {
                int epicId = -1;
                if (parts.length > 7 && !parts[7].isEmpty()) {
                    epicId = Integer.parseInt(parts[7]);
                }
                if (epicId <= 0) {
                    throw new IllegalArgumentException("У подзадачи должен быть корректный epicId");
                }

                Subtask subtask = new Subtask(title, description, startTime, duration, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            }

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
