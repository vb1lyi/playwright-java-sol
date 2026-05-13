package com.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final ConfigManager INSTANCE = new ConfigManager();
    private final Properties props = new Properties();

    private ConfigManager() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static ConfigManager get() {
        return INSTANCE;
    }

    public String baseUrl() {
        return resolve("base.url");
    }

    public String apiBaseUrl() {
        return resolve("api.base.url");
    }

    public String browser() {
        return resolve("browser");
    }

    public boolean headless() {
        return Boolean.parseBoolean(resolve("headless"));
    }

    // System property overrides config.properties, e.g. -Dbase.url=https://staging.example.com
    private String resolve(String key) {
        return System.getProperty(key, props.getProperty(key));
    }
}
