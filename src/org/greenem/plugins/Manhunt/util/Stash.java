package org.greenem.plugins.Manhunt.util;

import static org.greenem.plugins.Manhunt.util.SimpleLogger.log;

import org.greenem.plugins.Manhunt.Main.Main;

public class Stash {
	public void healTests() {
		Main.Speedrunners.add("Greenem");
		Main.Hunters.add("MayX");
		//Main.Hunters.add("Dismov");
		Main.HealCommand(null, null, "Greenem", new String[]{});
		log("-----");
		Main.HealCommand(null, null, "Greenem", new String[]{"Greenem"});
		log("-----");
		Main.HealCommand(null, null, "Greenem", new String[]{"MayX"});
		log("-----");
		Main.HealCommand(null, null, "Greenem", new String[]{"Dismov"});
		log("----------------------------------------------");
		Main.HealCommand(null, null, "MayX", new String[]{});
		log("-----");
		Main.HealCommand(null, null, "MayX", new String[]{"MayX"});
		log("-----");
		Main.HealCommand(null, null, "MayX", new String[]{"Greenem"});
		log("-----");
		Main.HealCommand(null, null, "MayX", new String[]{"Dismov"});
		log("----------------------------------------------");
		Main.HealCommand(null, null, "Dismov", new String[]{});
		log("-----");
		Main.HealCommand(null, null, "Dismov", new String[]{"Dismov"});
		log("-----");
		Main.HealCommand(null, null, "Dismov", new String[]{"Greenem"});
		log("-----");
		Main.HealCommand(null, null, "Dismov", new String[]{"MayX"});
	}
}
