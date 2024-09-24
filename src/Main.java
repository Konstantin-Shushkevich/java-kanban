import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import utils.Managers;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager();
        /*FileBackedTaskManager downloadedFileBackedTaskManager = Managers.downloadFromFile("data\\data.csv");
        System.out.println(downloadedFileBackedTaskManager.getTaskList());
        System.out.println();
        System.out.println(downloadedFileBackedTaskManager.getEpicList());
        System.out.println();
        System.out.println(downloadedFileBackedTaskManager.getSubTaskList());


        Epic epicWithSubTasks = new Epic("Курс Java", "Изучить Java");
        Epic epicNoSubTasks = new Epic("Успешно завершить 2-й модуль", "Сдать ФЗ №6 и т.д.");

        inMemoryTaskManager.addEpic(epicWithSubTasks);
        inMemoryTaskManager.addEpic(epicNoSubTasks);

        SubTask subTask1 = new SubTask("Изучить Java Core", "Внимательно", epicWithSubTasks.getId());
        SubTask subTask2 = new SubTask("Изучить Spring", "Досконально", epicWithSubTasks.getId());
        SubTask subTask3 = new SubTask("Диплом", "Качественно", epicWithSubTasks.getId());

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
        System.out.println(); ЫЫЫЫЫЫ*/


        Task task1 = new Task("Изучить Java Core", "Внимательно");
        Task task2 = new Task("Изучить Spring", "Досконально");
        Task task3 = new Task("Диплом", "Качественно");
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        fileBackedTaskManager.addTask(task3);

        Epic epicWithSubTasks = new Epic("Курс Java", "Изучить Java");
        Epic epicNoSubTasks = new Epic("Успешно завершить 2-й модуль", "Сдать ФЗ №6 и т.д.");

        fileBackedTaskManager.addEpic(epicWithSubTasks);
        fileBackedTaskManager.addEpic(epicNoSubTasks);

        SubTask subTask1 = new SubTask("Изучить Java Core", "Внимательно", epicWithSubTasks.getId());
        SubTask subTask2 = new SubTask("Изучить Spring", "Досконально", epicWithSubTasks.getId());
        SubTask subTask3 = new SubTask("Диплом", "Качественно", epicWithSubTasks.getId());

        /*fileBackedTaskManager.addSubTask(subTask1);
        fileBackedTaskManager.addSubTask(subTask2);
        fileBackedTaskManager.addSubTask(subTask3);*/


    }
}
