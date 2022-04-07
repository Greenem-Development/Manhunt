package org.greenem.plugins.manhunt.data.config;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.manhunt.data.References.serverDirectory;
import static org.greenem.plugins.manhunt.data.References.mainConfigDirectory;
import static org.greenem.plugins.manhunt.data.References.runFileString;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigOperations {
	public static void loadConfig() {
		File f = new File(mainConfigDirectory, "mainconfig.yml");
		f.getParentFile().mkdirs();
		
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		String currentKey = null;

		try {
			String noExistingStartFileWarning = ChatColor.RED + "You haven't specified your run script file name in the config! (for world reseting, but other things are fine)";
			currentKey = "launch-server-script-name";
			if (c.get(currentKey) == null) {
				c.set(currentKey, "Run.bat");
				runFileString = "Run.bat";
				if(!new File(serverDirectory, runFileString).exists()) {
					log(noExistingStartFileWarning);
				}
			} else {
				try {
					runFileString = (String) c.get(currentKey);
					if(!new File(serverDirectory, runFileString).exists()) {
						log(noExistingStartFileWarning);
					}
				} catch (ClassCastException e) {
					
				}
			}
			/*currentKey = "java-path-to-start-java-middle-script"; //TODO?
			if (c.get(currentKey) == null) {
				c.set(currentKey, "java");
			} else {
				try {
					javaString = (String) c.get(currentKey);
				} catch (ClassCastException e) {

				}
			}*/
			currentKey = "allow-gm-command-for-ops";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				try {
					ConfigData.allowGmCommandForOps = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = "allow-heal-command-for-ops";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				try {
					ConfigData.allowHealCommandForOps = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = "require-confirmation-for-world-resetting"; // TODO
			/*if (c.get(currentKey) == null) {
				c.set(currentKey, false);
			} else {
				try {
					ConfigData.requireConfirmationForWorldResetting = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}*/
			// TODO: HALT OR NOT
		} catch (Throwable t) {
			log("Error while reading \"" + currentKey + "\" from the config");
		}
		
		// DONE
		
		boolean same = true;
	    for(String key : c.getKeys(false)) {
	        if(!cOld.contains(key)) {
	        	same = false;
	        }
	    }
	    
		if (!same) {
			try {
				c.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void logCastEx(String currentKey) {
		log("Wrong value type for \"" + currentKey + "\" in the config");
	}
}
