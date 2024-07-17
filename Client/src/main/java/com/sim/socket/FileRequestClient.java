package com.sim.socket;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class FileRequestClient {
    private static final String FILE_PATH = "Client/src/main/resources/Test.txt";
    private static final String SERVER_HOST = "localhost";
    private static final Integer SERVER_PORT = 8080;

    public void sendMessage(Boolean isZeroCopy) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             FileChannel fileChannel = fis.getChannel();
        ) {

            if(isZeroCopy) {
                try(SocketChannel socketChannel = SocketChannel.open();){
                    socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
                    socketChannel.configureBlocking(true);
                    sendWithZeroCopy(socketChannel, fileChannel);
                }
            }else{
                try(Socket socket = new Socket(SERVER_HOST, SERVER_PORT)){
                    sendWithNonZeroCopy(socket, fis);
                }
            }

        } catch (IOException e) {
            System.err.println("error occur when attempt to connect server socket, error message: " + e.getMessage());
        }
    }

    private void sendWithNonZeroCopy(Socket socket, FileInputStream fis)throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = fis.read(buffer)) != -1) {
            socket.getOutputStream().write(buffer, 0, read);
        }
    }

    private void sendWithZeroCopy(SocketChannel socketChannel, FileChannel fileChannel)throws IOException{
        fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        ByteBuffer ackBuffer = ByteBuffer.allocate(255);
        socketChannel.read(ackBuffer);
        ackBuffer.clear();
    }
}
