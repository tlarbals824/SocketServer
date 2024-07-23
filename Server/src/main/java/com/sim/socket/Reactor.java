package com.sim.socket;

import com.sim.socket.handler.AcceptHandler;
import com.sim.socket.handler.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;
import java.util.logging.Logger;

public class Reactor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Reactor.class.getName());
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();

        this.serverSocketChannel.bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        selectionKey.attach(new AcceptHandler(this.selector, this.serverSocketChannel));
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.selector.select();

                Set<SelectionKey> selected = this.selector.selectedKeys();
                selected.forEach(this::dispatch);
                selected.clear();
            }
        } catch (IOException e) {
            LOGGER.severe("error occur when attempt to run reactor, error message: " + e.getMessage());
        }
    }

    private void dispatch(SelectionKey key) {
        if(key.attachment() instanceof Handler handler){
            handler.handle();
        }
    }
}
