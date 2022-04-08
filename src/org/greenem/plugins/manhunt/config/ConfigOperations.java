package org.greenem.plugins.manhunt.config;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.manhunt.data.References.serverDirectory;
import static org.greenem.plugins.manhunt.data.References.pluginConfigDirectory;
import static org.greenem.plugins.manhunt.data.References.runFileString;
import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.greenem.plugins.manhunt.data.Names;
import org.greenem.plugins.manhunt.versionCompatibility.ConfigCompatibility;

public class ConfigOperations {
	public static void updateAndLoadConfig() {
		ConfigCompatibility.updateConfig();
		loadConfig();
	}
	
	public static void loadConfig() {
		File f = new File(pluginConfigDirectory, Names.mainConfigName);
		f.getParentFile().mkdirs();
		
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		String currentKey = null;
		Object currentValue = null;
		Object def = null;
		try {
			String noExistingStartFileWarning = ChatColor.RED + "You haven't specified your run script file name in the config! (for world reseting, but other things are fine)";
			String settingsNameWDot = "settings.";
			
			currentKey = settingsNameWDot + "launch-server-script-name";
			runFileString = (String) loadKey(cOld, currentKey, () -> {
				if(!new File(serverDirectory, runFileString).exists()) {
					log(noExistingStartFileWarning);
				}
			}, () -> {
				if(!new File(serverDirectory, runFileString).exists()) {
					log(noExistingStartFileWarning);
				}
			});
			
			//currentKey = "java-path-to-start-java-middle-script"; //TODO?
			currentKey = settingsNameWDot + "allow-gm-command-for-ops";
			if (c.get(currentKey) == null) {
				def = getDefaultValue(currentKey);
				c.set(currentKey, def);
				debug("set");
			} else {
				try {
					ConfigData.allowGmCommandForOps = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			debug(currentKey);
			debug(def);
			debug(ConfigData.allowGmCommandForOps);
			currentKey = settingsNameWDot + "allow-heal-command-for-ops";
			if (c.get(currentKey) == null) {
				def = getDefaultValue(currentKey);
				c.set(currentKey, def);
			} else {
				try {
					ConfigData.allowHealCommandForOps = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = settingsNameWDot + "require-confirmation-for-world-resetting"; // TODO
			// TODO: HALT OR NOT
		} catch (Throwable t) {
			log("Error while reading \"" + currentKey + "\" from the config");
		}
		
		// DONE
		
		saveConfigIfHasChanges(c, cOld, f);
	}
	
	public static void logCastEx(String currentKey) {
		log("Wrong value type for \"" + currentKey + "\" in the config");
	}
	
	private static Object loadKey(YamlConfiguration c, String currentKey, Runnable ifExists, Runnable ifNotExists) {
		Object currentValue = c.get(currentKey);
		Object def = null;
		Object returnValue = null;
		if (currentValue == null) {
			def = (String) getDefaultValue(currentKey);
			c.set(currentKey, def);
			returnValue = (String) def;
			if(ifExists!=null) {
				ifExists.run();
			}
		} else {
			try {
				returnValue = (String) currentValue;
				if(ifNotExists!=null) {
					ifNotExists.run();
				}
			} catch (ClassCastException e) {
				logCastEx(currentKey);
			}
		}
		return returnValue;
	}
	
	/***
	 * Returnes true if found changes
	 * **/
	public static boolean saveConfigIfHasChanges(YamlConfiguration newConfig, YamlConfiguration oldConfig, File f) {
		boolean same = true;
	    for(String key : newConfig.getKeys(false)) { // missing some keys in oldConfig, think about it later (alrady not)
	        if(!oldConfig.contains(key)) {
	        	same = false;
	        }
	    }
	    for(String key : oldConfig.getKeys(false)) {
	        if(!newConfig.contains(key)) {
	        	same = false;
	        }
	    }
	    
		if (!same) {
			try {
				newConfig.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return !same;
	}
	
	public static Object getDefaultValue(String key) {
		switch (key) {
		case "settings.launch-server-script-name": return "Run.bat";
		case "settings.allow-gm-command-for-ops": return true;
		case "settings.allow-heal-command-for-ops": return true;
		case "settings.require-confirmation-for-world-resetting": return false;
		default:
			return "UNDEFINED_CONFIG_VALUE";
		}
	}
}
