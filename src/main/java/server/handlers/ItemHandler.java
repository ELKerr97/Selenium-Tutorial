package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Item;
import util.Serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemHandler implements HttpHandler {

    private Integer currentIdCount = 0;
    private final Map<Integer, Item> itemStore = new HashMap<>();

    public void handle(HttpExchange httpExchange) throws IOException {
        switch (httpExchange.getRequestMethod()) {
            case "POST":
                this.handlePost(httpExchange);
                break;
            case "PUT":
                this.handlePut(httpExchange);
                break;
            case "DELETE":
                this.handleDelete(httpExchange);
                break;
            case "GET":
                this.handleGet(httpExchange);
                break;
            default:
                  httpExchange.sendResponseHeaders(500, 0);
                  OutputStream os = httpExchange.getResponseBody();
                  os.write("Error: Unkown method".getBytes());
                  os.close();
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        List<Item> items = new ArrayList<>(itemStore.values());
        OutputStream os = httpExchange.getResponseBody();
        httpExchange.sendResponseHeaders(200, 0);
        Serializer.serialize(items, os);
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        Item item = Serializer.deserialize(Item.class, httpExchange.getRequestBody());
        currentIdCount++;
        item.setId(currentIdCount);
        itemStore.put(currentIdCount, item);
        httpExchange.sendResponseHeaders(200,0);
        httpExchange.getResponseBody().close();
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] path = httpExchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(path[path.length - 1]);
        itemStore.remove(id);
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.getResponseBody().close();
    }

    private void handlePut(HttpExchange httpExchange) throws IOException {
        String[] path = httpExchange.getRequestURI().getPath().split("/");
        Integer id = Integer.parseInt(path[path.length - 1]);
        Item item = Serializer.deserialize(Item.class, httpExchange.getRequestBody());
        itemStore.put(id, item);
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.getResponseBody().close();
    }
}

