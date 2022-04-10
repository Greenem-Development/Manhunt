package org.greenem.plugins.manhunt.config;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.manhunt.data.References.serverDirectory;
import static org.greenem.plugins.manhunt.data.References.pluginConfigDirectory;
import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.greenem.plugins.manhunt.data.Lines;
import org.greenem.plugins.manhunt.data.References;
import org.greenem.plugins.manhunt.versionCompatibility.ConfigCompatibility;

public class ConfigOperations {
	public static void updateAndLoadConfig() {
		ConfigCompatibility.updateConfig();
		loadConfig();
	}
	
	public static void loadConfig() {
		File f = new File(pluginConfigDirectory, Lines.mainConfigName);
		f.getParentFile().mkdirs();
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		String currentKey = null;
		try {
			currentKey = "settings.warnings.disable-no-existing-start-file-warning"; // TESTED
			ConfigData.disableNoExistingStartFileWarning = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.world-resetting.launch-server-script-name";
			References.runFileString = (String) loadKey(c, currentKey, () -> { checkStartScript(); }, () -> { checkStartScript(); });
			currentKey = "settings.commands.allow-gm-command-for-ops"; // TODO
			ConfigData.allowGmCommandForOps = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.commands.allow-heal-command-for-ops"; // TODO
			ConfigData.allowHealCommandForOps = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.world-resetting.allow-ops-use-world-resetting";
			ConfigData.allowWorldResetting = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.world-resetting.require-confirmation-for-world-resetting"; // TODO
			ConfigData.requireConfirmationForWorldResetting = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.world-resetting.use-halt-for-instant-server-stopping"; // TODO
			ConfigData.useHaltForWorldResettings = (boolean) loadKey(c, currentKey, null, null);
			currentKey = "settings.game.delay-for-speedrunner-on-start-in-milliseconds";
			ConfigData.speedrunnerDelayMilliseconds = (int) loadKey(c, currentKey, null, null);
			currentKey = "settings.game.clear-items-on-start";
			ConfigData.clearItemsOnStart = (boolean) loadKey(c, currentKey, null, null);
			
			//"java-path-to-start-java-middle-script" // maybe later
		} catch (Throwable t) {
			log("Error while reading \"" + currentKey + "\" from the config");
		}
		saveConfigIfHasChanges(c, cOld, f);
	}
	
	public static Object getDefaultValue(String key) {
		switch (key) {
			case "settings.world-resetting.launch-server-script-name": return "Run.bat";
			case "settings.commands.allow-gm-command-for-ops": return true;
			case "settings.commands.allow-heal-command-for-ops": return true;
			case "settings.world-resetting.require-confirmation-for-world-resetting": return false;
			case "settings.world-resetting.use-halt-for-instant-server-stopping": return false;
			case "settings.game.delay-for-speedrunner-on-start-in-milliseconds": return 2000;
			case "settings.warnings.disable-no-existing-start-file-warning": return false;
			case "settings.game.clear-items-on-start": return true;
			case "settings.world-resetting.allow-ops-use-world-resetting": return true;
			default:
				return "UNDEFINED_CONFIG_VALUE";
		}
	}
	
	private static Object loadKey(YamlConfiguration c, String currentKey, Runnable ifExists, Runnable ifNotExists) {
		Object currentValue = c.get(currentKey);
		Object def = null;
		Object returnValue = null;
		if (currentValue == null) {
			def = getDefaultValue(currentKey);
			c.set(currentKey, def);
			returnValue = def;
			if(ifExists!=null) {
				ifExists.run();
			}
		} else {
			try {
				returnValue = currentValue;
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
		boolean hasChanges = false;
	    for(String key : newConfig.getKeys(true)) { // missing keys added by user
	        if(!oldConfig.contains(key)) {
	        	hasChanges = true;
	        }
	    }
	    for(String key : oldConfig.getKeys(true)) {
	        if(!newConfig.contains(key)) {
	        	hasChanges = true;
	        }
	    }
	    
		if (hasChanges) {
			try {
				newConfig.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return hasChanges;
	}
	
	private static void checkStartScript() {
		if(ConfigData.disableNoExistingStartFileWarning) return;
		if(!new File(serverDirectory, References.runFileString).exists()) {
			String noExistingStartFileWarning = ChatColor.RED + "You haven't specified your run script file name in the config!"
					+ "(for world reseting, but other things are fine)";
			log(noExistingStartFileWarning);
		}
	}
	
	public static void logCastEx(String currentKey) {
		log("Wrong value type for \"" + currentKey + "\" in the config");
	}
}
