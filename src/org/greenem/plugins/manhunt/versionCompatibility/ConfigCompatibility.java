package org.greenem.plugins.manhunt.versionCompatibility;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.data.References.runFileString;
import static org.greenem.plugins.manhunt.data.References.serverDirectory;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
	
	private static void updateConfigFrom1_3To1_4() {
		File f = getConfigFile();
		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		
		String newPrefixString = "settings.";
		String currentKey = null;
		try {
			currentKey = "launch-server-script-name";
			moveValue(c, currentKey, newPrefixString + "world-resetting." + currentKey);
			currentKey = "allow-gm-command-for-ops";
			moveValue(c, currentKey, newPrefixString + ".commands." + currentKey);
			currentKey = "allow-heal-command-for-ops";
			moveValue(c, currentKey, newPrefixString + ".commands." + currentKey);
		} catch (Throwable t) {
			log("Error while updating \"" + currentKey + "\" from the config");
		}
		ConfigOperations.saveConfigIfHasChanges(c, cOld, f);
	}
	
	private static File getConfigFile() {
		return new File(References.pluginConfigDirectory, Names.mainConfigName);
	}
	
	@Deprecated
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
	
	private static void moveValue(YamlConfiguration c, String oldKey, String newKey) {
		if(c.get(newKey)==null) {
			Object currentValue = null;
			try{
				currentValue = c.get(oldKey);
			} catch (ClassCastException e) {
				
			}
			if (currentValue!=null) {
				c.set(newKey, currentValue);
			} else {
				c.set(newKey, ConfigOperations.getDefaultValue(newKey));
			}
		}
		c.set(oldKey, null);
	}
}
