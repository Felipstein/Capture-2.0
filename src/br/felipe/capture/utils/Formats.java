package br.felipe.capture.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.bukkit.Location;

import static java.lang.String.valueOf;

public class Formats {
	
	public static String dateFormat(long ms) {
		return dateFormat(ms, "dd/MM/yyyy HH:mm:ss");
	}
	
	public static String dateFormat(long ms, String format) {
		return new SimpleDateFormat(format).format(ms);
	}
	
	public static String decimalFormat(double value) {
		return decimalFormat(value, "###,###.##");
	}
	
	public static String decimalFormat(double value, String format) {
		return new DecimalFormat(format).format(value);
	}
	
	public static String locationToString(Location location) {
		return locationToString(location, true);
	}
	
	public static String locationToString(Location location, boolean withWorld) {
		String x = valueOf(location.getBlockX());
		String y = valueOf(location.getBlockY());
		String z = valueOf(location.getBlockZ());
		return (withWorld ? location.getWorld().getName() + ", " : "") + x + ", " + y + ", " + z;
	}
	
	public static String toJSON(Object o) {
		return "{\"text\":\"" + (o == null ? "" : String.valueOf(o)) + "\"}";
	}
	
}