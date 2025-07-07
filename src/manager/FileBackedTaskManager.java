package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

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

    @Override
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getTasks().values()) {
                writer.write(taskToString(task));
                writer.newLine();
            }

            for (Epic epic : getEpics().values()) {
                writer.write(epicToString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getSubtasks().values()) {
                writer.write(subtaskToString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager saveManager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            //скипаем первую строку (костыль?)
            reader.readLine();

            //читаем файл
            while (reader.ready()) {
                String line = reader.readLine();
                parseLine(saveManager, line);
            }
            } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: " + e.getMessage());
        }
        return saveManager;
    }

    private static void parseLine(FileBackedTaskManager manager, String line) {
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
                manager.getTasks().put(id, task);
                break;

            case "EPIC":
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(status);
                manager.getEpics().put(id, epic);
                break;

            case "SUBTASK":
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(title, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                manager.getSubtasks().put(id, subtask);

                //двусторонняя связь
                Epic storedEpic = manager.getEpics().get(epicId);
                storedEpic.addSubtaskId(id);
                manager.updateEpicStatus(storedEpic);

                break;
        }

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

    //не буду менять toString, вдруг пригодится еще
    private String taskToString(Task task) {
        return task.getId() + ","
                + TaskType.TASK + ","
                + task.getTitle() + ","
                + task.getStatus() + ","
                + task.getDescription();
    }

    private String epicToString(Epic epic) {
        return epic.getId() + ","
                + TaskType.EPIC + ","
                + epic.getTitle() + ","
                + epic.getStatus() + ","
                + epic.getDescription();
    }

    private String subtaskToString(Subtask subtask) {
        return subtask.getId() + ","
                + TaskType.SUBTASK + ","
                + subtask.getTitle() + ","
                + subtask.getStatus() + ","
                + subtask.getDescription() + ","
                + subtask.getEpicId();
    }

}
