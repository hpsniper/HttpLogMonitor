package core.formats;

public class CommonLog extends LogFormat {
    private String format = "%h %l %u %t \"%r\" %>s %b";

    @Override
    public String getFormat() {
        return format;
    }

}
