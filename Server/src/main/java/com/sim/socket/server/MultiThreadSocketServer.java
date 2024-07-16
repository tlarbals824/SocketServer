package com.sim.socket.server;

import com.sim.socket.handler.ClientSocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class MultiThreadSocketServer {
    private static final Logger LOGGER = Logger.getLogger(MultiThreadSocketServer.class.getName());
    private static final Integer SOCKET_SERVER_PORT = 8080;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SOCKET_SERVER_PORT)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(ClientSocketHandler.create(clientSocket)).start();
            }
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to create server socket, error message: " + e.getMessage());
        }
    }
}
