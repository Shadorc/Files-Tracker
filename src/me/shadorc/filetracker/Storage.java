package me.shadorc.filetracker;

import java.io.*;
import java.time.Duration;
import java.util.Properties;

public class Storage {

    private static final Properties PROPERTIES = new Properties();
    private static final File CONFIG_FILE = new File("config.properties");

    public enum Data {
        CREATED_TIME_DAY(1),
        MODIFIED_TIME_DAY(1),
        SHOW_CREATED(true),
        SHOW_MODIFIED(true),
        SHOW_SYSTEM_DIR(false);

        private final Object defaultValue;

        Data(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static <T> void save(Data data, T value) {
        try {
            CONFIG_FILE.createNewFile();
        } catch (IOException err) {
            err.printStackTrace();
        }

        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            PROPERTIES.setProperty(data.toString(), value.toString());
            PROPERTIES.store(out, null);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private static String get(Data data) {
        if (!CONFIG_FILE.exists()) {
            return data.getDefaultValue().toString();
        }

        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            PROPERTIES.load(input);
            return PROPERTIES.getProperty(data.toString(), data.getDefaultValue().toString());
        } catch (IOException err) {
            err.printStackTrace();
        }

        return data.getDefaultValue().toString();
    }

    public static Duration getDuration(Data data) {
        return Duration.ofDays(Long.parseLong(Storage.get(data)));
    }

    public static boolean getBool(Data data) {
        return Boolean.parseBoolean(Storage.get(data));
    }
}
