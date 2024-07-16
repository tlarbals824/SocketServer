package com.sim.socket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final String SERVER_HOST = "localhost";
    private static final Integer SERVER_PORT = 8080;

    public static void main(String[] args) {
        try(Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        ){
            LOGGER.info("connected to server");

            while(true){
                System.out.print("please input message to send to server: ");
                String message = scanner.nextLine();
                if("exit".equalsIgnoreCase(message)){
                    break;
                }
                out.println(message);
            }
        }catch (IOException e){
            LOGGER.severe("error occur when attempt to connect server socket, error message: " + e.getMessage());
        }
    }
}
