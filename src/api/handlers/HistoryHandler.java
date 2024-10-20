package api.handlers;

import api.server.HttpTaskServer;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathAsArray = path.split("/");
        int pathLength = pathAsArray.length;
        Gson gson = HttpTaskServer.getGson();

        if (method.equals("GET") && pathLength == 2) {
            try {
                String jsonResponse = gson.toJson(taskManager.getHistory());
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
