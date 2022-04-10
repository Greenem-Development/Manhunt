package org.greenem.plugins.manhunt.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.greenem.plugins.manhunt.util.OsType;

public class References {
	public static ArrayList<String> Hunters = new ArrayList<>();
	public static ArrayList<String> Speedrunners = new ArrayList<>();
	public static Map <String, Location> HuntersPortalFrom = new HashMap<>();
	public static Map <String, Location> HuntersPortalTo = new HashMap<>();
	public static Map <String, Location> PortalFrom = new HashMap<>();
	public static Map <String, Location> PortalTo = new HashMap<>();
	public static Map <String, Location> PortalEnd = new HashMap<>();
	
	public static ArrayList<String> Frozen = new ArrayList<>();
	public static Map <Player, Location> FrozenLocs = new HashMap<>();
	public static Map <Player, Integer> FrozenAir = new HashMap<>();

	public static File serverDirectory;
	public static File configsDirectory;
	public static File pluginConfigDirectory;
	
	public static String runFileString = "asdgsdfgsd";
	public static String javaString = "java"; //"\"C:\\Program Files\\Java\\jdk-17.0.1\\bin\\java\"";
	public static OsType os;
//	public static String launchServerFileName;
}
