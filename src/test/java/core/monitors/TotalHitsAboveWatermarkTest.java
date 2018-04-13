package core.monitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.google.inject.Guice;
import com.google.inject.Injector;
import core.AlertModule;
import core.HttpEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TotalHitsAboveWatermarkTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final HttpEvent httpEvent = mock(HttpEvent.class);

    public TotalHitsAboveWatermark getMonitor() {
        Injector injector = Guice.createInjector(new AlertModule());
        return injector.getInstance(TotalHitsAboveWatermark.class);
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(System.out);
    }

    private void assertEmptyOutContent() {
        assertEquals("", outContent.toString());
    }

    private void assertAlertMessage(int windowSize, int totalHitsInWindow, double averageRate) {
        assertTrue(outContent.toString().contains(generateAlertMessage(windowSize, totalHitsInWindow, averageRate)), "actual => '" + outContent.toString() + "'");
        outContent.reset();
    }
    private String generateAlertMessage(int windowSize, int totalHitsInWindow, double averageRate) {
        return String.format("High traffic alert triggered - %s - triggered at ", generateHitRateOutput(windowSize, totalHitsInWindow, averageRate));
    }

    private void assertRecoveryMessage(int windowSize, int totalHitsInWindow, double averageRate) {
        assertTrue(outContent.toString().contains(generateRecoveryMessage(windowSize, totalHitsInWindow, averageRate)), "actual => '" + outContent.toString() + "'");
        outContent.reset();
    }

    private String generateRecoveryMessage(int windowSize, int totalHitsInWindow, double averageRate) {
        return String.format("High traffic alert recovered - %s - recovered at ", generateHitRateOutput(windowSize, totalHitsInWindow, averageRate));
    }

    private String generateHitRateOutput(int windowSize, int totalHitsInWindow, double averageRate) {
        return String.format("total hits in %d second window: %d, average hit rate: %.2f", windowSize, totalHitsInWindow, averageRate);
    }

    @Test
    public void testJustShyOfThresholdNoAlert() {
        TotalHitsAboveWatermark monitor = getMonitor();
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.run();
        assertEmptyOutContent();
    }

    @Test
    public void testJustMeetsThresholdAlert() {
        TotalHitsAboveWatermark monitor = getMonitor();
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.run();
        assertAlertMessage(1, 3, 3d );
    }

    @Test
    public void testJustMeetsThresholdRecoversAfterOneSecond() {
        TotalHitsAboveWatermark monitor = getMonitor();
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.run();
        assertAlertMessage(1, 3, 3d );
        monitor.run();
        assertRecoveryMessage(2, 3, 1.5d);
    }

    @Test
    public void testDoubleThresholdRecoversAfterTwoSeconds() {
        TotalHitsAboveWatermark monitor = getMonitor();
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.run();
        assertAlertMessage(1, 6, 6d );
        monitor.run();
        assertEquals("", outContent.toString());
        monitor.run();
        assertRecoveryMessage(3, 6, 2d);
    }

    @Test
    public void testBounceBackToAlertAfterRecovery() {
        TotalHitsAboveWatermark monitor = getMonitor();
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.run();
        assertAlertMessage(1, 6, 6d );

        monitor.run();
        assertEquals("", outContent.toString());

        monitor.run();
        assertRecoveryMessage(3, 6, 2d);

        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.run();
        assertAlertMessage(4, 12, 3d );
    }

    @Test
    public void testFourEventsASecondForTwoMinutesRecoversAfterThirtyOneSeconds() {
        TotalHitsAboveWatermark monitor = getMonitor();

        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.processEvent(httpEvent);
        monitor.run(); // 1
        assertAlertMessage(1, 4, 4d);
        for(int i=1;i<120;i++) {
            monitor.processEvent(httpEvent);
            monitor.processEvent(httpEvent);
            monitor.processEvent(httpEvent);
            monitor.processEvent(httpEvent);
            monitor.run();
        }
        // 120

        for(int i=0;i<30;i++) {
            monitor.run();
        }
        assertEmptyOutContent();

        monitor.run();
        assertRecoveryMessage(120, 356, 2.966667d);
    }
}
