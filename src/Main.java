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
        Epic epic1 = new Epic("Ремонт квартиры", "Покраска стен");

        manager.createTask(task1);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Купить краску", "Белая матовая", epic1.getId());
        Subtask subtask2 = new Subtask("Покрасить стены", "Все комнаты", epic1.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // Выводим всё
        System.out.println("=== Состояние после создания задач ===");
        manager.printAllTasks();

        // Получаем задачи по ID (это добавляет их в историю)
        System.out.println("=== Добавляем задачи в историю ===");
        manager.getTaskById(1);
        manager.getEpicById(2);
        manager.getSubtaskById(3);
        manager.getSubtaskById(4);
        manager.getTaskById(1); // повторный просмотр

        manager.printAllTasks(); // история должна содержать последние 10 просмотренных

        // Обновляем задачу
        Task updatedTask = new Task("Купить овощи", "Картошка и лук");
        updatedTask.setId(1);
        updatedTask.setStatus(TaskStatus.DONE);
        manager.updateTask(updatedTask);

        System.out.println("=== После обновления задачи ===");
        manager.printAllTasks();

        // Удаляем подзадачу
        manager.deleteSubtaskById(3);

        System.out.println("=== После удаления подзадачи ===");
        manager.printAllTasks();
            }
        }