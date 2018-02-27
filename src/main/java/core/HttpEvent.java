package core;

import nl.basjes.parse.core.Field;

public class HttpEvent {
    public String ip;
    public String user;
    public String path;
    public String date_utc;
    public String time_utc;

    @Field("IP:connection.client.host")
    public void setIP(final String value) {
        ip = value;
    }

    @Field("STRING:connection.client.user")
    public void setUser(final String value) {
        user = value;
    }

    @Field("HTTP.PATH:request.firstline.uri.path")
    public void setPath(final String value) {
        path = value;
    }

    @Field("TIME.Date:request.receive.time.last.date_utc")
    public void setDate(final String value) {
        date_utc = value;
    }

    @Field("TIME.Time:request.receive.time.last.time_utc")
    public void setTime(final String value) {
        time_utc = value;
    }

    public String getSection() {
        // java's little nuance to get the second index of a character
        int endIndex = path.indexOf("/", path.indexOf("/") + 1);
        // endIndex = -1 when a second index doesn't exist
        return endIndex <= -1 ? path : path.substring(0, endIndex);
    }

   @Override
   public String toString() {
       String result = "HttpEvent:";
       result += "\nIP:connection.client.host \t\t\t\t\t\t=> ip \t\t\t=> " + ip;
       result += "\nSTRING:connection.client.user \t\t\t\t\t=> user \t\t=> " + user;
       result += "\nHTTP.PATH:request.referer.path \t\t\t\t\t=> path \t\t=> " + path;
       result += "\n\t\t\t\t\t\t\t\t\t\t\t\t=> section \t\t=> " + getSection();
       result += "\nTIME.Date:request.receive.time.last.date_utc \t=> date_utc \t=> " + date_utc;
       result += "\nTIME.Time:request.receive.time.last.time_utc \t=> time_utc \t=> " + time_utc;
       return result;
   }

}
