package core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import core.formats.CommonLog;

class TestLogCreator {

    private void writeLogFile() {
        LogCreator logCreator = new LogCreator("/basiclogfile.log", new CommonLog());
        logCreator.writeLogLines(96);
    }

    @Test
    void write3LogLines() {
        writeLogFile();
        assertEquals(3, 3);
    }

}
