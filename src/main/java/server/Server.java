package server;

import com.sun.net.httpserver.HttpServer;
import server.handlers.DefaultHandler;
import server.handlers.ItemHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_PORT = 65535;

    private HttpServer httpServer;
    private int port;

    public static void main(String[] args) throws IOException {
        Server server = new Server();

        if(args.length == 0) {
            server.start();
        } else if(args.length == 1) {
            server.start(Integer.parseInt(args[0]));
        } else {
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("java Server [port]");
    }

    void start() throws IOException {
        start(DEFAULT_PORT);
    }

    private void start(int port) throws IOException {
        httpServer = null;
        while(httpServer == null) {
            try {
                InetSocketAddress serverAddress = new InetSocketAddress(port);
                httpServer = HttpServer.create(serverAddress, 10);
                this.port = port;
            } catch(IOException ex) {
                System.out.println("Failed to start server on port " + port);

                port++;
                if(port > MAX_PORT) {
                    System.out.println("Failed to start server");
                    throw ex;
                }
            }
        }

        registerHandlers(httpServer);

        httpServer.start();
        System.out.println("Started server on port " + port);
    }

    private void registerHandlers(HttpServer server) {
        server.createContext("/", new DefaultHandler());
        server.createContext("/api/items", new ItemHandler());
    }

    void stop() {
        httpServer.stop(1);
    }

    public int getPort() {
        return port;
    }
}
