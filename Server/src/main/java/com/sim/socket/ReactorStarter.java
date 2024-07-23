package com.sim.socket;

import java.io.IOException;

public class ReactorStarter {
    private static final Integer SOCKET_SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {
        Reactor reactor = new Reactor(SOCKET_SERVER_PORT);
        reactor.run();
    }
}
