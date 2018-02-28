package core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TwoMinuteAlertMonitor extends HttpAlertMonitor {
    private int INITIAL_DELAY_IN_SECONDS = 1;
    private int INTERVAL_IN_SECONDS = 1;
    private static int WINDOW_SIZE = 120;
    private static int ALERT_THRESHOLD = 24;

    private static int totalHitsInTwoMinutes = 0;
    private static int[] hitsAtSecond = new int[WINDOW_SIZE];
    private static int currentIndex = 0;

    private static boolean isActiveAlert = false;

    public int getIntervalInSeconds() {
        return INTERVAL_IN_SECONDS;
    }

    public int getInitialDelayInSeconds() {
        return INITIAL_DELAY_IN_SECONDS;
    }

    public void processEvent(HttpEvent event) {
        addToHits();
    }

    private int getNextIndex() {
        return (currentIndex + 1) % WINDOW_SIZE;
    }

    public void run() {
        if (totalHitsInTwoMinutes > ALERT_THRESHOLD) {
            if (!isActiveAlert) {
                isActiveAlert = true;
                System.out.println("High traffic generated an alert - hits = " + totalHitsInTwoMinutes + ", triggered at " + getCurrentDateTime());
            }
        } else if(isActiveAlert) {
            isActiveAlert = false;
            System.out.println("High traffic alert recovered at " + getCurrentDateTime());
        }

        if(hitsAtSecond[getNextIndex()] != 0) {
            totalHitsInTwoMinutes -= hitsAtSecond[getNextIndex()];
        }

        currentIndex = getNextIndex();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void addToHits() {
        hitsAtSecond[currentIndex] += 1;
        totalHitsInTwoMinutes++;
    }

}
