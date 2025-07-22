package manager;

import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final TreeSet<Task> prioritizedTask = new TreeSet<>(new StartTimeComparator());
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    public HashMap<Integer, Task> getTasks() {
        return new HashMap<>(tasks);
    }

    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epics);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return new HashMap<>(subtasks);
    }

    // генератор id
    @Override
    public int generateId() {
        return idCounter++;
    }

    // Создание задач
    @Override
    public Task createTask(Task task) {
        if (hasIntersection(task)) {
            throw new IllegalArgumentException("Есть пересечения по времени");
        }

        int id = task.getId();
        if (id == 0) {
            id = generateId();
            task.setId(id);
        }

        tasks.put(id, task);
        addTaskToPrioritized(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = epic.getId();
        if (id == 0) {
            id = generateId();
            epic.setId(id);
        }

        epics.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        if (hasIntersection(subtask)) {
            throw new IllegalArgumentException("Есть пересечения по времени");
        }

        if (epic == null) {
            return null;
        }

        int id = subtask.getId();
        if (id == 0) {
            id = generateId();
            subtask.setId(id);
        }

        subtasks.put(id, subtask);

        epic.addSubtaskId(id);
        updateEpicStatus(epic);

        addTaskToPrioritized(subtask);
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
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        return null;
    }

    //обновление задач
    //сохранение в treeSet
    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }

        if (hasIntersection(task)) {
            throw new IllegalArgumentException("Нельзя обновить из-за пересечения по времени");
        }

        Task oldTask = tasks.get(task.getId());

        tasks.put(task.getId(), task);

        removeTaskFromPrioritized(oldTask);
        addTaskToPrioritized(task);
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

        if (hasIntersection(subtask)) {
            throw new IllegalArgumentException("Нельзя обновить из-за пересечения по времени");
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        removeTaskFromPrioritized(oldSubtask);

        subtasks.put(subtask.getId(), subtask);
        addTaskToPrioritized(subtask);

        Epic epic = getEpicById(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    protected void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        List<Subtask> allSubtasks = new ArrayList<>(subtasks.values());

        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            updateEpic(epic);
            return;
        }

        List<Subtask> subtaskList = subtaskIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        boolean tasksDone = subtaskList.stream()
                .allMatch(st -> st.getStatus() == TaskStatus.DONE);
        boolean tasksInProgress = subtaskList.stream()
                .anyMatch(st -> st.getStatus() == TaskStatus.IN_PROGRESS || st.getStatus() == TaskStatus.DONE);

        if (tasksDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (tasksInProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }

        epic.updateEpicFromSubtasks(allSubtasks);
        updateEpic(epic);
    }

    //удаление задач
    @Override
    public void deleteAll() {
        tasks.clear();
        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(TaskStatus.NEW);
        });

        /*
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            epic.setStatus(TaskStatus.NEW);
        }
         */
        epics.clear();

        historyManager.deleteAll();
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);

        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);

        epics.values().forEach(epic -> {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        });

        /*
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }
         */

        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasksByEpicId(int epicId) {
        Epic epic = getEpicById(epicId);
        if (epic == null) {
            System.out.println("Эпик с id " + epicId + " не найден");
            return;
        }

        epic.getSubtaskIds().forEach(subtasks::remove);

        epic.clearSubtaskIds();
        updateEpicStatus(epic);
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            removeTaskFromPrioritized(task);
            historyManager.remove(id);
            //System.out.println("Задача с id " + id + " удалена");
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

        removeTaskFromPrioritized(subtask);
        historyManager.remove(id);
        System.out.println("Подзадача с id " + id + " удалена");
    }

    //методы для управления приоритетами
    private void addTaskToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTask.add(task);
        }
    }

    private void removeTaskFromPrioritized(Task task) {
        prioritizedTask.remove(task);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTask);
    }

    //роверка пересечений
    private boolean isOverlapping(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) {
            return false; //если null - нет пересечений(наверное)
        }

        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = t1.getEndTime();
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = t2.getEndTime();

        return end1.isAfter(start2) && end2.isAfter(start1);
    }

    //есть пересечения
    private boolean hasIntersection(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(existingTask -> isOverlapping(newTask, existingTask));
    }

    void loadTask(Task task) {
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTask.add(task);
        }
    }

    void loadEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        // Не добавляем в prioritizedTask, даже если startTime != null
        // Он будет добавлен позже через updateEpicStatus()
    }

    void loadSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTask.add(subtask);
        }
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