package com.sim.socket.handler;

import com.sim.socket.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientSocketHandler implements Runnable{
    private static final Logger LOGGER = Logger.getLogger(ClientSocketHandler.class.getName());
    private final Socket clientSocket;

    private ClientSocketHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public static ClientSocketHandler create(Socket clientSocket){
        return new ClientSocketHandler(clientSocket);
    }

    @Override
    public void run() {
        try (
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            LOGGER.info(LogUtils.getCurrentThreadInfo()+"client socket connect request was accepted, waiting message from client...");
            out.println("connected to server");

            String receiveData;
            while ((receiveData = in.readLine()) != null) {
                System.out.println(LogUtils.getCurrentThreadInfo()+"received message: " + receiveData);
            }
            System.out.println("disconnected...");
        }catch (IOException e) {
            LOGGER.severe(LogUtils.getCurrentThreadInfo()+"error occur when attempt to connect client socket, error message: " + e.getMessage());
        }finally {
            try{
                this.clientSocket.close();
            }catch (IOException e){
                LOGGER.severe(LogUtils.getCurrentThreadInfo()+"error occur when attempt to close client socket, error message: " + e.getMessage());
            }
        }
    }
}
