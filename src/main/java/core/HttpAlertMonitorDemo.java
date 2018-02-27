package core;

import com.google.inject.Inject;
import core.formats.LogFormat;
import nl.basjes.parse.core.Parser;
import nl.basjes.parse.httpdlog.HttpdLoglineParser;

import java.util.List;

public class HttpAlertMonitorDemo {
    private final LogFormat logFormat;

    @Inject
    public HttpAlertMonitorDemo(final LogFormat logFormat) {
        this.logFormat = logFormat;
    }

    public String getLogFormat() {
        return logFormat.getFormat();
    }


    public static void printPossiblePaths(String logformat) {
        Parser<Object> dummyParser = new HttpdLoglineParser<Object>(Object.class, logformat);
        List<String> possiblePaths = dummyParser.getPossiblePaths();
        for (String path : possiblePaths) {
            System.out.println(path);
        }

    }

    public static void main(String[] args) {
        String logformat = "%h %l %u %t \"%r\" %>s %b";
        String logline =  "127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] \"GET /apache_pb.gif HTTP/1.0\" 200 2326";
        String logline2 =  "127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] \"GET /pages/create HTTP/1.0\" 200 2326";
        String logline3 =  "127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] \"GET / HTTP/1.0\" 200 2326";
        Parser<HttpEvent> parser = new HttpdLoglineParser<HttpEvent>(HttpEvent.class, logformat);
        try {
            HttpEvent httpEvent = parser.parse(logline2);
            System.out.println(httpEvent.toString());
        } catch (Exception e) {
            System.out.println("EXCEPTION " + e.getMessage());
        }
    }
}
