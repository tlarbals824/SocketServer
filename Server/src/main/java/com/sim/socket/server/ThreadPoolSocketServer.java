package com.sim.socket.server;

import com.sim.socket.handler.ClientSocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ThreadPoolSocketServer {
    private static final Logger LOGGER = Logger.getLogger(ThreadPoolSocketServer.class.getName());
    private static final Integer SOCKET_SERVER_PORT = 8080;
    private static final Integer CORE_POOL_SIZE = 10;
    private static final Integer MAX_POOL_SIZE = 200;
    private static final Integer MAX_QUEUE_SIZE = 100;
    private static final Integer KEEP_ALIVE_TIME = 60 * 60;
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<>(MAX_QUEUE_SIZE)
    );

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SOCKET_SERVER_PORT)) {
            LOGGER.info("server port is " + SOCKET_SERVER_PORT);
            LOGGER.info("server is ready, wait connect request....");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                THREAD_POOL.submit(ClientSocketHandler.create(clientSocket));
            }
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to create server socket, error message: " + e.getMessage());
        }
    }
}
