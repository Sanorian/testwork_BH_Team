package test.server;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static java.lang.Math.floor;
import static java.lang.Math.min;
import static java.lang.Thread.sleep;

public class App {
    private ArrayList<WebDriver> drivers = new ArrayList<>();
    private Process process;
    private Integer width, height;
    private Integer radius;

    public App() {
        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Integer browserWindowWidth = (int) (floor((double) screenSize.width / 2));
        Integer browserWindowHeight = (int) (floor((double) screenSize.height / 2));
        width = browserWindowWidth;
        height = browserWindowHeight;
        radius = min(width/4, height/4);
        runBrowser(0, 0, browserWindowWidth, browserWindowHeight);
        runBrowser(browserWindowWidth, 0, browserWindowWidth, browserWindowHeight);
        runBrowser(0, browserWindowHeight, browserWindowWidth, browserWindowHeight);
        runCursors();
    }

    private void runBrowser(Integer x, Integer y, Integer width, Integer height){
        WebDriver driver = new FirefoxDriver();
        Dimension windowSize = new Dimension(width, height);
        driver.manage().window().setSize(windowSize);
        Point windowPosition = new Point(x, y);
        driver.manage().window().setPosition(windowPosition);
        this.drivers.add(driver);
    }

    private void runCursors(){
        String command = "./create_pointer"; // замените на путь к вашей программе
        try {
            this.process = Runtime.getRuntime().exec(command);
            this.process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeAll(){
        this.drivers.forEach(WebDriver::quit);
        this.process.destroy();
    }

    public Integer[] getCoordinates(Double period, Double angle) throws InterruptedException {
        Double radians = 1/period * Math.PI / 180.0;
        Double x =  width/4 + radius * Math.cos(radians);
        Double y = height/4 + radius * Math.sin(radians);
        sleep((long) floor(period*1000));
        return new Integer[]{(int) floor(x), (int) floor(y)};
    }
}
