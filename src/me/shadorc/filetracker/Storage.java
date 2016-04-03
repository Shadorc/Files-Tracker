package me.shadorc.filetracker;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Storage {

	private static Properties prop = new Properties();

	public enum Data {
		CREATED_TIME_DAY,
		MODIFIED_TIME_DAY,
		SHOW_CREATED,
		SHOW_MODIFIED,
		SHOW_ALL;
	}

	public static void store(Data data, Object value) {
		OutputStream output = null;

		try {
			output = new FileOutputStream("config.properties");

			prop.setProperty(data.toString(), value.toString());
			prop.store(output, null);

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

	public static String getData(Data data) {
		InputStream input = null;

		try {
			input = new FileInputStream("config.properties");
			prop.load(input);

			return prop.getProperty(data.toString());

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
