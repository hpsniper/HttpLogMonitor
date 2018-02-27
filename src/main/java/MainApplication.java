import core.HttpAlertMonitor;
import core.TailListener;
import core.TenSecondAlertMonitor;
import org.apache.commons.io.input.Tailer;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainApplication {
    private final Integer poolSize;
    private final TenSecondAlertMonitor tenSecondAlertMonitor;

    public MainApplication(Integer poolSize, TenSecondAlertMonitor tenSecondAlertMonitor) {
        this.poolSize = poolSize;
        this.tenSecondAlertMonitor = tenSecondAlertMonitor;
    }

    public static void main(String[] args) {
        MainApplication app = new MainApplication(1, new TenSecondAlertMonitor());
        app.run();
    }

    private void run() {
        setupMonitors();
        tailLogFile();
    }

    private void setupMonitors() {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(poolSize);
        executor.scheduleAtFixedRate(
                tenSecondAlertMonitor,
                tenSecondAlertMonitor.getInitialDelayInSeconds(),
                tenSecondAlertMonitor.getIntervalInSeconds(),
                TimeUnit.SECONDS
        );
    }

    private void tailLogFile() {
        ArrayList<HttpAlertMonitor> monitors = new ArrayList<HttpAlertMonitor>();
        monitors.add(tenSecondAlertMonitor);
        TailListener tailListener = new TailListener(monitors);
        File file = new File("/home/mdecosta/IdeaProjects/HttpLogMonitor/src/main/resources/basiclogfile.log");
        Tailer tailer = new Tailer(file, tailListener, 300);
        tailer.run();
    }
}
