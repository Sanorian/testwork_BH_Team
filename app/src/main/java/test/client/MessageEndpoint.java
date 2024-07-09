package test.client;

import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import test.server.Message;

import java.io.IOException;

public class MessageEndpoint extends Endpoint {
    private Session session;

    public void onOpen(Session session, EndpointConfig config){
        this.session = session;
        this.session.addMessageHandler(new MessageHandler.Whole<Message>() {

            @Override
            public void onMessage(Message message) {
                Client.writeDataToTheDataBase(message);
            }
        });
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
