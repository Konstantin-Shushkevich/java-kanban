package api.handlers;

import api.server.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.manager.TaskManager;
import tracker.tasks.Epic;

import java.io.IOException;
import java.io.InputStream;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
                        String jsonResponse = gson.toJson(taskManager.getEpicList());
                        sendText(exchange, jsonResponse);
                    } catch (IOException e) {
                        sendSomethingWentWrong(exchange);
                        throw new RuntimeException(e);
                    }
                } else if (pathLength == 3) {
                    try {
                        int id = Integer.parseInt(pathAsArray[2]);
                        Epic epic = taskManager.getEpicById(id);

                        if (epic != null) {
                            String jsonResponse = gson.toJson(epic);
                            sendText(exchange, jsonResponse);
                        } else {
                            sendNotFound(exchange);
                        }
                    } catch (NumberFormatException | IOException e) {
                        sendSomethingWentWrong(exchange);
                        throw new RuntimeException(e);
                    }
                } else if (pathLength == 4) {
                    try {
                        int id = Integer.parseInt(pathAsArray[2]);
                        Epic epic = taskManager.getEpicById(id);

                        if (epic != null) {
                            String jsonResponse = gson.toJson(epic.getSubTasksIdInEpic());
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
                    Epic epic;

                    try {
                        epic = gson.fromJson(body, Epic.class);
                    } catch (JsonSyntaxException e) {
                        sendSomethingWentWrong(exchange);
                        throw new RuntimeException(e);
                    }

                    if (epic != null) {
                        if (taskManager.addEpic(epic)) {
                            sendAnyCode(exchange, 201);
                        } else {
                            sendSomethingWentWrong(exchange);
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
                        if (taskManager.deleteEpicById(id)) {
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
