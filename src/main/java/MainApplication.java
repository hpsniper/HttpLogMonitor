import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import core.TwoMinuteAlertMonitor;
import org.apache.commons.io.input.Tailer;

import core.HttpAlertMonitor;
import core.TailListener;
import core.TenSecondAlertMonitor;

public class MainApplication {
    private final Integer poolSize;
    private final TenSecondAlertMonitor tenSecondAlertMonitor;
    private final TwoMinuteAlertMonitor twoMinuteAlertMonitor;

    public MainApplication(Integer poolSize, TenSecondAlertMonitor tenSecondAlertMonitor, TwoMinuteAlertMonitor twoMinuteAlertMonitor) {
        this.poolSize = poolSize;
        this.tenSecondAlertMonitor = tenSecondAlertMonitor;
        this.twoMinuteAlertMonitor = twoMinuteAlertMonitor;
    }

    public static void main(String[] args) {
        MainApplication app = new MainApplication(1, new TenSecondAlertMonitor(), new TwoMinuteAlertMonitor());
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
        )
        ;
        executor = new ScheduledThreadPoolExecutor(poolSize);
        executor.scheduleAtFixedRate(
                twoMinuteAlertMonitor,
                twoMinuteAlertMonitor.getInitialDelayInSeconds(),
                twoMinuteAlertMonitor.getIntervalInSeconds(),
                TimeUnit.SECONDS
        );
    }

    private void tailLogFile() {
        ArrayList<HttpAlertMonitor> monitors = new ArrayList<HttpAlertMonitor>();
        monitors.add(tenSecondAlertMonitor);
        monitors.add(twoMinuteAlertMonitor);
        TailListener tailListener = new TailListener(monitors);
        File file = new File(getClass().getResource("/basiclogfile.log").getFile());
        Tailer tailer = new Tailer(file, tailListener, 300);
        tailer.run();
    }
}
