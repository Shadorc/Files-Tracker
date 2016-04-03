package me.shadorc.filetracker;

import java.awt.Font;
import java.util.Date;

public class Utils {

	public static int daysBetween(Date d1, Date d2){
		return (int) Math.abs((d2.getTime()-d1.getTime())/(1000*60*60*24));
	}

	public static Font getFont() {
		return new Font("Tahoma", Font.PLAIN, 12);
	}

	public static String toReadableByteCount(long bytes) {
		if (bytes < 1000) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(1000));
		char pre = "kMGTPE".charAt(exp-1);
		return String.format("%.1f %sB", bytes / Math.pow(1000, exp), pre);
	}
}
