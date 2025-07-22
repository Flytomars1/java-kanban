package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, null, null);
    }

    public void addSubtaskId(int id) {
        if (id == this.getId()) {
            return; // игнорируем попытку добавить себя как подзадачу
        }
        subtaskIds.add(id);
    }

    public void removeSubtaskId(Integer id) {
        subtaskIds.remove(id);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public LocalDateTime calculateStartTime(List<Subtask> allSubtasks) {
        return allSubtasks.stream()
                .filter(st -> subtaskIds.contains(st.getId()))
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public Duration calculateDuration(List<Subtask> allSubtasks) {
        return allSubtasks.stream()
                .filter(st -> subtaskIds.contains(st.getId()))
                .map(Task::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public LocalDateTime calculateEndTime(List<Subtask> allSubtasks) {
        return allSubtasks.stream()
                .filter(st -> subtaskIds.contains((st.getId())))
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public void updateEpicFromSubtasks(List<Subtask> allSubtasks) {
        LocalDateTime newStartTime = calculateStartTime(allSubtasks);
        Duration newDuration = calculateDuration(allSubtasks);
        LocalDateTime newEndTime = calculateEndTime(allSubtasks);

        super.setStartTime(newStartTime);
        super.setDuration(newDuration);
        this.endTime = newEndTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
