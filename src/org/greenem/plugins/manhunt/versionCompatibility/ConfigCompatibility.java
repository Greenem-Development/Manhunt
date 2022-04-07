package org.greenem.plugins.manhunt.versionCompatibility;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.data.References.runFileString;
import static org.greenem.plugins.manhunt.data.References.serverDirectory;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.greenem.plugins.manhunt.data.Names;
import org.greenem.plugins.manhunt.data.References;

public class ConfigCompatibility {
	public static void updateConfig() {
		updateConfigFrom1_3To1_4();
		// other later
	}
	
	@SuppressWarnings("unchecked")
	private static void updateConfigFrom1_3To1_4() {
		YamlConfiguration c = null;
		try {
			c = getConfigYaml(getConfigFile());
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
		
		String settingsName = "settings";
		String currentKey = null;
		try {
			currentKey = settingsName;
			List<String> settings = (ArrayList<String>) c.get(currentKey);
			if(settings==null) {
				settings = new ArrayList<>();
			}
			
			currentKey = "launch-server-script-name";
			String currentValue = null;
			try{
				currentValue = (String) c.get(currentKey);
			} catch (ClassCastException e) {}
			if (currentValue != null) {
				boolean alreadyExists = false;
				for (int i = 0; i < settings.size(); i++) {
					if(settings.get(i).equals(settingsName)) {
						settings.set(i, currentValue);
						alreadyExists = true;
						break;
					}
				}
				if(!alreadyExists) {
					settings.add(currentValue);
				}
			}
			
			c.set("settings", settings);
		} catch (Throwable t) {
			log("Error while updating \"" + currentKey + "\" from the config");
		}
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
