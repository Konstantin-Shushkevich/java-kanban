package api.handlers;

import api.server.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.manager.TaskManager;
import tracker.tasks.Task;

import java.io.IOException;
import java.io.InputStream;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathAsArray = path.split("/");
        int pathLength = pathAsArray.length;
        Gson gson = HttpTaskServer.getGson();

        switch (method) {
            case "GET":
                if (pathLength == 2) {
                    try {
                        String jsonResponse = gson.toJson(taskManager.getTaskList());
                        sendText(exchange, jsonResponse);
                    } catch (IOException e) {
                        sendSomethingWentWrong(exchange);
                        throw new RuntimeException(e);
                    }
                } else if (pathLength == 3) {
                    try {
                        int id = Integer.parseInt(pathAsArray[2]);
                        Task task = taskManager.getTaskById(id);

                        if (task != null) {
                            String jsonResponse = gson.toJson(taskManager.getTaskById(id));
                            sendText(exchange, jsonResponse);
                        } else {
                            sendNotFound(exchange);
                        }
                    } catch (NumberFormatException | IOException e) {
                        sendSomethingWentWrong(exchange);
                        throw new RuntimeException(e);
                    }
                } else {
                    sendSomethingWentWrong(exchange);
                }
                break;

            case "POST":
                if (pathLength == 2) {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    Task task;

                    try {
                        task = gson.fromJson(body, Task.class);
                    } catch (JsonSyntaxException e) {
                        sendSomethingWentWrong(exchange);
                        throw new RuntimeException(e);
                    }

                    if (task != null && task.getId() == 0) {
                        if (taskManager.addTask(task)) {
                            sendAnyCode(exchange, 201);
                        } else {
                            sendHasInteractions(exchange);
                        }
                    } else if (task != null && task.getId() > 0) {
                        if (taskManager.updateTask(task)) {
                            sendAnyCode(exchange, 201);
                        } else {
                            sendHasInteractions(exchange);
                        }
                    } else {
                        sendSomethingWentWrong(exchange);
                    }
                } else {
                    sendSomethingWentWrong(exchange);
                }
                break;

            case "DELETE":
                if (pathLength == 3) {
                    try {
                        int id = Integer.parseInt(pathAsArray[2]);
                        if (taskManager.deleteTaskById(id)) {
                            sendAnyCode(exchange, 200);
                        } else {
                            sendSomethingWentWrong(exchange);
                        }
                    } catch (NumberFormatException | IOException e) {
                        sendSomethingWentWrong(exchange);
                        throw new RuntimeException(e);
                    }
                } else {
                    sendSomethingWentWrong(exchange);
                }
                break;

            default:
                sendSomethingWentWrong(exchange);
        }
    }
}
