public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // создание задач
        System.out.println("Создание задач");
        Task task1 = new Task("Задача 1", "Описание задачи 1", 0, TaskStatus.NEW);
        manager.createTask(task1);

        Epic epic1 = new Epic("Эпик 1", "Большая задача 1", 0, TaskStatus.NEW);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Часть эпика 1", 0, TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Еще часть эпика 1", 0, TaskStatus.NEW, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.printAllTasks();

        // получение задач по id
        System.out.println("Получение задач по ID");
        System.out.println("Получаем задачу с ID=1: " + manager.getTaskById(1));
        System.out.println("Получаем эпик с ID=2: " + manager.getTaskById(2));
        System.out.println("Получаем подзадачу с ID=3: " + manager.getTaskById(3));

        // обновление задач
        System.out.println("Обновление задач");
        Task updatedTask = new Task("Обновлённая Задача 1", "Новое описание", 1, TaskStatus.DONE);
        manager.updateTask(updatedTask);

        Epic updatedEpic = new Epic("Обновлённый Эпик 1", "Обновлённое описание", 2, TaskStatus.IN_PROGRESS);
        manager.updateEpic(updatedEpic);

        Subtask updatedSubtask = new Subtask("Обновлённая Подзадача 1", "Новое описание подзадачи", 3, TaskStatus.DONE, epic1.getId());
        manager.updateSubtask(updatedSubtask);

        manager.printAllTasks();

        // списки задач
        System.out.println("Получение списков задач");
        System.out.println("Все обычные задачи:");
        for (Task t : manager.getAllTasks()) {
            System.out.println(t);
        }

        System.out.println("Все эпики:");
        for (Epic e : manager.getAllEpics()) {
            System.out.println(e);
        }

        System.out.println("Все подзадачи:");
        for (Subtask s : manager.getAllSubtasks()) {
            System.out.println(s);
        }

        System.out.println("Подзадачи эпика с ID=2:");
        for (Subtask s : manager.getSubtasksByEpicId(2)) {
            System.out.println(s);
        }

        // обновление статуса эпика
        System.out.println("Обновление статуса эпика");
        Subtask subtask3 = new Subtask("Подзадача 3", "IN_PROGRESS", 0, TaskStatus.IN_PROGRESS, epic1.getId());
        manager.createSubtask(subtask3);

        System.out.println("После добавления подзадачи со статусом IN_PROGRESS:");
        System.out.println(manager.getTaskById(2)); // должен быть IN_PROGRESS

        // удаление задач
        System.out.println("Удаление задач");

        System.out.println("Удаляем подзадачу с ID=3...");
        manager.deleteTaskById(3);
        System.out.println("Состояние после удаления подзадачи:");
        manager.printAllTasks();

        System.out.println("Удаляем эпик с ID=2 (должны удалиться и его подзадачи)...");
        manager.deleteTaskById(2);
        manager.printAllTasks();

        System.out.println("Удаляем все задачи...");
        manager.deleteAllTasks();
        manager.printAllTasks();
    }
}