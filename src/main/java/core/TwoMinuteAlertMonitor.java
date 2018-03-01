package core;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TwoMinuteAlertMonitor extends HttpAlertMonitor {
    private int INITIAL_DELAY_IN_SECONDS = 1;
    private int INTERVAL_IN_SECONDS = 1;
    private int WINDOW_SIZE = 120;
    private int ALERT_THRESHOLD = 24;

    private int totalHitsInTwoMinutes = 0;
    private int[] hitsAtSecond = new int[WINDOW_SIZE];
    private int currentIndex = 0;
    private boolean goneOnceAround = false;

    private boolean isActiveAlert = false;

    @Inject
    public TwoMinuteAlertMonitor(@Named("core.twominutealertmonitor.alert.threshold") int alertThreshold) {
        ALERT_THRESHOLD = alertThreshold;
    }

    public int getIntervalInSeconds() {
        return INTERVAL_IN_SECONDS;
    }

    public int getInitialDelayInSeconds() {
        return INITIAL_DELAY_IN_SECONDS;
    }

    public void processEvent(HttpEvent event) {
        addToHits();
    }

    public void run() {
        if (averageMeetsThreshold()) {
            if (!isActiveAlert) {
                isActiveAlert = true;
                System.out.println("High traffic generated an alert - " + getHitRateAverageOutput() + ", triggered at " + getCurrentDateTime());
            }
        } else if(isActiveAlert) {
            isActiveAlert = false;
            System.out.println("High traffic alert recovered - " + getHitRateAverageOutput() + ", recovered at " + getCurrentDateTime());
        }

        if(hitsAtSecond[getNextIndex()] != 0) {
            totalHitsInTwoMinutes -= hitsAtSecond[getNextIndex()];
        }

        currentIndex = getNextIndex();
    }

    private String getHitRateAverageOutput() {
        return String.format("total hits in %d second window: '%d' average hit rate: '%f'", getAverageRateDivisor(), totalHitsInTwoMinutes, getAverageRate());
    }

    private int getNextIndex() {
        int nextIndex = currentIndex + 1;
        if (nextIndex >= WINDOW_SIZE) {
            goneOnceAround = true;
            nextIndex = 0;
        }

        return nextIndex;
    }

    private boolean averageMeetsThreshold() {
        return getAverageRate() >= ALERT_THRESHOLD;
    }

    private double getAverageRate() {
        return (double) totalHitsInTwoMinutes / getAverageRateDivisor();
    }

    private int getAverageRateDivisor() {
        return goneOnceAround ? WINDOW_SIZE : (currentIndex + 1);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void addToHits() {
        hitsAtSecond[currentIndex] += 1;
        totalHitsInTwoMinutes++;
    }

}
