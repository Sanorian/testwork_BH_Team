package test.client;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import test.server.Message;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientWebsocketHandler {

    private WebSocketContainer container;
    private MessageEndpoint endpoint;

    public ClientWebsocketHandler() {
        this.container = ContainerProvider.getWebSocketContainer();
        this.endpoint = new MessageEndpoint();
    }

    public void connectToServer(String address) throws URISyntaxException, DeploymentException, IOException {
        this.container.connectToServer(this.endpoint, new URI(address));
    }

    public void sendMessage(String message) throws IOException {
        this.endpoint.sendMessage(message);
    }

}