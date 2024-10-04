import manager.InMemoryTaskManager;
import tasks.Epic;
import tasks.SubTask;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
        Epic epicWithSubTasks = new Epic("Большое дело", "Описание большого дела");
        Epic epicNoSubTasks = new Epic("Еще одно большое дело", "Описание второго большого дела");

        inMemoryTaskManager.addEpic(epicWithSubTasks);
        inMemoryTaskManager.addEpic(epicNoSubTasks);

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", epicWithSubTasks.getId(),
                Duration.ofHours(24), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", epicWithSubTasks.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.FEBRUARY, 6, 12, 0));
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", epicWithSubTasks.getId(),
                Duration.ofMinutes(120), LocalDateTime.of(2024, Month.FEBRUARY, 6, 15, 0));

        inMemoryTaskManager.addSubTask(subTask1);
        inMemoryTaskManager.addSubTask(subTask2);
        inMemoryTaskManager.addSubTask(subTask3);

        inMemoryTaskManager.getSubTaskById(subTask1.getId());
        inMemoryTaskManager.getSubTaskById(subTask2.getId());
        inMemoryTaskManager.getEpicById(epicWithSubTasks.getId());
        inMemoryTaskManager.getEpicById(epicNoSubTasks.getId());
        inMemoryTaskManager.getSubTaskById(subTask1.getId());

        System.out.print("Первая проверка по доп. заданию: ");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();

        inMemoryTaskManager.getEpicById(epicNoSubTasks.getId());
        inMemoryTaskManager.getSubTaskById(subTask3.getId());
        inMemoryTaskManager.getSubTaskById(subTask2.getId());

        System.out.print("Вторая проверка по доп. заданию: ");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();

        inMemoryTaskManager.deleteEpicById(epicNoSubTasks.getId());
        inMemoryTaskManager.deleteSubTaskById(subTask2.getId());

        System.out.print("Вывод истории после удаления эпика без субтасок и субтаски: ");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();

        inMemoryTaskManager.deleteEpicById(epicWithSubTasks.getId());

        System.out.print("Вывод истории после удаления эпика с субтасками (должен вывестись пустой список): ");
        System.out.println(inMemoryTaskManager.getHistory());
        System.out.println();
    }
}
