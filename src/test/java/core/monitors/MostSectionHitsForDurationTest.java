package core.monitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import core.AlertModule;
import core.HttpEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MostSectionHitsForDurationTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final HttpEvent httpEvent = mock(HttpEvent.class);

    public MostSectionHitsForDuration getMonitor() {
        Injector injector = Guice.createInjector(new AlertModule());
        return injector.getInstance(MostSectionHitsForDuration.class);
    }

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(System.out);
    }

    private void assertNoActivity() {
        String expected = "No activity in 10 seconds" + System.getProperty("line.separator");
        assertEquals(expected, outContent.toString());
    }

    private void assertHitMessage(int interval, int totalHits, int uniqueSections, String section, int sectionHits, String percent) {
        assertEquals(generateHitMessage(interval, totalHits, uniqueSections, section, sectionHits, percent) + System.getProperty("line.separator"), outContent.toString());
        outContent.reset();
    }
    private String generateHitMessage(int interval, int totalHits, int uniqueSections, String section, int sectionHits, String percent) {
        return String.format("Report for %d second duration: %d total hits across %d unique sections. section '%s' had most hits with %d : (%s).",
                interval,
                totalHits,
                uniqueSections,
                section,
                sectionHits,
                percent
        );
    }

    @Test
    public void testNoActivity() {
        MostSectionHitsForDuration monitor = getMonitor();
        monitor.run();
        assertNoActivity();
    }

    @Test
    public void testSingleHit() {
        MostSectionHitsForDuration monitor = getMonitor();
        when(httpEvent.getSection()).thenReturn("/test");
        monitor.processEvent(httpEvent);

        monitor.run();

        assertHitMessage(10, 1, 1, "/test", 1, "100.00%");
    }

    @Test
    public void testRunClearsHits() {
        MostSectionHitsForDuration monitor = getMonitor();
        when(httpEvent.getSection()).thenReturn("/test");
        monitor.processEvent(httpEvent);

        monitor.run();

        assertHitMessage(10, 1, 1,"/test", 1, "100.00%");

        monitor.run();
        assertNoActivity();
    }

    @Test
    public void testTwoSingleHits() {
        MostSectionHitsForDuration monitor = getMonitor();
        when(httpEvent.getSection()).thenReturn("/first-test");
        monitor.processEvent(httpEvent);
        when(httpEvent.getSection()).thenReturn("/second-test");
        monitor.processEvent(httpEvent);

        monitor.run();

        assertHitMessage(10, 2, 2, "/first-test", 1, "50.00%");
    }

    @Test
    public void testPassTheFirst() {
        MostSectionHitsForDuration monitor = getMonitor();
        when(httpEvent.getSection()).thenReturn("/first-test");
        monitor.processEvent(httpEvent);
        when(httpEvent.getSection()).thenReturn("/second-test");
        monitor.processEvent(httpEvent);
        when(httpEvent.getSection()).thenReturn("/second-test");
        monitor.processEvent(httpEvent);

        monitor.run();

        assertHitMessage(10, 3, 2, "/second-test", 2, "66.67%");
    }
}
