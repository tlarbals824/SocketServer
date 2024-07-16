package com.sim.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class SingleThreadBlockingSocketServer {
    private static final Logger LOGGER = Logger.getLogger(SingleThreadBlockingSocketServer.class.getName());
    private static final Integer SOCKET_SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SOCKET_SERVER_PORT)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                ) {
                    LOGGER.info("client socket connect request was accepted, waiting message from client...");
                    out.println("connected to server");

                    String receiveData;
                    while ((receiveData = in.readLine()) != null) {
                        System.out.println("received message: " + receiveData);
                    }
                    System.out.println("disconnected...");
                } catch (IOException e) {
                    LOGGER.severe("error occur when attempt to connect client socket, error message: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to create server socket, error message: " + e.getMessage());
        }
    }
}
