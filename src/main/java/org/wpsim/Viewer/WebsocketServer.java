package org.wpsim.Viewer;

import BESA.ExceptionBESA;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import io.undertow.server.handlers.resource.PathResourceManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import org.wpsim.Simulator.wpsStart;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;

public class WebsocketServer implements Runnable {

    private final List<WebSocketChannel> activeChannels = Collections.synchronizedList(new ArrayList<>());
    private static WebsocketServer instance;

    public static synchronized WebsocketServer getInstance() {
        if (instance == null) {
            instance = new WebsocketServer();
        }
        return instance;
    }

    @Override
    public void run() {

        ResourceManager resourceManager = null;
        try {
            resourceManager = new PathResourceManager(Paths.get(wpsStart.class.getClassLoader().getResource("web/").toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(path()
                        .addPrefixPath("/wpsViewer", websocket(new WebSocketConnectionCallback() {
                            @Override
                            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
                                activeChannels.add(channel);
                                channel.addCloseTask(ch -> activeChannels.remove(ch));
                                channel.getReceiveSetter().set(new AbstractReceiveListener() {
                                    @Override
                                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                                        String msg = message.getData();
                                        System.out.println("Received message: " + msg);

                                        if (msg.contains("start")) {
                                            wpsStart.startAgents();
                                        } else if (msg.contains("stop")) {
                                            wpsStart.stopSimulation();
                                        } else if(msg.contains("setup")) {
                                            WebSockets.sendText("q=" + wpsStart.peasantFamiliesAgents, channel, null);
                                        }else{
                                            System.out.println("Unknown message: " + msg);
                                        }

                                    }
                                });
                                channel.resumeReceives();
                            }
                        }))
                        //.addPrefixPath("/", resource(resourceManager).addWelcomeFiles("index.html"))
                )
                .build();
        System.out.println("WebSocket Server Started on port 8080");
        server.start();
    }

    public void broadcastMessage(String message) {
        synchronized (activeChannels) {
            for (WebSocketChannel channel : activeChannels) {
                if (channel.isOpen()) {
                    //System.out.println("Sending message: " + message);
                    WebSockets.sendText(message, channel, null);
                }
            }
        }
    }
}
