package com.sim.socket.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MultiplexingServer {
    private static final Logger LOGGER = Logger.getLogger(MultiplexingServer.class.getName());
    private static final Integer SOCKET_SERVER_PORT = 8080;
    private static final Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocket = ServerSocketChannel.open();
             Selector selector = Selector.open();
        ) {

            serverSocket.bind(new InetSocketAddress(SOCKET_SERVER_PORT));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.forEach(MultiplexingServer::dispatch);
                selectionKeys.clear();
            }
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to create server socket, error message: " + e.getMessage());
        }
    }

    private static void dispatch(SelectionKey key) {
        try {
            if (key.isValid()) {
                switch (key.readyOps()) {
                    case SelectionKey.OP_ACCEPT -> handleAcceptEvent(key);
                    case SelectionKey.OP_READ -> handleReadEvent(key);
                    case SelectionKey.OP_WRITE -> handleWriteEvent(key);
                }
            }
        } catch (ClosedChannelException e) {
            closeSocket((SocketChannel) key.channel());
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to handle event, error message: " + e.getMessage());
        }
    }

    private static void handleAcceptEvent(SelectionKey key) throws IOException {
        ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socket = socketChannel.accept();

        socket.configureBlocking(false);

        socket.register(key.selector(), SelectionKey.OP_READ);
        sockets.put(socket, ByteBuffer.allocateDirect(1024));
    }

    private static void handleReadEvent(SelectionKey key) throws IOException {
        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = sockets.get(socket);

        int data = socket.read(byteBuffer);

        if (data == -1) {
            closeSocket(socket);
            sockets.remove(socket);
        }


        byte[] bytes = new byte[byteBuffer.position()];
        byteBuffer.flip();
        byteBuffer.get(bytes);
        String message = new String(bytes, StandardCharsets.UTF_8);
        LOGGER.info("received message: " + message);


        socket.configureBlocking(false);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static void handleWriteEvent(SelectionKey key) throws IOException {
        SocketChannel socket = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = sockets.get(socket);

        byteBuffer.clear();
        byteBuffer.put("message received".getBytes(StandardCharsets.UTF_8));
        byteBuffer.flip();

        socket.write(byteBuffer);

        while (!byteBuffer.hasRemaining()) {
            byteBuffer.clear();
            key.interestOps(SelectionKey.OP_READ);
            closeSocket(socket);
        }
    }

    private static void closeSocket(SocketChannel socket) {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to close socket, error message: " + e.getMessage());
        }
    }


}
