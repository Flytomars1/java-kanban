package main;

public class Main {
    public static void main(String[] args) {
        main.TaskManager manager = main.Managers.getDefault();

        // Создаём задачи
        main.Task task1 = new main.Task("Купить продукты", "Молоко и хлеб");
        main.Epic epic1 = new main.Epic("Ремонт квартиры", "Покраска стен");

        manager.createTask(task1);
        manager.createEpic(epic1);

        main.Subtask subtask1 = new main.Subtask("Купить краску", "Белая матовая", epic1.getId());
        main.Subtask subtask2 = new main.Subtask("Покрасить стены", "Все комнаты", epic1.getId());

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
        main.Task updatedTask = new main.Task("Купить овощи", "Картошка и лук");
        updatedTask.setId(1);
        updatedTask.setStatus(main.TaskStatus.DONE);
        manager.updateTask(updatedTask);

        System.out.println("=== После обновления задачи ===");
        manager.printAllTasks();

        // Удаляем подзадачу
        manager.deleteSubtaskById(3);

        System.out.println("=== После удаления подзадачи ===");
        manager.printAllTasks();
    }
}