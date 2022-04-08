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
		/*YamlConfiguration c = null;
		try {
			c = getConfigYaml(f);
		} catch (FileNotFoundException e) {
			log(ChatColor.RED + "Cannot update the config - file not found");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		YamlConfiguration cOld = null;
		try {
			cOld = getConfigYaml(getConfigFile());
		} catch (IOException e) {}*/
		f.getParentFile().mkdirs();
		
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		
		String settingsString = "settings.";
		String currentKey = null;
		try {
			currentKey = "launch-server-script-name";
			c = updateValue(c, currentKey, settingsString + currentKey);
			currentKey = "allow-gm-command-for-ops";
			c = updateValue(c, currentKey, settingsString + currentKey);
			currentKey = "allow-heal-command-for-ops";
			c = updateValue(c, currentKey, settingsString + currentKey);
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
	
	private static YamlConfiguration updateValue(YamlConfiguration c, String oldKey, String newKey) {
		String currentValue = null;
		try{
			currentValue = (String) c.get(oldKey);
		} catch (ClassCastException e) {
//			c.set(extendedKey, null);
		}
		if (currentValue != null) {
			c.set(newKey, currentValue);
		} else {
			c.set(newKey, ConfigOperations.getDefaultValue(newKey));
		}
		return c;
	}
}
