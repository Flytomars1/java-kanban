package manager;
import model.Task;
import model.Epic;
import model.Subtask;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    int generateId();

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteAll();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteAllSubtasksByEpicId(int epicId);

    boolean deleteTaskById(int id);

    boolean deleteEpicById(int id);

    boolean deleteSubtaskById(int id);

    void printAllTasks();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean hasIntersection(Task newTask);

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasks();
}