package core;

import java.util.HashMap;

public class TenSecondAlertMonitor extends HttpAlertMonitor {
    private int INITIAL_DELAY_IN_SECONDS = 10;
    private int INTERVAL_IN_SECONDS = 10;

    private HashMap<String, Integer> sectionHitMonitor = new HashMap<String, Integer>();
    private String maxHitsSection = "";

    public int getIntervalInSeconds() {
        return INTERVAL_IN_SECONDS;
    }

    public int getInitialDelayInSeconds() {
        return INITIAL_DELAY_IN_SECONDS;
    }

    public void processEvent(HttpEvent event) {
        addToSectionHits(event.getSection());
    }

    public void run() {
        if(maxHitsSection.equals("")) {
            System.out.println("No activity in 10 seconds");
        } else {
            System.out.println("Most hits in 10 seconds: section '" + maxHitsSection + "' with '" + sectionHitMonitor.get(maxHitsSection) + "' hits.");
        }
        clearSectionHits();
    }

    private void addToSectionHits(String section) {
        if(!sectionHitMonitor.containsKey(section)) {
            sectionHitMonitor.put(section, 0);
        }

        int hits = sectionHitMonitor.get(section) + 1;
        sectionHitMonitor.put(section, hits);
        if(maxHitsSection.equals("") || sectionHitMonitor.get(maxHitsSection) < hits) {
            maxHitsSection = section;
        }
    }

    private void clearSectionHits() {
        sectionHitMonitor = new HashMap<String, Integer>();
        maxHitsSection = "";
    }

}
