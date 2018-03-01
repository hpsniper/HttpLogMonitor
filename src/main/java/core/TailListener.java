package core;

import com.google.inject.Inject;
import core.formats.LogFormat;
import core.monitors.HttpAlertMonitor;
import nl.basjes.parse.core.Parser;
import nl.basjes.parse.httpdlog.HttpdLoglineParser;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.ArrayList;
import java.util.Optional;

public class TailListener extends TailerListenerAdapter {
    private LogFormat logFormat;
    private ArrayList<HttpAlertMonitor> monitors;

    @Inject
    public TailListener(LogFormat logFormat) {
        this.logFormat = logFormat;
    }

    public void setMonitors(ArrayList<HttpAlertMonitor> alertmonitors) {
        monitors = alertmonitors;
    }

    public void handle(String line) {
        Parser<HttpEvent> parser = new HttpdLoglineParser<HttpEvent>(HttpEvent.class, logFormat.getFormat());
        Optional<HttpEvent> maybeHttpEvent;
        try {
            maybeHttpEvent = Optional.of(parser.parse(line));
        } catch (Exception e) {
            System.out.println("EXCEPTION " + e.getMessage());
            throw new RuntimeException(e);
        }

        if(maybeHttpEvent.isPresent()) {
            for(HttpAlertMonitor monitor : monitors) {
                monitor.processEvent(maybeHttpEvent.get());
            }
        }
    }
}
