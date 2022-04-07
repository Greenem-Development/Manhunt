package org.greenem.plugins.manhunt.util;

import org.bukkit.Bukkit;

public class SimpleLogger {
	public static String pluginName = "AdvancedManhunt";
	public static String color = ""; // + ChatColor.WHITE;
	private static boolean debugOutputEnabled = true;
	
	public static void log(Object o) {
		if(o!=null) {
			log(o.toString());
		} else {
			log("null");
		}
	}
	
	public static void log(String s) {
		s = color + "[" + pluginName + "] " + s;
		Bukkit.getConsoleSender().sendMessage(s);
	}
	
	public static void log(int s) {
		log(s + "");
	}
	
	public static void log(boolean s) {
		log(s + "");
	}
	
	public static void log(double s) {
		log(s + "");
	}
	
	public static void log(float s) {
		log(s + "");
	}
	
	public static void log(char s) {
		log(s + "");
	}
	
	public static void log(short s) {
		log(s + "");
	}
	
	public static void debug(Object o) {
		if(o!=null) {
			debug(o.toString());
		} else {
			debug("null");
		}
	}
	
	public static void debug(String s) {
		if(!debugOutputEnabled) return;
		s = color + "[" + pluginName + " DEBUG] " + s;
		Bukkit.getConsoleSender().sendMessage(s);
	}
	
	public static void debug(int s) {
		debug(s + "");
	}
	
	public static void debug(boolean s) {
		debug(s + "");
	}
	
	public static void debug(double s) {
		debug(s + "");
	}
	
	public static void debug(float s) {
		debug(s + "");
	}
	
	public static void debug(char s) {
		debug(s + "");
	}
	
	public static void debug(short s) {
		debug(s + "");
	}
}
