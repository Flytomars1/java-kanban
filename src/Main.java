import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // Создаём задачи
        Task task1 = new Task("Купить продукты", "Молоко и хлеб");
        Task task2 = new Task("Выкинуть мусор", "Не промахнуться");
        Epic epic1 = new Epic("Ремонт квартиры", "Покраска стен");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Купить краску", "Белая матовая", epic1.getId());
        Subtask subtask2 = new Subtask("Покрасить стены", "Все комнаты", epic1.getId());
        Subtask subtask3 = new Subtask("Покрасить пол", "В гостиной", epic1.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        Epic epic2 = new Epic("Подготовка к поездке", "Собрать вещи");
        manager.createEpic(epic2);

        // Выводим всё
        System.out.println("=== Состояние после создания задач ===");
        manager.printAllTasks();

        // Получаем задачи по ID (это добавляет их в историю)
        System.out.println("=== Добавляем задачи в историю ===");
        manager.getTaskById(task1.getId());
        manager.getEpicById(task2.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getSubtaskById(subtask3.getId());
        manager.getEpicById(epic2.getId());

        manager.printAllTasks(); // выводим историю просмотров

        manager.getTaskById(task2.getId());
        manager.getEpicById(epic2.getId());
        manager.getSubtaskById(subtask3.getId());

        manager.printAllTasks(); // история меняется

        // Обновляем задачу
        Task updatedTask = new Task("Купить овощи", "Картошка и лук");
        updatedTask.setId(1);
        updatedTask.setStatus(TaskStatus.DONE);
        manager.updateTask(updatedTask);

        System.out.println("=== После обновления задачи ===");
        manager.printAllTasks();

        // Удаляем обычную задачу
        manager.deleteTaskById(task2.getId());

        // Удаляем эпик с подзадачами
        manager.deleteEpicById(epic1.getId());

        System.out.println("=== После удаления ===");
        manager.printAllTasks();
            }
        }