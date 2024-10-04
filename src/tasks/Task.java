package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    // Конструктор для добавления задач типа Task
    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this(name, description);
        this.id = 1;
        this.status = TaskStatus.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Конструктор для обновления/(загрузки из файла) задачи типа Task
    public Task(String name, String description, TaskStatus status, int id, Duration duration,
                LocalDateTime startTime) {
        this(name, description);
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Супер-конструктор для добавления задач типа Epic
    protected Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Супер-конструктор для обновления задач типа Epic
    protected Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Duration getDuration() {
        return duration;
    }

    protected void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    protected void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status
                && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", id, TaskTypes.TASK, name, description, status,
                duration.toMinutes(), startTime);
    }
}
