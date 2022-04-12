package org.greenem.plugins.manhunt.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.greenem.plugins.manhunt.data.Lines;
import org.greenem.plugins.manhunt.data.References;

public class OsDependent {
	public static OsType getSystemType() {
//		System.getProperties().list(System.out);
		String os = System.getProperty("os.name");
		if(os.toLowerCase().contains("windows")) {
			return OsType.WINDOWS;
		} else {
			return OsType.LINUX;
		}
	}
	
	public static boolean checkSystemSupported(OsType... osTypes) {
		return checkSystemSupported(null, osTypes);
	}
	
	public static boolean checkSystemSupported(CommandSender sender, OsType... osTypes) {
		for (int i = 0; i < osTypes.length; i++) {
			if(References.os==osTypes[i]) return true;
		}
		if(sender!=null) {
			sender.sendMessage(ChatColor.RED + Lines.operationNotSupportedOnSystem);
		}
		return false;
	}
}
