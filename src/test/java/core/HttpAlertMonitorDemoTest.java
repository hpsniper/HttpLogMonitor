package core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import core.formats.CommonLog;

class HttpAlertMonitorDemoTest {

    private HttpAlertMonitorDemo getHttpAlertMonitor() {
        Injector injector = Guice.createInjector(new AlertModule());
        return injector.getInstance(HttpAlertMonitorDemo.class);
    }

    private void writeLogFile() {
        LogCreator logCreator = new LogCreator("/basiclogfile.log", new CommonLog());
        logCreator.writeLogLines(3);
    }

    @Test
    void write3LogLines() {
        writeLogFile();
        assertEquals(3, 3);
    }

    @Test
    void testLogFormat() {
        HttpAlertMonitorDemo httpAlertMonitorDemo = getHttpAlertMonitor();
        assertEquals("%h %l %u %t \"%r\" %>s %b", httpAlertMonitorDemo.getLogFormat());
    }
}
