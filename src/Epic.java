public class Epic extends Task {
    public Epic(String title, String description, int id, TaskStatus status) {
        super(title, description, id, status);
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
