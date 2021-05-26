package me.shadorc.filetracker;

import java.io.*;
import java.util.Properties;

public class Storage {

    private static final Properties PROPERTIES = new Properties();
    private static final File CONFIG_FILE = new File("config.properties");

    public enum Data {
        CREATED_TIME_DAY,
        MODIFIED_TIME_DAY,
        SHOW_CREATED,
        SHOW_MODIFIED,
        SHOW_SYSTEM_DIR;
    }

    public static void init() throws IOException {
        if (!CONFIG_FILE.exists()) {
            CONFIG_FILE.createNewFile();
            Storage.save(Data.MODIFIED_TIME_DAY, 1);
            Storage.save(Data.CREATED_TIME_DAY, 1);
            Storage.save(Data.SHOW_MODIFIED, true);
            Storage.save(Data.SHOW_CREATED, true);
            Storage.save(Data.SHOW_SYSTEM_DIR, false);
        }
    }

    public static void save(Data data, Object value) {
        OutputStream output = null;

        try {
            output = new FileOutputStream(CONFIG_FILE);

            PROPERTIES.setProperty(data.toString(), value.toString());
            PROPERTIES.store(output, null);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (output != null) output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String get(Data data) {
        InputStream input = null;

        try {
            input = new FileInputStream(CONFIG_FILE);
            PROPERTIES.load(input);

            return PROPERTIES.getProperty(data.toString());

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (input != null) input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
