import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private static int idCounter = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();

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
        tasks.put(id, epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        tasks.put(id, subtask);

        Epic epic = (Epic) getTaskById(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }

        return subtask;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // получаем задачи из мапы
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (!(task instanceof Epic)) {
                result.add(task);
            }
        }
        return result;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic && !(task instanceof Subtask)) {
                result.add((Epic) task);
            }
        }
        return result;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) {
                result.add((Subtask) task);
            }
        }
        return result;
    }

    // получаем подзадачи из мапы эпика
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask subtask && subtask.getEpicId() == epicId) {
                result.add(subtask);
            }
        }
        return result;
    }

    // обновление задач (проверки на null и несуществующий id)
    public void updateTask(Task task) {
        if (task == null) {;
            return;
        }

        if (!tasks.containsKey(task.getId())) {
            return;
        }

        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if (tasks.containsKey(epic.getId())) {
            tasks.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (tasks.containsKey(subtask.getId())) {
            tasks.put(subtask.getId(), subtask);
        }

        Epic epic = (Epic) getTaskById(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    // обновление статуса эпика от статуса подзадач
    public void updateEpicStatus(Epic epic) {
        int epicId = epic.getId();
        ArrayList<Subtask> subtasks = getSubtasksByEpicId(epicId);

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean tasksDone = true;
        boolean tasksInProgress = false;

        for (Subtask subtask : subtasks) {
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

        updateTask(epic);
    }

    //удалить все задачи
    public void deleteAllTasks() {
        tasks.clear();
    }

    //удалить задачи по id (обновлять эпик, как?)
    public void deleteTaskById(int id) {
        Task task = tasks.get(id);

        if (task == null) {
            System.out.println("Задача с id " + id + " не найдена.");
            return;
        }

        if (task instanceof Epic) {
            if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                int epicId = subtask.getEpicId();

                tasks.remove(id);
                System.out.println("Подзадача c id " + id + " удалена");

                Epic epic = (Epic) tasks.get(epicId);
                updateEpic(epic);
            } else {
                deleteEpicsWithSubtasks(id);
            }
        } else {
            tasks.remove(id);
            System.out.println("Задача c id " + id + " удалена");
        }
    }

    // отдельный метод для удаления эпиков, не знаю как, подумать
    private void deleteEpicsWithSubtasks(int epicId) {
        ArrayList<Integer> subtaskIdsToRemove = new ArrayList<>();
        for (Integer key : tasks.keySet()) {
            Task task = tasks.get(key);
            if (task instanceof Subtask && ((Subtask) task).getEpicId() == epicId) {
                subtaskIdsToRemove.add(key);
            }
        }

        for (int subtaskId : subtaskIdsToRemove) {
            tasks.remove(subtaskId);
        }

        tasks.remove(epicId);

        System.out.println("Эпик с id " + epicId + " и всего подзадачи удалены");
    }

    // вывести все задачи
    public void printAllTasks() {
        System.out.println("Все задачи:");
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }
}
