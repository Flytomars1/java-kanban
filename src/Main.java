public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        //Создаём обычную задачу
        Task task = new Task("Новая обычная задача", "Описание новой обычной задачи");
        manager.createTask(task);
        System.out.println("Создана обычная задача: " + task);

        //Создаём эпик
        Epic epic = new Epic("Новый эпик", "Описание нового эпика");
        manager.createEpic(epic);
        System.out.println("Создан эпик: " + epic);

        //Создаём подзадачи для эпика
        Subtask subtask1 = new Subtask("Первая подзадча", "Описание первой подзадачи", epic.getId());
        Subtask subtask2 = new Subtask("Вторая подзадача", "Описание второй подзадачи", epic.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        System.out.println("Созданы подзадачи:");
        System.out.println(manager.getSubtaskById(subtask1.getId()));
        System.out.println(manager.getSubtaskById(subtask2.getId()));

        //Проверяем получение задач по ID
        System.out.println("Получаем задачи по ID");
        System.out.println("Задача: " + manager.getTaskById(task.getId()));
        System.out.println("Эпик: " + manager.getEpicById(epic.getId()));
        System.out.println("Подзадача: " + manager.getSubtaskById(subtask1.getId()));

        //Обновляем обычную задачу (только заголовок и описание)
        task.setTitle("Обновленная обычная задача");
        task.setDescription("Обновленное описание");
        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);
        System.out.println("Обновлённая задача: " + manager.getTaskById(task.getId()));

        //Пробуем обновить эпик — только имя и описание
        Epic epicUpdate = new Epic("Обновленный эпик", "Обновленное описание эпика");
        epicUpdate.setId(epic.getId());
        epicUpdate.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateEpic(epicUpdate);
        System.out.println("Обновлённый эпик (статус должен зависеть от подзадач): " + manager.getEpicById(epic.getId()));

        //Обновляем подзадачу и проверяем статус эпика
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        System.out.println("Статус эпика после завершения одной подзадачи: " + manager.getEpicById(epic.getId()).getStatus());

        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);
        System.out.println("Статус эпика после завершения всех подзадач: " + manager.getEpicById(epic.getId()).getStatus());

        //Удаляем подзадачу
        manager.deleteSubtaskById(subtask1.getId());
        System.out.println("Подзадача удалена. Статус эпика: " + manager.getEpicById(epic.getId()).getStatus());

        //Удаляем эпик и его подзадачи
        manager.deleteEpicById(epic.getId());
        System.out.println("Эпик удалён. Поиск эпика по ID: " + manager.getEpicById(epic.getId()));

        //Удаляем обычную задачу
        manager.deleteTaskById(task.getId());
        System.out.println("Задача удалена. Поиск задачи по ID: " + manager.getTaskById(task.getId()));

        //Выводим всё
        System.out.println("Вывод всех задач");
        manager.printAllTasks();
    }
}