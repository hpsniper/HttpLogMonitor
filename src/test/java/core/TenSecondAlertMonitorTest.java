package core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TenSecondAlertMonitorTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final HttpEvent httpEvent = mock(HttpEvent.class);

    public TenSecondAlertMonitor getMonitor() {
        Injector injector = Guice.createInjector(new AlertModule());
        return injector.getInstance(TenSecondAlertMonitor.class);
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(System.out);
    }

    @Test
    public void testNoActivity() {
        TenSecondAlertMonitor monitor = getMonitor();
        monitor.run();
        String expected = "No activity in 10 seconds" + System.getProperty("line.separator");
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testSingleHit() {
        TenSecondAlertMonitor monitor = getMonitor();
        when(httpEvent.getSection()).thenReturn("/test");
        monitor.processEvent(httpEvent);

        monitor.run();

        String expected = "Most hits in 10 seconds: section '/test' with '1' hits." + System.getProperty("line.separator");
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testRunClearsHits() {
        TenSecondAlertMonitor monitor = getMonitor();
        when(httpEvent.getSection()).thenReturn("/test");
        monitor.processEvent(httpEvent);

        monitor.run();

        String expected = "Most hits in 10 seconds: section '/test' with '1' hits." + System.getProperty("line.separator");
        assertEquals(expected, outContent.toString());
        outContent.reset();

        monitor.run();
        expected = "No activity in 10 seconds" + System.getProperty("line.separator");
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testTwoSingleHits() {
        TenSecondAlertMonitor monitor = getMonitor();
        when(httpEvent.getSection()).thenReturn("/first-test");
        monitor.processEvent(httpEvent);
        when(httpEvent.getSection()).thenReturn("/second-test");
        monitor.processEvent(httpEvent);

        monitor.run();

        String expected = "Most hits in 10 seconds: section '/first-test' with '1' hits." + System.getProperty("line.separator");
        assertEquals(expected, outContent.toString());
    }

    @Test
    public void testPassTheFirst() {
        TenSecondAlertMonitor monitor = getMonitor();
        when(httpEvent.getSection()).thenReturn("/first-test");
        monitor.processEvent(httpEvent);
        when(httpEvent.getSection()).thenReturn("/second-test");
        monitor.processEvent(httpEvent);
        when(httpEvent.getSection()).thenReturn("/second-test");
        monitor.processEvent(httpEvent);

        monitor.run();

        String expected = "Most hits in 10 seconds: section '/second-test' with '2' hits." + System.getProperty("line.separator");
        assertEquals(expected, outContent.toString());
    }
}
