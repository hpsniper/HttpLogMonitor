package core;

import com.google.inject.Inject;
import core.formats.LogFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LogCreator {

    private Map<String, String[]> LOG_VALUES = new HashMap();
    private final String logFileLocation;
    private final LogFormat logFormat;

    @Inject
    public LogCreator(String logFileLocation, LogFormat logFormat) {
        this.logFileLocation = logFileLocation;
        this.logFormat = logFormat;
    }

    public void writeLogLines(int numLines) {
        while(numLines > 0) {
            try {
                writeLogLine(generateLogLine());
            } catch (IOException e) {
                System.out.println("\nERROR: IOException: "+e.getMessage());
                numLines = 0;
            }
            numLines--;
        }
    }

    private void writeLogLine(String line) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(this.logFileLocation, true);
        byte[] strToBytes = line.getBytes();
        fileOutputStream.write(strToBytes);
        fileOutputStream.close();
    }

    private String generateLogLine() {
        if(LOG_VALUES.size() == 0) {
            fillLogValues();
        }

        ArrayList<String> logline = new ArrayList<String>();
        String[] parts = logFormat.getFormat().split(" ");

        for(String part : parts) {
            if(part.equals("%t")) {
                logline.add(generateTimeString());
            } else {
                logline.add(generateRandom(LOG_VALUES.get(part)));
            }
        }

        return String.join(" ", logline)+"\n";
    }

    private String generateRandom(String[] possibilities) {
        int randomValue = ThreadLocalRandom.current().nextInt(0, possibilities.length);
        return possibilities[randomValue];
    }

    private String generateTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void fillLogValues() {
        LOG_VALUES.put("%h", getHosts());
        LOG_VALUES.put("%l", getIdentifiers());
        LOG_VALUES.put("%u", getUsers());
        LOG_VALUES.put("\"%r\"", getRequests());
        LOG_VALUES.put("%>s", getStatuses());
        LOG_VALUES.put("%b", getResponseSizes());
    }

    private String[] getHosts() {
        return new String[]{"127.0.0.1","115.36.149.56","193.56.115.194"};
    }

    private String[] getIdentifiers() {
        return new String[]{"-"};
    }

    private String[] getUsers() {
        return new String[]{"frank", "jim", "lisa", "melissa"};
    }

    private String[] getRequests() {
        return new String[]{
                "\"GET / HTTP/1.0\"",
                "\"GET /index.html HTTP/1.0\"",
                "\"GET /apache_pb.gif HTTP/1.0\"",
                "\"GET /resource/new\" HTTP/1.0",
                "\"GET /pages/2756 HTTP/1.0\"",
                "\"GET /pages/2756/sub_section HTTP/1.0\"",
                "\"POST /pages/update/2756 HTTP/1.0\"",
                "\"POST /pages/delete/2756 HTTP/1.0\""
        };
    }

    private String[] getStatuses() {
        return new String[]{"200","201","404"};
    }

    private String[] getResponseSizes() {
        return new String[]{"-", "21", "376", "4825", "59999"};
    }

}
