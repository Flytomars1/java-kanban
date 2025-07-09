package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

public class CsvParser {
    public static String taskToString(Task task) {
        return task.getId() + ","
                + TaskType.TASK + ","
                + task.getTitle() + ","
                + task.getStatus() + ","
                + task.getDescription();
    }

    public static String epicToString(Epic epic) {
        return epic.getId() + ","
                + TaskType.EPIC + ","
                + epic.getTitle() + ","
                + epic.getStatus() + ","
                + epic.getDescription();
    }

    public static String subtaskToString(Subtask subtask) {
        return subtask.getId() + ","
                + TaskType.SUBTASK + ","
                + subtask.getTitle() + ","
                + subtask.getStatus() + ","
                + subtask.getDescription() + ","
                + subtask.getEpicId();
    }

    public static Task parseLine(String line) {
        String[] parts = line.split(",");

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]); // украл
        String description = parts[4];
        //int epicId = Integer.parseInt(parts[5]);

        switch (type) {
            case "TASK":
                Task task = new Task(title, description);
                task.setId(id);
                task.setStatus(status);
                return task;

            case "EPIC":
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;

            case "SUBTASK":
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(title, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
