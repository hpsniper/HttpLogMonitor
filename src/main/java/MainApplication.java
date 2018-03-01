import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import core.*;
import core.monitors.HttpAlertMonitor;
import core.monitors.MostSectionHitsForDuration;
import core.monitors.TotalHitsAboveWatermark;
import org.apache.commons.io.input.Tailer;

public class MainApplication {
    private final TailListener tailListener;
    private final MostSectionHitsForDuration mostSectionHitsForDuration;
    private final TotalHitsAboveWatermark totalHitsAboveWatermark;
    private final String logfileLocation;

    @Inject
    public MainApplication(
            TailListener tailListener,
            MostSectionHitsForDuration mostSectionHitsForDuration,
            TotalHitsAboveWatermark totalHitsAboveWatermark,
            @Named("main.logfile.location") String logfileLocation
    ) {
        this.tailListener = tailListener;
        this.mostSectionHitsForDuration = mostSectionHitsForDuration;
        this.totalHitsAboveWatermark = totalHitsAboveWatermark;
        this.logfileLocation = logfileLocation;
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AlertModule());
        MainApplication app = injector.getInstance(MainApplication.class);
        app.run();
    }

    private void run() {
        setupMonitors();
        tailLogFileForMonitors();
    }

    private void setupMonitors() {
        int numCpus = Runtime.getRuntime().availableProcessors();
        System.out.println("Starting service using a threadpool of size '" + (numCpus + 1) + "'");
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(numCpus + 1);
        executor.scheduleAtFixedRate(
                mostSectionHitsForDuration,
                mostSectionHitsForDuration.getIntervalInSeconds(), // initialDelay
                mostSectionHitsForDuration.getIntervalInSeconds(), // run every interval
                TimeUnit.SECONDS
        );

        executor.scheduleAtFixedRate(
                totalHitsAboveWatermark,
                totalHitsAboveWatermark.getIntervalInSeconds(), // initialDelay
                totalHitsAboveWatermark.getIntervalInSeconds(), // run every interval
                TimeUnit.SECONDS
        );
    }

    private void tailLogFileForMonitors() {
        ArrayList<HttpAlertMonitor> monitors = new ArrayList<HttpAlertMonitor>();
        monitors.add(mostSectionHitsForDuration);
        monitors.add(totalHitsAboveWatermark);
        tailListener.setMonitors(monitors);
        File file = new File(getClass().getResource(logfileLocation).getFile());
        Tailer tailer = new Tailer(file, tailListener, 300);
        tailer.run();
    }
}
