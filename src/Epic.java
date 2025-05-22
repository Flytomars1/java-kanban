import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
