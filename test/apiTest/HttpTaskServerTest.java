package apiTest;

import api.server.HttpTaskServer;
import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.manager.InMemoryTaskManager;
import tracker.manager.TaskManager;
import tracker.tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static api.server.HttpTaskServer.getGson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
    Gson gson = getGson();

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        String taskJson = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        Task task2 = new Task("Other test task", "Testing task for test with identical time properties",
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        taskJson = gson.toJson(task2);

        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(),
                "Была добавлена задача, пересекающаяся с существующими");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        manager.addTask(task);
        Task taskUpdated = new Task("Test 2", "Testing task 2", TaskStatus.DONE, task.getId(),
                Duration.ofMinutes(5), LocalDateTime.now());
        String updatedTaskJson = gson.toJson(taskUpdated);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertEquals(TaskStatus.DONE, tasksFromManager.getFirst().getStatus(), "Некорректный статус задачи");

        /* Проверка корректности работы программы при попытке обновления Task, когда новый экземпляр не проходит
        проверку на пересечение с уже существующими задачами */
        Task task1 = new Task("Test 1", "Testing task 1", Duration.ofMinutes(15),
                LocalDateTime.of(2024, Month.FEBRUARY, 1, 0, 0));

        manager.addTask(task1);

        Task taskUpdatedTime = new Task("Test 2", "Testing task 2", TaskStatus.DONE, task.getId(),
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.FEBRUARY, 1, 0, 10));
        updatedTaskJson = gson.toJson(taskUpdatedTime);

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8081/tasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testTheCorrectnessOfGettingOfTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при попытке получения списка задач Task");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        Task taskFromJson = gson.fromJson(jsonObject, Task.class);
        assertEquals(task, taskFromJson, "Возвращаемый экземпляр Task не соответствует ожидаемому");

        url = URI.create("http://localhost:8081/tasks/1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка доступа по id к задаче типа Task");

        jsonElement = JsonParser.parseString(response.body());
        jsonObject = jsonElement.getAsJsonObject();
        taskFromJson = gson.fromJson(jsonObject, Task.class);
        assertEquals(task, taskFromJson, "Возвращаемый по id экземпляр Task не соответствует ожидаемому");

        url = URI.create("http://localhost:8081/tasks/2");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Ошибка при попытке получить Task по несуществующему id");
    }

    @Test
    public void testTheCorrectnessOfDeletionOfTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTaskList();

        assertEquals(0, tasksFromManager.size(), "Задача не была удалена");
    }

    @Test
    public void testAddEpicAndSubTask() throws IOException, InterruptedException {
        // Проверка корректности добавления задачи типа Epic
        Epic epicWithSubTasks = new Epic("Epic 1", "Epic for test with subTasks");

        String taskJson = gson.toJson(epicWithSubTasks);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Задача типа Epic не была добавлена");

        List<Epic> epicsFromManager = manager.getEpicList();

        assertNotNull(epicsFromManager, "Задачи типа Epic не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач типа Epic");
        assertEquals("Epic 1", epicsFromManager.getFirst().getName(),
                "Некорректное имя задачи типа Epic");

        // Проверка корректности добавления задачи типа SubTask
        SubTask subTask1 = new SubTask("Test 1", "Testing SubTask 1", 1, Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));
        taskJson = gson.toJson(subTask1);

        url = URI.create("http://localhost:8081/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Задача типа SubTask не была добавлена");

        List<SubTask> subTasksFromManager = manager.getSubTaskList();

        assertNotNull(subTasksFromManager, "Задачи типа SubTask не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач типа SubTask");
        assertEquals("Test 1", subTasksFromManager.getFirst().getName(),
                "Некорректное имя задачи типа SubTask");

        // Проверка на возможность добавления пересекающихся по времени задач типа SubTask
        SubTask subTask2 = new SubTask("Test 2", "Testing SubTask 2 incorrect time properties",
                1, Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));

        taskJson = gson.toJson(subTask2);

        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(),
                "Была добавлена задача типа SubTask, пересекающаяся с существующими");
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epicWithSubTasks = new Epic("Epic 1", "Epic for test with subTasks");
        SubTask subTask1 = new SubTask("SubTask test 1", "Testing SubTask 1", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));
        SubTask subTask1Updated = new SubTask("SubTask test 1", "Testing SubTask 1 updated",
                TaskStatus.DONE, 2, 1, Duration.ofMinutes(5),
                LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));

        manager.addEpic(epicWithSubTasks);
        manager.addSubTask(subTask1);


        String taskJson = gson.toJson(subTask1Updated);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Задача типа SubTask не была обновлена");

        List<SubTask> subTasksFromManager = manager.getSubTaskList();

        assertNotNull(subTasksFromManager, "Обновленные SubTasks не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество SubTask");
        assertEquals("SubTask test 1", subTasksFromManager.getFirst().getName(),
                "Некорректное имя задачи");
        assertEquals(TaskStatus.DONE, subTasksFromManager.getFirst().getStatus(),
                "Некорректный статус SubTask");
        assertEquals(TaskStatus.DONE, manager.getEpicById(1).getStatus(),
                "Некорректный статус связанного Epic");

        /* Проверка корректности работы программы при попытке обновления SubTask, когда новый экземпляр не проходит
        проверку на пересечение с уже существующими задачами */
        SubTask subTask2 = new SubTask("SubTask test 2", "Testing SubTask 2",
                TaskStatus.IN_PROGRESS, 2, 1, Duration.ofMinutes(120),
                LocalDateTime.of(2024, Month.JANUARY, 3, 0, 0));
        SubTask subTask1UpdatedForTimeTest = new SubTask("SubTask test 1", "Testing SubTask 1 updated",
                TaskStatus.DONE, 2, 1, Duration.ofMinutes(120),
                LocalDateTime.of(2024, Month.JANUARY, 3, 1, 0));

        manager.addSubTask(subTask2);

        taskJson = gson.toJson(subTask1UpdatedForTimeTest);

        url = URI.create("http://localhost:8081/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Задача типа SubTask была обновлена при " +
                "пересекающихся с другими задачами временных характеристиках");
    }

    @Test
    public void testTheCorrectnessOfGettingOfEpicsAndSubTasks() throws IOException, InterruptedException {
        // Инициализация тестового пространства
        Epic epicWithSubTasks = new Epic("Epic 1", "Epic for test with subTasks");
        Epic epicSimple = new Epic("Epic 2", "Epic for test without subTasks");


        SubTask subTask1 = new SubTask("SubTaskTest1", "Testing SubTask 1", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));
        SubTask subTask2 = new SubTask("SubTaskTest2", "Testing SubTask 2", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.FEBRUARY, 2, 0, 0));

        manager.addEpic(epicWithSubTasks);
        manager.addEpic(epicSimple);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при доступе к списку задач типа Epic");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        JsonObject jsonObject1 = jsonArray.get(0).getAsJsonObject();
        JsonObject jsonObject2 = jsonArray.get(1).getAsJsonObject();
        Epic epic1FromJson = gson.fromJson(jsonObject1, Epic.class);
        Epic epic2FromJson = gson.fromJson(jsonObject2, Epic.class);
        assertEquals(epic1FromJson, epicWithSubTasks,
                "Возвращаемый экземпляр Epic не соответствует ожидаемому");
        assertEquals(epic2FromJson, epicSimple, "Возвращаемый экземпляр Epic не соответствует ожидаемому");
        assertEquals(manager.getEpicList().size(), jsonArray.size(),
                "Количество возвращаемых задач типа Epic не соответствует ожидаемому");

        url = URI.create("http://localhost:8081/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при доступе к списку задач типа SubTask");

        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        jsonObject1 = jsonArray.get(0).getAsJsonObject();
        jsonObject2 = jsonArray.get(1).getAsJsonObject();
        SubTask subTask1FromJson = gson.fromJson(jsonObject1, SubTask.class);
        SubTask subTask2FromJson = gson.fromJson(jsonObject2, SubTask.class);
        assertEquals(subTask1FromJson, subTask1, "Возвращаемый экземпляр SubTask не соответствует ожидаемому");
        assertEquals(subTask2FromJson, subTask2, "Возвращаемый экземпляр SubTask не соответствует ожидаемому");
        assertEquals(manager.getSubTaskList().size(), jsonArray.size(),
                "Количество возвращаемых задач типа SubTask не соответствует ожидаемому");

        // Тестирование доступа по id
        url = URI.create("http://localhost:8081/epics/1");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при попытке доступа к Epic по id");

        jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epicFromJson = gson.fromJson(jsonObject, Epic.class);
        assertEquals(epicWithSubTasks, epicFromJson,
                "Возвращаемый по id экземпляр Epic не соответствует ожидаемому");

        url = URI.create("http://localhost:8081/epics/3");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Программа работает некорректно при попытке доступа к задаче типа Epic по несуществующему id");

        url = URI.create("http://localhost:8081/subtasks/3");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при попытке доступа к SubTask по id");

        jsonElement = JsonParser.parseString(response.body());
        jsonObject = jsonElement.getAsJsonObject();
        SubTask subTaskFromJson = gson.fromJson(jsonObject, SubTask.class);
        assertEquals(subTask1, subTaskFromJson,
                "Возвращаемый по id экземпляр SubTask не соответствует ожидаемому");

        url = URI.create("http://localhost:8081/subtasks/7");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Программа работает некорректно при попытке доступа к SubTask по несуществующему id");

        url = URI.create("http://localhost:8081/epics/1/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Программа работает некорректно при попытке доступа по id Epic к субтаскам, харанящемся в нем");

        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        JsonElement jsonElement1 = jsonArray.get(0);
        JsonElement jsonElement2 = jsonArray.get(1);
        Integer subTask1Id = gson.fromJson(jsonElement1, Integer.class);
        Integer subTask2Id = gson.fromJson(jsonElement2, Integer.class);
        assertEquals(subTask1Id, subTask1.getId(),
                "Возвращаемое id экземпляра SubTask не соответствует ожидаемому");
        assertEquals(subTask2Id, subTask2.getId(),
                "Возвращаемое id экземпляра SubTask не соответствует ожидаемому");
        assertEquals(manager.getSubTasksInEpic(1).size(), jsonArray.size(),
                "Количество id SubTask не соответствует ожидаемому");

        url = URI.create("http://localhost:8081/epics/3/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "Программа работает некорректно при попытке доступа по id Epic к задачам типа SubTask");
    }

    @Test
    public void testTheCorrectnessOfDeletionOfSubTasksAndEpics() throws IOException, InterruptedException {
        // Инициализация тестового пространства
        Epic epicWithSubTasks = new Epic("Epic 1", "Epic for test with subTasks");
        Epic epicSimple = new Epic("Epic 2", "Epic for test without subTasks");


        SubTask subTask1 = new SubTask("SubTaskTest1", "Testing SubTask 1", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));
        SubTask subTask2 = new SubTask("SubTaskTest2", "Testing SubTask 2", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.FEBRUARY, 2, 0, 0));

        manager.addEpic(epicWithSubTasks);
        manager.addEpic(epicSimple);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/epics/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при удалении по id задачи типа Epic");

        List<Epic> epicsFromManager = manager.getEpicList();

        assertNotNull(epicsFromManager, "Задачи типа Epic не возвращаются");
        assertEquals(1, epicsFromManager.size(),
                "Некорректное количество задач типа Epic после удаления");
        assertEquals(epicsFromManager.getFirst(), epicWithSubTasks,
                "Оставшаяся после удаления задача типа Epic не соответствует ожидаемому результату");

        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8081/subtasks/4");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при удалении по id задачи типа SubTask");

        List<SubTask> subTasksFromManager = manager.getSubTaskList();

        assertNotNull(subTasksFromManager, "Задачи типа SubTask не возвращаются");
        assertEquals(1, subTasksFromManager.size(),
                "Некорректное количество задач типа SubTask после удаления");
        assertEquals(subTasksFromManager.getFirst(), subTask1,
                "Оставшаяся после удаления задача типа SubTask не соответствует ожидаемому результату");
    }

    @Test
    public void testTheCorrectnessOfGettingHistoryAndPrioritizedTasks() throws IOException, InterruptedException {
        // Инициализация тестового пространства
        Epic epicWithSubTasks = new Epic("Epic 1", "Epic for test with subTasks");
        Epic epicSimple = new Epic("Epic 2", "Epic for test without subTasks");


        SubTask subTask1 = new SubTask("SubTaskTest1", "Testing SubTask 1", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.JANUARY, 2, 0, 0));
        SubTask subTask2 = new SubTask("SubTaskTest2", "Testing SubTask 2", 1,
                Duration.ofMinutes(5), LocalDateTime.of(2024, Month.FEBRUARY, 2, 0, 0));

        manager.addEpic(epicWithSubTasks);
        manager.addEpic(epicSimple);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        manager.getSubTaskById(epicWithSubTasks.getId());
        manager.getEpicById(subTask2.getId());
        manager.getEpicById(epicSimple.getId());
        manager.getSubTaskById(subTask1.getId());
        manager.getSubTaskById(epicWithSubTasks.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8081/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ошибка при получении истории просмотров");

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(manager.getHistory().size(), jsonArray.size(),
                "Количество возвращаемых просмотренных задач не соответствует ожидаемому");

        url = URI.create("http://localhost:8081/prioritized");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),
                "Ошибка при получении списка задач, отсортированного по приоритетности");

        jsonElement = JsonParser.parseString(response.body());
        jsonArray = jsonElement.getAsJsonArray();
        assertEquals(manager.getPrioritizedTasks().size(), jsonArray.size(),
                "Количество возвращаемых в списке приоритетности задач не соответствует ожидаемому");
    }
}
