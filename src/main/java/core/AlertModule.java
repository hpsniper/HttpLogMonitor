package core;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import core.formats.CommonLog;
import core.formats.LogFormat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AlertModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(getClass().getResource("/config.properties").getFile()));
            Names.bindProperties(binder(), properties);
        } catch (IOException e) {
            System.out.println("ERROR: Could not load properties");
            throw new RuntimeException(e);
        }

        bind(LogFormat.class).to(CommonLog.class);
    }
}
