package org.greenem.plugins.manhunt.util;

import java.io.File;

import org.greenem.plugins.manhunt.data.References;

public class DebugModeSwitch {
	public static void init() {
		File f = new File(References.pluginConfigDirectory, "enableDebug.dev");
		if(!f.exists()) {
			SimpleLogger.debugOutputEnabled = false;
		}
	}
}
