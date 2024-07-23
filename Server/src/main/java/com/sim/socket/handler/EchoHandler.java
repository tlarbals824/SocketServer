package com.sim.socket.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class EchoHandler implements Handler {
    private static final Logger LOGGER = Logger.getLogger(EchoHandler.class.getName());


    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private State state = State.READING;

    public EchoHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);

        this.selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        this.selectionKey.attach(this);
        selector.wakeup();
    }


    @Override
    public void handle() {
        try {
            switch (this.state) {
                case READING -> read();
                case SENDING -> send();
            }
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to handle event, error message: " + e.getMessage());
        }
    }

    private void read() throws IOException {
        int readCount = this.socketChannel.read(this.buffer);
        if (readCount == -1) {
            closeConnection();
            return;
        }

        if (readCount > 0) {
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String receiveData = new String(bytes);
            System.out.println("received message: " + receiveData);
        }

        selectionKey.interestOps(SelectionKey.OP_WRITE);
        state = State.SENDING;
    }

    private void send() throws IOException {
        this.socketChannel.write(buffer);
        buffer.clear();
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = State.READING;
    }

    private void closeConnection() {
        try {
            this.socketChannel.close();
            this.selectionKey.cancel();
            LOGGER.info("connection closed");
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to close connection, error message: " + e.getMessage());
        }
    }

    private enum State {
        READING, SENDING
    }
}
