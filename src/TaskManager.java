import java.util.ArrayList;
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
    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubtaskById(int id);

    void printAllTasks();

    List<Task> getHistory();

    //оставить
    ArrayList<Task> getAllTasks();
    ArrayList<Subtask> getSubtasksByEpicId(int epidId);
}