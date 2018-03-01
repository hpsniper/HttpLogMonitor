package core.monitors;


import core.HttpEvent;

public abstract class HttpAlertMonitor implements Runnable {

    public abstract int getIntervalInSeconds();
    public abstract void processEvent(HttpEvent event);
    public abstract void run();

}
