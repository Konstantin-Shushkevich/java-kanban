package tasks;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;

    // Конструктор для добавления задач класса tasks.Task
    public Task(String name, String description) {
        this(name, description, TaskStatus.NEW, 1);
    }

    // Конструктор для обновления задачи типа tasks.Task
    public Task(String name, String description, TaskStatus status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return "{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", id=" + id + ", status=" + status + '}';
    }
}
