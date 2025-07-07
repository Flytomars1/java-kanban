package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void save() {
    }

    // генератор id
    @Override
    public int generateId() {
        return idCounter++;
    }

    // Создание задач
    @Override
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

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

    //получение задач
    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return null;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epidId) {
        return null;
    }

    //обновление задач
    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        Epic storedEpic = epics.get(epic.getId());
        storedEpic.setTitle(epic.getTitle());
        storedEpic.setDescription(epic.getDescription());
    }

    @Override
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

    protected void updateEpicStatus(Epic epic) {
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
            //System.out.println("Подзадача ID=" + id + ", статус=" + (subtask != null ? subtask.getStatus() : "null"));
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

    //удаление задач
    @Override
    public void deleteAll() {
        tasks.clear();
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(TaskStatus.NEW);
        }
        epics.clear();

        historyManager.deleteAll();
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }

        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }

        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }

        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }

        subtasks.clear();
    }

    @Override
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

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(id);
            System.out.println("Задача с id " + id + " удалена");
        } else {
            System.out.println("Задача с id " + id + " не найдена");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            System.out.println("Эпик с id " + id + " не найден");
            return;
        }

        for (int subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }

        historyManager.remove(id);

        System.out.println("Эпик с id " + id + " и все его подзадачи удалены");
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            System.out.println("Подзадача с id " + id + " не найдена");
            return;
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.removeSubtaskId(id);
            updateEpicStatus(epic);
        }

        historyManager.remove(id);
        System.out.println("Подзадача с id " + id + " удалена");
    }

    //вывод задач
    @Override
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

        System.out.println("История:");
        //List<model.Task> history = getHistory();
        for (Task task : getHistory()) {
            System.out.println(task);
        }

        System.out.println("------------------------------------");

    }
}