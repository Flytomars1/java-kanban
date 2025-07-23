import model.Epic;
import model.Subtask;
import model.Task;
import manager.FileBackedTaskManager;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            File tempFile = File.createTempFile("kanban_tasks_", ".csv");
            System.out.println("Файл создан: " + tempFile.getAbsolutePath());

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Task task1 = new Task("Купить продукты", "Молоко и хлеб", LocalDateTime.of(2025, 4, 5, 15, 0), Duration.ofMinutes(30));
            manager.createTask(task1);

            Epic epic1 = new Epic("Ремонт квартиры", "Покраска стен");
            epic1 = manager.createEpic(epic1);

            Subtask subtask1 = new Subtask("Купить краску", "Белая матовая",LocalDateTime.of(2025, 4, 5, 10, 30), Duration.ofMinutes(30), epic1.getId());
            Subtask subtask2 = new Subtask("Покрасить стены", "Все комнаты",LocalDateTime.of(2025, 4, 5, 20, 0), Duration.ofMinutes(30), epic1.getId());

            manager.createSubtask(subtask1);
            manager.createSubtask(subtask2);

            manager.save();

            System.out.println("Файл после сохранения");
            FileBackedTaskManager.printFileContent(tempFile);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            System.out.println("После загрузки");

            Task loadedTask = loadedManager.getTaskById(task1.getId());
            Epic loadedEpic = loadedManager.getEpicById(epic1.getId());
            Subtask loadedSubtask1 = loadedManager.getSubtaskById(subtask1.getId());
            Subtask loadedSubtask2 = loadedManager.getSubtaskById(subtask2.getId());

            System.out.println("Загружена задача: " + loadedTask);
            System.out.println("Загружен эпик: " + loadedEpic);

            System.out.println("Загружены подзадачи:");
            System.out.println(loadedSubtask1);
            System.out.println(loadedSubtask2);

            List<Integer> subtaskIds = loadedEpic.getSubtaskIds();
            System.out.println("ID подзадач у эпика: " + subtaskIds);

            System.out.println();

            System.out.println("Приоритизированный список после загрузки");
            List<Task> loadedPrioritized = loadedManager.getPrioritizedTasks();
            for (Task task : loadedPrioritized) {
                System.out.println(task.getId() + ": " + task.getTitle() +
                        " -> " + task.getStartTime() + " (end=" + task.getEndTime() + ")");
            }


        } catch (IOException e) {
            System.out.println("Произошла ошибка при работе с файлом: " + e.getMessage());
        }
    }
}