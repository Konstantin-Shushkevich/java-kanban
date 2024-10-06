package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    // Конструктор для добавления задач типа SubTask
    public SubTask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicId = epicId;
    }

    // Конструктор для обновления задачи типа SubTask
    public SubTask(String name, String description, TaskStatus status, int id, int epicId, Duration duration,
                   LocalDateTime startTime) {
        super(name, description, status, id, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SubTask subTask = (SubTask) object;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toStringForFile() {
        Long duration = getDuration() == null
                ? null
                : getDuration().toMinutes();

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", getId(), TaskTypes.SUBTASK, getName(), getDescription(),
                getStatus(), duration, getStartTime(), getEpicId());
    }
}
