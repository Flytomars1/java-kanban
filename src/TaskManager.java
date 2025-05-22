import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private static int idCounter = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int generateId() {
        return idCounter++;
    }

    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());

        if (epic == null) {
            return null;
        }

        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        epic.addSubtaskId(id);
        updateEpicStatus(epic);

        return subtask;
    }

    // получаем задачи из мапы
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    /* Вроде не нужные методы, но хочу оставить
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int epidId) {
        ArrayList<Subtask> result = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epidId) {
                result.add(subtask);
            }
        }
        return result;
    }
     */

    // обновление задач (проверки на null и несуществующий id)
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        Epic storedEpic = epics.get(epic.getId());
        storedEpic.setTitle(epic.getTitle());
        storedEpic.setDescription(epic.getDescription());
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);

        Epic epic = getEpicById(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    // обновление статуса эпика от статуса подзадач
    public void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            updateEpic(epic);
            return;
        }

        boolean tasksDone = true;
        boolean tasksInProgress = false;

        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask.getStatus() != TaskStatus.DONE) {
                tasksDone = false;
            }
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                tasksInProgress = true;
            }
        }

        if (tasksDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (tasksInProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }

        updateEpic(epic);
    }

    //удалить вообще все задачи (полная очистка)
    public void deleteAll() {
        tasks.clear();
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(TaskStatus.NEW);
        }
        epics.clear();
    }

    //удалить все задачи (по типам)
    //обычные задачи
    public void deleteAllTasks() {
        tasks.clear();
    }

    //эпики
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    //подзадачи
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }

        subtasks.clear();
    }

    //удалить подзадачи конкретного эпика
    public void deleteAllSubtasksByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic == null) {
            System.out.println("Эпик с id " + epicId + " не найден");
            return;
        }

        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }

        epic.clearSubtaskIds();
        updateEpicStatus(epic);
    }

    //удалить обычную задачу задачи по id
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            System.out.println("Задача с id " + id + " удалена");
        } else {
            System.out.println("Задача с id " + id + " не найдена");
        }
    }

    //удалить эпик с подзадачами
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            System.out.println("Эпик с id " + id + " не найден");
            return;
        }

        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }

        System.out.println("Эпик с id " + id + " и все его подзадачи удалены");
    }

    //удалить конкретную подзадачу и обновить статус эпика
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            System.out.println("Подзадача с id " + id + " не найдена");
            return;
        }

        Epic epic = getEpicById(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtaskId(id);
            updateEpicStatus(epic);
        }

        System.out.println("Подзадача с id " + id + " удалена");
    }

    // вывести все задачи
    public void printAllTasks() {
        System.out.println("Все задачи");

        System.out.println("Обычные задачи");
        for (Task task : tasks.values()) {
            System.out.println(task);
        }

        System.out.println("Эпики");
        for (Epic epic : epics.values()) {
            System.out.println(epic);
        }

        System.out.println("Подзадачи");
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask);
        }
    }
}
