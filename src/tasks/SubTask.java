package tasks;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    // Конструктор для добавления задач класса tasks.SubTask
    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    // Конструктор для обновления задачи класса tasks.SubTask
    public SubTask(String name, String description, TaskStatus status, int id, int epicId) {
        super(name, description, status, id);
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
}
