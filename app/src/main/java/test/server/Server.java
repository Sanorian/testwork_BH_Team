package test.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class Server {
        public static void main(String[] args) {
            SpringApplication app = new SpringApplication(ServerController.class);
            app.setDefaultProperties(Collections.singletonMap("server.port", 8080));
            app.run(args);
        }
}
