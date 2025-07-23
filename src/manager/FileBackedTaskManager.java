package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        if (created != null) {
            save();
        }
        return created;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        if (created != null) {
            save();
        }
        return created;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask created = super.createSubtask(subtask);
        if (created != null) {
            save();
        }
        return created;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllSubtasksByEpicId(int epicId) {
        super.deleteAllSubtasksByEpicId(epicId);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,startTime,duration,epicId");
            writer.newLine();

            getTasks().values().forEach(task -> writeTask(writer, task));

            getEpics().values().forEach(epic -> writeTask(writer, epic));

            getSubtasks().values().forEach(subtask -> writeTask(writer, subtask));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + e.getMessage(), e);
        }
    }

    private void writeTask(BufferedWriter writer, Task task) {
        try {
            String line;
            if (task instanceof Subtask subtask) {
                line = CsvParser.subtaskToString(subtask);
            } else if (task instanceof Epic epic) {
                line = CsvParser.epicToString(epic);
            } else {
                line = CsvParser.taskToString(task);
            }
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager saveManager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Task task = CsvParser.parseLine(line);

                    if (task instanceof Epic epic) {
                        saveManager.epics.put(epic.getId(), epic);
                    } else if (task instanceof Subtask subtask) {
                        saveManager.subtasks.put(subtask.getId(), subtask);
                        if (subtask.getStartTime() != null) {
                            saveManager.prioritizedTask.add(subtask);
                        }
                    } else {
                        saveManager.tasks.put(task.getId(), task);
                        if (task.getStartTime() != null) {
                            saveManager.prioritizedTask.add(task);
                        }
                    }
                } catch (RuntimeException e) {
                    throw new ManagerSaveException("Ошибка парсинга строки '" + line + "': " + e.getMessage(), e);
                }
            }

            for (Subtask subtask : saveManager.subtasks.values()) {
                Epic epic = saveManager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }

            for (Epic epic : saveManager.epics.values()) {
                saveManager.updateEpicStatus(epic);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + e.getMessage(), e);
        }

        return saveManager;
    }

    public static void printFileContent(File file) {
        System.out.println("Содержимое файла " + file.getAbsolutePath() + ":");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
