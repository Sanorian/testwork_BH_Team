package test.server;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static java.lang.Math.floor;
import static java.lang.Math.min;
import static java.lang.Thread.sleep;

public class App {
    private ArrayList<Process> browserProcessList = new ArrayList<>();
    private Process cursorProcess;

    public App(Integer numberOfWindows) {
        runBrowsers(numberOfWindows);
        runCursors(numberOfWindows);
    }

    private void runCursors(Integer numberOfCursors){
        String command = "./create_pointer "+numberOfCursors.toString();
        try {
            this.cursorProcess = Runtime.getRuntime().exec(command);
            this.cursorProcess.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.cursorProcess.getInputStream()));
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
        // wmctrl -i pid -c pid - убивание окна по pid
        for (Process process : browserProcessList){
            process.destroy();
        }
        cursorProcess.destroy();
    }

    public Integer[] getCoordinates() throws InterruptedException {
        // написать расчет координат. Или даже получение
        return new Integer[]{};
    }

    private void runBrowsers(Integer numberOfWindows){
        for (int i=0; i<numberOfWindows; i++){
            runBrowser();
        }
        resizeAndMoveBrowsers(numberOfWindows);
    }

    private void runBrowser(){
        try {
            Process browser = Runtime.getRuntime().exec("firefox");
            browser.waitFor();
            this.browserProcessList.add(browser);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void resizeAndMoveBrowsers(Integer numberOfWindows){
        ArrayList<String> browsersIdList = new ArrayList<>();
        try {
            Process windowsList = Runtime.getRuntime().exec("wmctrl -l");
            windowsList.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(windowsList.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Firefox")){
                    browsersIdList.add(line.substring(0, 9));
                }
            }
            reader.close();
            Integer x0 = 0, y0 = 0;
            java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Integer widthOfBrowserWindow = (int) floor(2*screenSize.getWidth()/(numberOfWindows + numberOfWindows%2));
            Integer heightOfBrowserWindow = (int) floor(screenSize.getHeight()/2);
            for (String pid: browsersIdList){
                resizeAndMoveBrowser(pid, 0, 0, widthOfBrowserWindow, heightOfBrowserWindow);
                if (x0+widthOfBrowserWindow>screenSize.getWidth()){
                    x0 = 0;
                    y0 += heightOfBrowserWindow;
                } else {
                    x0 += widthOfBrowserWindow;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resizeAndMoveBrowser(String pid, Integer x0, Integer y0, Integer windowWidth, Integer windowHeight){
        try {
            Process resizeMovingProcess = Runtime.getRuntime().exec("wmctrl -i "+pid+" -e 0, "+x0+", "+y0+", "+windowWidth+", "+windowHeight);
            resizeMovingProcess.waitFor();
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
    }
}
