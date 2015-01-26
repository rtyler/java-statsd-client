package com.timgroup.statsd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DummyStatsDServer {
    private final List<String> messagesReceived = new ArrayList<String>();
    private final DatagramSocket server;

    public DummyStatsDServer(int port) {
        try {
            server = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final DatagramPacket packet = new DatagramPacket(new byte[256], 256);
                    server.receive(packet);
                    messagesReceived.add(new String(packet.getData(), Charset.forName("UTF-8")).trim());
                } catch (Exception e) { }
            }
        }).start();
    }

    public void stop() {
        server.close();
    }

    public void waitForMessage() {
        while (messagesReceived.isEmpty()) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {}
        }
    }

    public List<String> messagesReceived() {
        return new ArrayList<String>(messagesReceived);
    }
}
