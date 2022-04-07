package org.greenem.plugins.manhunt.versionCompatibility;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.data.References.runFileString;
import static org.greenem.plugins.manhunt.data.References.serverDirectory;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.greenem.plugins.manhunt.config.ConfigOperations;
import org.greenem.plugins.manhunt.data.Names;
import org.greenem.plugins.manhunt.data.References;

public class ConfigCompatibility {
	public static void updateConfig() {
		updateConfigFrom1_3To1_4();
		// other later
	}
	
	@SuppressWarnings("unchecked")
	private static void updateConfigFrom1_3To1_4() {
		File f = getConfigFile();
		YamlConfiguration c = null;
		try {
			c = getConfigYaml(f);
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			log(ChatColor.RED + "Cannot update the config - not found");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		YamlConfiguration cOld = null;
		try {
			cOld = getConfigYaml(getConfigFile());
		} catch (IOException e) {}
		
		String settingsNameWDot = "settings.";
		String currentKey = null;
		try {
			currentKey = settingsNameWDot;
			/*Map<String, Object> settings = (HashMap<String, Object>) c.get(currentKey);
			if(settings==null) {
				settings = new HashMap<>();
			}*/
			
			currentKey = "launch-server-script-name";
			String currentValue = null;
			try{
				currentValue = (String) c.get(currentKey);
			} catch (ClassCastException e) {
				c.set(settingsNameWDot + currentKey, null);
			}
			if (currentValue != null) {
				settings.put(currentKey, currentValue);
				c.set(settingsNameWDot + currentKey, null);
			} else {
				settings.put(currentKey, currentValue);
			}
			
			c.set("settings", settings);
		} catch (Throwable t) {
			log("Error while updating \"" + currentKey + "\" from the config");
		}
		
		ConfigOperations.saveConfigIfHasChanges(c, cOld, f);
	}
	
	private static File getConfigFile() {
		return new File(References.pluginConfigDirectory, Names.mainConfigName);
	}
	
	private static YamlConfiguration getConfigYaml(File f) throws FileNotFoundException, IOException {
		YamlConfiguration c = new YamlConfiguration();
		try {
			c.load(f);
		} catch (InvalidConfigurationException e) {
			log(ChatColor.RED + "The config file is invalid! Make sure it's edited the right way");
//			e.printStackTrace();
		}
		return c;
	}
}
