package com.sim.socket;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class FileRequestClient {
    private static final String FILE_PATH = "Client/src/main/resources/Hello.txt";
    private static final String SERVER_HOST = "localhost";
    private static final Integer SERVER_PORT = 8080;

    public void sendMessage(Boolean isZeroCopy) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             FileChannel fileChannel = fis.getChannel();
             SocketChannel socketChannel = SocketChannel.open();
        ) {
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            socketChannel.configureBlocking(true);

            long transferSize = 0;
            if (isZeroCopy) {
                transferSize = sendWithZeroCopy(socketChannel, fileChannel);
            } else {
                transferSize = sendWithNonZeroCopy(socketChannel, fileChannel);
            }

            ByteBuffer ackBuffer = ByteBuffer.allocate(1024);
            int byteRead = socketChannel.read(ackBuffer);

        } catch (IOException e) {
            System.err.println("error occur when attempt to connect server socket, error message: " + e.getMessage());
        }
    }

    private long sendWithNonZeroCopy(SocketChannel socketChannel, FileChannel fileChannel) throws IOException {
        long transferSize = 0;

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numberOfReadBytes = 0;
        while ((numberOfReadBytes = fileChannel.read(buffer)) != -1) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

            transferSize += Math.max(numberOfReadBytes, 0);
        }
        return transferSize;
    }

    private long sendWithZeroCopy(SocketChannel socketChannel, FileChannel fileChannel) throws IOException {
        return fileChannel.transferTo(0, fileChannel.size(), socketChannel); // java nio transferTo
    }
}
