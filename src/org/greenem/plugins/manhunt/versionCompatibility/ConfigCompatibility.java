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
import org.greenem.plugins.manhunt.data.Lines;
import org.greenem.plugins.manhunt.data.References;

public class ConfigCompatibility {
	public static void updateConfig() {
		File f = getConfigFile();
		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		
		String currentKey = null;
		try {
			updateConfigFrom1_3To1_4(c, currentKey);
		} catch (Throwable t) {
			log("Error while updating \"" + currentKey + "\" from the config");
		}
		ConfigOperations.saveConfigIfHasChanges(c, cOld, f);
	}
	
	private static void updateConfigFrom1_3To1_4(YamlConfiguration c, String currentKey) {
		String newPrefixString = "settings.";
		currentKey = "launch-server-script-name";
		moveValue(c, currentKey, newPrefixString + "world-resetting." + currentKey);
		currentKey = "allow-gm-command-for-ops";
		moveValue(c, currentKey, newPrefixString + ".commands." + currentKey);
		currentKey = "allow-heal-command-for-ops";
		moveValue(c, currentKey, newPrefixString + ".commands." + currentKey);
	}
	
	private static File getConfigFile() {
		return new File(References.pluginConfigDirectory, Lines.mainConfigName);
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
		if(c.get(newKey)==null && c.get(oldKey)!=null) {
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
