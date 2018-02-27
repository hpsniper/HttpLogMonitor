package core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpAlertMonitorTest {

    private HttpAlertMonitor getHttpAlertMonitor() {
        Injector injector = Guice.createInjector(new AlertModule());
        return injector.getInstance(HttpAlertMonitor.class);
    }

    @Test
    void testLogFormat() {
        HttpAlertMonitor httpAlertMonitor = getHttpAlertMonitor();
        assertEquals("%h %l %u %t \"%r\" %>s %b", httpAlertMonitor.getLogFormat());
    }
}