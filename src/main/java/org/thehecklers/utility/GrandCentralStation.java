package org.thehecklers.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by markheckler on 1/19/2016.
 */
@Service
public class GrandCentralStation {
    @Autowired
    MissionControl missionControl;

    @PostConstruct
    public void runIt() {
        try {
            if (missionControl.connect()) {
                missionControl.notifyObservers(missionControl.createBeanFromReading("{6210,120,98989}"));
                System.out.println("Notified observers of first reading.");
                missionControl.notifyObservers(missionControl.createBeanFromReading("{5220,210,100000}"));
                System.out.println("Notified observers of second reading.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
