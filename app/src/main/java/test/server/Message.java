package test.server;

import java.util.Date;
import java.util.Map;

public class Message {
    private Date datetime;
    private Integer[] coordinatesArray;

    public Message(Integer[] coordinatesArray){
        this.datetime = new Date();
        this.coordinatesArray = coordinatesArray;
    }

    public Date getDatetime() {
        return datetime;
    }

    public Integer[] getCoordinatesArray() {
        return coordinatesArray;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public void setCoordinatesArray(Integer[] coordinatesArray) {
        this.coordinatesArray = coordinatesArray;
    }
}
