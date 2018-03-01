package core.monitors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import core.HttpEvent;

import java.util.HashMap;

public class MostSectionHitsForDuration extends HttpAlertMonitor {
    private final int intervalInSeconds;

    private HashMap<String, Integer> sectionHitCounts = new HashMap<String, Integer>();
    private String maxHitsSection = "";
    private int totalHitsForDuration;

    @Inject
    public MostSectionHitsForDuration(@Named("core.monitors.mostsectionhitsforduration.interval.in.seconds") int intervalInSeconds) {
        this.intervalInSeconds = intervalInSeconds;
    }

    public int getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public void processEvent(HttpEvent event) {
        addToSectionHits(event.getSection());
    }

    public void run() {
        if(maxHitsSection.equals("")) {
            System.out.println(String.format("No activity in %d seconds", intervalInSeconds));
        } else {
            System.out.println(String.format("Report for %d second duration: %d total hits across %d unique sections. section '%s' had most hits with %d : (%.2f%%).",
                    intervalInSeconds,
                    totalHitsForDuration,
                    sectionHitCounts.size(),
                    maxHitsSection,
                    getMaxSectionHitCount(),
                    getMaxHitPercentage()
            ));
        }
        clearIntervalStats();
    }

    private void addToSectionHits(String section) {
        if(!sectionHitCounts.containsKey(section)) {
            sectionHitCounts.put(section, 0);
        }

        int hits = sectionHitCounts.get(section) + 1;
        sectionHitCounts.put(section, hits);
        if(maxHitsSection.equals("") || getMaxSectionHitCount() < hits) {
            maxHitsSection = section;
        }

        totalHitsForDuration++;
    }

    private void clearIntervalStats() {
        sectionHitCounts = new HashMap<String, Integer>();
        maxHitsSection = "";
        totalHitsForDuration = 0;
    }

    private double getMaxHitPercentage() {
        return ((double) getMaxSectionHitCount() / totalHitsForDuration) * 100;
    }

    private int getMaxSectionHitCount() {
        return sectionHitCounts.get(maxHitsSection);
    }
}
