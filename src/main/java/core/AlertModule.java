package core;

import com.google.inject.AbstractModule;
import core.formats.CommonLog;
import core.formats.LogFormat;

public class AlertModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LogFormat.class).to(CommonLog.class);
    }
}
