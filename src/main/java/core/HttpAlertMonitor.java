package core;


public abstract class HttpAlertMonitor implements Runnable {

    public abstract int getIntervalInSeconds();
    public abstract int getInitialDelayInSeconds();
    public abstract void processEvent(HttpEvent event);
    public abstract void run();

}
