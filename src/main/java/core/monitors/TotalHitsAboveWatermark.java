package core.monitors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import core.HttpEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TotalHitsAboveWatermark extends HttpAlertMonitor {
    private final int intervalInSeconds;
    private final int alertWatermark;
    private final int windowSizeInSeconds;

    private int totalHitsInDuration = 0;
    private int[] hitsAtSecond;
    private int currentIndex = 0;
    private boolean goneOnceAround = false;

    private boolean isActiveAlert = false;

    @Inject
    public TotalHitsAboveWatermark(
            @Named("core.monitors.totalhitsabovewatermark.interval.in.seconds") int intervalInSeconds,
            @Named("core.monitors.totalhitsabovewatermark.alert.watermark") int alertWatermark,
            @Named("core.monitors.totalhitsabovewatermark.window.size.in.seconds") int windowSizeInSeconds
    ) {
        this.intervalInSeconds = intervalInSeconds;
        this.alertWatermark = alertWatermark;
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.hitsAtSecond = new int[this.windowSizeInSeconds];
    }

    public int getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public void processEvent(HttpEvent event) {
        addToHits();
    }

    public void run() {
        if (averageMeetsWatermark()) {
            if (!isActiveAlert) {
                isActiveAlert = true;
                System.out.println("High traffic alert triggered - " + getHitRateAverageOutput() + " - triggered at " + getCurrentDateTime());
            }
        } else if(isActiveAlert) {
            isActiveAlert = false;
            System.out.println("High traffic alert recovered - " + getHitRateAverageOutput() + " - recovered at " + getCurrentDateTime());
        }

        if(hitsAtSecond[getNextIndex()] != 0) {
            totalHitsInDuration -= hitsAtSecond[getNextIndex()];
        }

        currentIndex = getNextIndex();
    }

    private String getHitRateAverageOutput() {
        return String.format("total hits in %d second window: %d, average hit rate: %.2f", getAverageRateDivisor(), totalHitsInDuration, getAverageRate());
    }

    private int getNextIndex() {
        int nextIndex = currentIndex + 1;
        if (nextIndex >= windowSizeInSeconds) {
            goneOnceAround = true;
            nextIndex = 0;
        }

        return nextIndex;
    }

    private boolean averageMeetsWatermark() {
        return getAverageRate() >= alertWatermark;
    }

    private double getAverageRate() {
        return (double) totalHitsInDuration / getAverageRateDivisor();
    }

    private int getAverageRateDivisor() {
        return goneOnceAround ? (windowSizeInSeconds * intervalInSeconds) : ( (currentIndex + 1) * intervalInSeconds );
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void addToHits() {
        hitsAtSecond[currentIndex] += 1;
        totalHitsInDuration++;
    }

}
