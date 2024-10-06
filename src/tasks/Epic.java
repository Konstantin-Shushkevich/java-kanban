package tasks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTasksIdInEpic;
    private LocalDateTime endTime;

    // Конструктор для добавления задач типа Epic без субтасок
    public Epic(String name, String description) {
        super(name, description);
        setStatus(TaskStatus.NEW);
        this.subTasksIdInEpic = new ArrayList<>();
    }

    // Конструктор для обновления задачи типа Epic и его загрузки из файла
    public Epic(String name, String description, int id) {
        super(name, description, id);
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subTasksIdInEpic, epic.subTasksIdInEpic)
                && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIdInEpic, endTime);
    }

    @Override
    public String toStringForFile() {
        Long duration = getDuration() == null
                ? null
                : getDuration().toMinutes();

        return String.format("%s,%s,%s,%s,%s,%s,%s", getId(), TaskTypes.EPIC, getName(), getDescription(), getStatus(),
                duration, getStartTime());
    }

    // Метод для записи в файл задачи типа Epic, в которую не были добавлены SubTasks
    public String toStringForFileFromEmptyEpic() {
        return String.format("%s,%s,%s,%s", getId(), TaskTypes.EPIC, getName(), getDescription());
    }
}
