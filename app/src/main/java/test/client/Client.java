package test.client;
import jakarta.websocket.DeploymentException;
import test.server.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;


public class Client {

    private String serverIP, port;
    private ClientWebsocketHandler clientWebsocketHandler;

    public Client(String serverIP, String port){
        this.serverIP = serverIP;
        this.port = port;
        this.clientWebsocketHandler = new ClientWebsocketHandler();
    }

    public void setServerIP(String serverIP){
        this.serverIP = serverIP;
    }

    public String getServerIP() {
        return this.serverIP;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return this.port;
    }

    public String getServerAddress(){
        return "ws://"+this.serverIP+":"+this.port+"/message";
    }

    public void start() throws IOException, DeploymentException, URISyntaxException {
        this.clientWebsocketHandler.connectToServer(getServerAddress());
        this.clientWebsocketHandler.sendMessage("Start");
    }

    public void stop() throws IOException {
        this.clientWebsocketHandler.sendMessage("Stop");
    }

    public static void writeDataToTheDataBase(Message message){
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true");
            statement = connection.createStatement();
            Integer[] coordinatesArray = message.getCoordinatesArray();
            statement.executeUpdate("INSERT INTO main (T, X, Y) VALUES (" + message.getDatetime() + ", " + String.valueOf(coordinatesArray[0])  + ", " + String.valueOf(coordinatesArray[1])+");");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement!= null) statement.close();
                if (connection!= null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean createTXTFile(String fileName) throws IOException {
        File file = new File(fileName);
        return file.createNewFile();
    }

    private ArrayList<String> getDataFromDataBase(String orderBy){
        Connection connection = null;
        Statement statement = null;
        ArrayList<String> lines = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true");
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT (T, X, Y) FROM Main ORDER BY "+orderBy+" DESC;");
            while(rs.next()) {
                String t = rs.getString("T");
                String x = rs.getString("X");
                String y = rs.getString("Y");
                lines.add("(" + x + ";" + y + ") - " + t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement!= null) statement.close();
                if (connection!= null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return lines;
    }

    private void writeDataToTheLogFile(String fileName, ArrayList<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        lines.forEach(line->{
            try {
                writer.write(line);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void log(SortingParameter parameter) throws IOException {
        String orderBy = null;
        switch (parameter){
            case X -> orderBy = "X";
            case Y -> orderBy = "Y";
            case T -> orderBy="T";
        }
        String fileName = "log.txt";
        createTXTFile(fileName);
        ArrayList<String> data = getDataFromDataBase(orderBy);
        writeDataToTheLogFile(fileName, data);
    }
}
