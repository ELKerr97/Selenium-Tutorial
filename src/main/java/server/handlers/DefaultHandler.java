package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultHandler implements HttpHandler {

    public void handle(HttpExchange httpExchange) throws IOException {
       String data = "";

        try {
            String path = httpExchange.getRequestURI().getPath().substring(1);
            if (path.equals("")) {
                path = "index.html";
            }

            Path pathToFile = Paths.get(getClass().getClassLoader().getResource(path).toURI());

            Stream<String> fileContents = Files.lines(pathToFile);
            data = fileContents.collect(Collectors.joining("\n"));

            fileContents.close();

            httpExchange.sendResponseHeaders(200, 0);
        } catch (URISyntaxException | NullPointerException e) {
            httpExchange.sendResponseHeaders(500, 0);
            data = "{\"msg\":\"Error\"}";
        } finally {
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(data.getBytes());
            outputStream.close();
        }
    }
}
