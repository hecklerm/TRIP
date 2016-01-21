package org.thehecklers.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thehecklers.model.Reading;
import org.thehecklers.ws.WsControlClient;
import org.thehecklers.ws.WsDataClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Observable;

/**
 * Created by markheckler on 1/19/2016.
 */
@Component
public class MissionControl extends Observable {
    private boolean isConnected;
    private static PrintStream remoteLog = null;

    private WsControlClient wsControl = null;
    private WsDataClient wsData = null;

    @Value("${uriWebSocket}")
    private String uriWebSocket;

    public boolean connect() throws Exception {
        // Initialize the log (PrintStream with autoflush)
        // ALWAYS start the logging FIRST!
        remoteLog = new PrintStream(new FileOutputStream(new File("SerialReadings.log")), true);

        if (uriWebSocket == null) {
            isConnected = false;
            // Get out of here!
            logIt("ERROR: Property 'uriWebSocket' missing from properties.");
            Exception e = new Exception("ERROR: Property 'uriWebSocket' missing from properties.");
            throw e;
        } else {
            System.out.println("uriWebSocket = |" + uriWebSocket + "|");
            isConnected = true;

            wsData = new WsDataClient(uriWebSocket);
            this.addObserver(wsData);

            wsControl = new WsControlClient(uriWebSocket, this);
            this.addObserver(wsControl);
        }

        return isConnected;
    }

    public static void logIt(String reading) {
//        if(remoteLog != null){
//            remoteLog.println(reading);
//        } else {
            System.out.println(reading);
//        }
    }

    public Reading createBeanFromReading(String reading) {
        Reading newBean = new Reading();

        // Remove braces from reading "set"
        // reading = reading.substring(1, reading.length() - 2); MAH: May have to restore for actual feed
        reading = reading.substring(1, reading.length() - 1);

        String[] values = reading.split("\\,");
        for (int x = 0; x < values.length; x++) {
            try {
                switch (x) {
                    case Reading.HUMIDITY:
                        newBean.setHum(Double.parseDouble(values[x]) / 100);
                        break;
                    case Reading.TEMPERATURE:
                        newBean.setTemp(Double.parseDouble(values[x]) / 100);
                        break;
                    case Reading.PRESSURE:
                        newBean.setPressure(Long.parseLong(values[x]));
                        break;
                }
            } catch (NumberFormatException nfe) {
                logIt("Error parsing " + reading);
            }
        }

        // MAH: For testing. Not sure we'll always want to trigger a publish for a new reading (but why not?)...
        this.setChanged();

        return newBean;
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }


}
