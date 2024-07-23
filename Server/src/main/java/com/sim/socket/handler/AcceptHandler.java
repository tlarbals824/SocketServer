package com.sim.socket.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class AcceptHandler implements Handler {
    private static final Logger LOGGER = Logger.getLogger(AcceptHandler.class.getName());

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;


    public AcceptHandler(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void handle() {
        try {
            final SocketChannel channel = this.serverSocketChannel.accept();
            if (channel != null) {
                new EchoHandler(this.selector, channel);
            }
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to handle event, error message: " + e.getMessage());
        }
    }
}
