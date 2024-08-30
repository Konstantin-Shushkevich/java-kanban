package tasks;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTasksIdInEpic;

    // Конструктор для добавления задач класса tasks.Epic
    public Epic(String name, String description) {
        super(name, description);
        this.subTasksIdInEpic = new ArrayList<>();
    }

    // Конструктор для обновления задачи типа tasks.Epic
    public Epic(String name, String description, int id) {
        super(name, description, null, id);
        this.subTasksIdInEpic = new ArrayList<>();
    }

    public List<Integer> getSubTasksIdInEpic() {
        return subTasksIdInEpic;
    }

    public void addSubTaskIdInEpic(int subTaskId) {
        subTasksIdInEpic.add(subTaskId);
    }

    public void removeSubTaskIdInEpic(Integer subTaskId) {
        subTasksIdInEpic.remove(subTaskId);
    }

    public void removeAllSubTaskIdInEpic() {
        subTasksIdInEpic.clear();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subTasksIdInEpic, epic.subTasksIdInEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIdInEpic);
    }
}
