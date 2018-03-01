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
import org.apache.commons.io.input.Tailer;

public class MainApplication {
    private final TenSecondAlertMonitor tenSecondAlertMonitor;
    private final TwoMinuteAlertMonitor twoMinuteAlertMonitor;
    private final String logfileLocation;

    @Inject
    public MainApplication(
            TenSecondAlertMonitor tenSecondAlertMonitor,
            TwoMinuteAlertMonitor twoMinuteAlertMonitor,
            @Named("main.logfile.location") String logfileLocation
    ) {
        this.tenSecondAlertMonitor = tenSecondAlertMonitor;
        this.twoMinuteAlertMonitor = twoMinuteAlertMonitor;
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
                tenSecondAlertMonitor,
                tenSecondAlertMonitor.getInitialDelayInSeconds(),
                tenSecondAlertMonitor.getIntervalInSeconds(),
                TimeUnit.SECONDS
        )
        ;
        executor.scheduleAtFixedRate(
                twoMinuteAlertMonitor,
                twoMinuteAlertMonitor.getInitialDelayInSeconds(),
                twoMinuteAlertMonitor.getIntervalInSeconds(),
                TimeUnit.SECONDS
        );
    }

    private void tailLogFileForMonitors() {
        ArrayList<HttpAlertMonitor> monitors = new ArrayList<HttpAlertMonitor>();
        monitors.add(tenSecondAlertMonitor);
        monitors.add(twoMinuteAlertMonitor);
        TailListener tailListener = new TailListener(monitors);
        File file = new File(getClass().getResource(logfileLocation).getFile());
        Tailer tailer = new Tailer(file, tailListener, 300);
        tailer.run();
    }
}
