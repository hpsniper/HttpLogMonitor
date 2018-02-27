package core;

import com.google.inject.Inject;
import nl.basjes.parse.core.Parser;
import nl.basjes.parse.httpdlog.HttpdLoglineParser;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.ArrayList;
import java.util.Optional;

public class TailListener extends TailerListenerAdapter {
    private ArrayList<HttpAlertMonitor> monitors;

    @Inject
    public TailListener(ArrayList<HttpAlertMonitor> monitors) {
        this.monitors = monitors;
    }

    public void handle(String line) {
        String logformat = "%h %l %u %t \"%r\" %>s %b";
        Parser<HttpEvent> parser = new HttpdLoglineParser<HttpEvent>(HttpEvent.class, logformat);
        Optional<HttpEvent> maybeHttpEvent = Optional.empty();
        try {
            maybeHttpEvent = Optional.of(parser.parse(line));
        } catch (Exception e) {
            System.out.println("EXCEPTION " + e.getMessage());
        }

        if(maybeHttpEvent.isPresent()) {
            for(HttpAlertMonitor monitor : monitors) {
                monitor.processEvent(maybeHttpEvent.get());
            }
        }
    }
}
