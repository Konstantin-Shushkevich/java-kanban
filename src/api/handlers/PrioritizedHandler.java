package api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.manager.TaskManager;

import java.io.IOException;

import static api.server.HttpTaskServer.getGson;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathAsArray = path.split("/");
        int pathLength = pathAsArray.length;
        Gson gson = getGson();

        if (method.equals("GET") && pathLength == 2) {
            try {
                String jsonResponse = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, jsonResponse);
            } catch (IOException e) {
                sendSomethingWentWrong(exchange);
                throw new RuntimeException(e);
            }
        } else {
            sendSomethingWentWrong(exchange);
        }
    }
}
