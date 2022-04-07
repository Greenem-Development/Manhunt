package org.greenem.plugins.Manhunt.Main;

import static org.greenem.plugins.Manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.Manhunt.util.SimpleLogger.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.greenem.plugins.Manhunt.completers.EmptyCompleterNoArgs;
import org.greenem.plugins.Manhunt.completers.MhGmCompleter;
import org.greenem.plugins.Manhunt.completers.MhStartCompleter;
import org.greenem.plugins.Manhunt.config.ConfigData;
import org.greenem.plugins.Manhunt.config.ConfigOperations;
//import org.spigotmc.event.entity.EntityDismountEvent;
import org.greenem.plugins.Manhunt.completers.MhProblemsCompleter;

//import com.mysql.jdbc.Connection;

import org.bukkit.inventory.meta.CompassMeta;

public class Main extends JavaPlugin implements Listener {
	Logger log = getLogger();
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
	public static File mainConfigDirectory;
	
	public static String runFileString = "asdgsdfgsd";
	public static String javaString = "java"; //"\"C:\\Program Files\\Java\\jdk-17.0.1\\bin\\java\"";
//	public static String launchServerFileName;
	
	public Plugin plugin = this;

	private static Main instance;
	public static Main getInstance() {
		if(instance==null) {
			instance = new Main();
		}
		return instance;
	}
	
	@Override
	public void onEnable() {
		log(ChatColor.GREEN + "Your Manhunt plugin has been enabled!");
		initAll();
		ConfigOperations.loadConfig();
	}
	
	@Override
	public void onDisable() {
		log("Your Manhunt plugin has been disabled.");
	}
	
	public boolean isSenderAPlayer(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.RED + "You can't do it through console!");
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
	
	public boolean isSenderAPlayerOrConsole(CommandSender sender) {
		if(!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = null;
		String pName = "";
		try {
			p = (Player) sender;
			pName = p.getName();
		} catch (ClassCastException e) {}
		
		if(cmd.getName().equalsIgnoreCase("gm")){
			if(isSenderAPlayerOrConsole(sender)) {
				GmCommand(sender, args);
			}
		}
		else if(cmd.getName().equalsIgnoreCase("heal")){
			if(isSenderAPlayerOrConsole(sender)) {
				HealCommand(sender, p, null, args);
			}
		}
		
		//--------------------------------------------------------------------------------------------------------------------------------------------------\\
		//-------------------------------------------------------M-A-N-H-U-N-T------------------------------------------------------------------------------\\
		//--------------------------------------------------------------------------------------------------------------------------------------------------\\
		
		if(cmd.getName().equalsIgnoreCase("mhhunter")){
			if(!isSenderAPlayer(sender)) {
				return true;
			}
			if(Speedrunners.contains(pName)) {
//				p.sendMessage(ChatColor.RED + "You are a speedrunner. Use /norole first");
//				return true;
				doNoRole(p, pName);
			}
			if(!p.getWorld().getEnvironment().equals(Environment.NORMAL)) {
				p.sendMessage(ChatColor.RED + "You must be in the overworld to use this command.");
				return true;
			}
			if(!Hunters.contains(pName)) {
				Hunters.add(pName);
				AddItemStack(p, new ItemStack(Material.COMPASS), 8);
				findTarget();
				p.sendMessage(ChatColor.GOLD + "Now you are a " + ChatColor.RED + "" + ChatColor.BOLD + "hunter.");
			}
			else {
				p.sendMessage(ChatColor.RED + "You are already a hunter.");
			}
		}
		else if(cmd.getName().equalsIgnoreCase("mhspeedrunner")){
			if(!isSenderAPlayer(sender)) {
				return true;
			}
			if(Hunters.contains(pName)) {
//				p.sendMessage(ChatColor.RED + "You are a hunter. Use /norole");
//				return true;
				doNoRole(p, pName);
			}
			if(!p.getWorld().getEnvironment().equals(Environment.NORMAL)) {
				p.sendMessage(ChatColor.RED + "You must be in the overworld to use this command. (There would be problems with missing portal location. Later this will be avaliable)");
				return true;
			}
			if(!Speedrunners.contains(pName)) {
				Speedrunners.add(pName);
				p.sendMessage(ChatColor.GOLD + "Now you are a speedrunner.");
			}
			else {
				p.sendMessage(ChatColor.RED + "You are already a " + ChatColor.GREEN + "" + ChatColor.BOLD + "speedrunner.");
			}
		}
		else if(cmd.getName().equalsIgnoreCase("mhnorole")){
			if(!isSenderAPlayer(sender)) {
				return true;
			}
			doNoRole(p, pName);
			p.sendMessage(ChatColor.GOLD + "You have removed your role.");
		}
		else if(cmd.getName().equalsIgnoreCase("mhstart")){
			if(!isSenderAPlayer(sender)) {
				return true;
			}
			if(args.length<1) {
				p.sendMessage(ChatColor.RED + "Insert the delay in seconds. /mhstart <seconds>");
			}
			if(args.length==1) {
				if(isInteger(args[0])) MhStart(Integer.parseInt(args[0]));
			}
			if(args.length>1) {
				p.sendMessage(ChatColor.RED + "Incorrect arguments. /mhstart <seconds>");
			}
		}
		else if(cmd.getName().equalsIgnoreCase("mhstop")){
			if(!isSenderAPlayerOrConsole(sender)) {
				return true;
			}
			mhStopComand(p, args);
		}
		else if(cmd.getName().equalsIgnoreCase("mhproblems")){
			if(!isSenderAPlayer(sender)) {
				return true;
			}
			mhProblemsComand(p, args);
		}
		else if(cmd.getName().equalsIgnoreCase("loc")){
			if(!isSenderAPlayer(sender)) {
				return true;
			}
			//sendActionBar(p, p.getLocation().toString());
			p.sendMessage(ChatColor.ITALIC + "" + ChatColor.WHITE + "Your location: " + ChatColor.AQUA + "" + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ() + " ");
			p.sendMessage(ChatColor.ITALIC + "" + ChatColor.WHITE + "Your direction: " + ChatColor.LIGHT_PURPLE + "" + GetPlayerFacing(p));
		}
		else if (cmd.getName().equalsIgnoreCase("resetworld")) {
			if(!isSenderAPlayerOrConsole(sender)) {
				return true;
			}
			resetWorld(sender);
		}
		else if (cmd.getName().equalsIgnoreCase("mhreloadconfig")) {
			if(!isSenderAPlayerOrConsole(sender)) {
				return true;
			}
			ConfigOperations.loadConfig();
			sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config.");
		}
		return false;
	}
	
	public void doNoRole(Player p, String pName) {
		if(Hunters.contains(pName)) {
			Hunters.remove(pName);
		}
		else if(Speedrunners.contains(pName)) {
			Speedrunners.remove(pName);
		}
		if(PortalEnd.containsKey(pName)) PortalEnd.remove(pName);
		if(PortalFrom.containsKey(pName)) PortalFrom.remove(pName);
		if(PortalTo.containsKey(pName)) PortalTo.remove(pName);
		if(HuntersPortalFrom.containsKey(pName)) HuntersPortalFrom.remove(pName);
		if(HuntersPortalTo.containsKey(pName)) HuntersPortalTo.remove(pName);
		RemoveMaterial(p, Material.COMPASS);
	}
	
	@EventHandler
	public void Move(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(Speedrunners.contains(e.getPlayer().getName())) findTarget();
		if(Hunters.contains(e.getPlayer().getName())) findTarget();
		if(Frozen.contains(p.getName())) {
			if(!e.getFrom().getWorld().equals(e.getTo().getWorld())) return;
			Location loc = e.getTo().clone();
			p.setAllowFlight(true);
			p.setFlying(true);
			loc.setX(FrozenLocs.get(p).getX());
			loc.setY(FrozenLocs.get(p).getY());
			loc.setZ(FrozenLocs.get(p).getZ());
			p.teleport(loc);
		}
	}
	
	public void findTarget() {
		for (String hunter : Hunters) {
			Player plHunter = Bukkit.getPlayerExact(hunter);
			if(plHunter==null) {
				continue;
			}
			if(plHunter.isDead()==false) { //TODO java.lang.NullPointerException: Cannot invoke "org.bukkit.entity.Player.isDead()" because "plHunter" is null, 274
				Map <String, Double> Distations = new HashMap<>();
				for (String speedrunner : Speedrunners) {
					Player speedrunnerPl = Bukkit.getPlayerExact(speedrunner);
					if(speedrunnerPl==null) {
						continue;
					}
					if(speedrunnerPl.isDead()==false) { //TODO java.lang.NullPointerException: Cannot invoke "org.bukkit.entity.Player.isDead()" because "speedrunnerPl" is null
						double distance = GetDistance(plHunter, speedrunnerPl);
						if(distance<0) {
							log.info("skipped.");
							return;
						}
						Distations.put(speedrunner, distance);
					}
				}
				//log.info(Distations.toString());
				double nearest = -1;
				int times = 0;
				for (double dist : Distations.values()) {
					if(times == 0) {
						nearest = dist;
						times++;
					}
					else {
						if(nearest>dist) {
							nearest = dist;
						}
					}
				}
				for (String sp : Speedrunners) {
					Player plSp = Bukkit.getPlayerExact(sp);
					if(Bukkit.getPlayerExact(sp).isDead()==false) {
						if(Distations.get(sp).equals(nearest)) {
							Location target = GetTargetLocation(plHunter, plSp);
							if(target.getWorld().getEnvironment().equals(Environment.NETHER) || target.getWorld().getEnvironment().equals(Environment.THE_END)) {
								for(int i = 0; i < plHunter.getInventory().getSize(); i++) {
							   		ItemStack CompassItem = plHunter.getInventory().getItem(i);
							      	if(CompassItem != null) {
							    	  	if(CompassItem.getType().equals(Material.COMPASS)) {
								    	  	if(plHunter.getInventory().getItem(plHunter.getInventory().getHeldItemSlot())!=null && plHunter.getInventory().getItem(plHunter.getInventory().getHeldItemSlot()).getType().equals(Material.COMPASS)) {
								    	  	
								    	  	}
								    	  	else if (plHunter.getWorld().getEnvironment()!=plSp.getWorld().getEnvironment()) {
								    	  		// because there was a bug
								    	  	}
								    	  	else {
								    	  		CompassMeta meta = (CompassMeta) CompassItem.getItemMeta();
								    	  		if(meta.getLodestone() == null) meta.setLodestoneTracked(false); //если нужно только
								    	  		meta.setLodestone(target);
								                CompassItem.setItemMeta(meta);
								    	  	}
							        	}
							      	}
								}
							}
							if(target.getWorld().getEnvironment().equals(Environment.NORMAL)) {
								boolean toggle1 = false;
								for(int i = 0; i < plHunter.getInventory().getSize(); i++) {
							   		ItemStack CompassItem = plHunter.getInventory().getItem(i);
							      	if(CompassItem != null) {
							    	  	if(CompassItem.getType().equals(Material.COMPASS)) {
							    	  		toggle1 = true;
							    	  		//log.info("has: " + CompassItem.getItemMeta().toString());
							    	  		CompassMeta meta = (CompassMeta) CompassItem.getItemMeta();
							    	  		CompassItem.setItemMeta(meta);
							    	  		if(meta.getLodestone()!=null) {
							    	  			//int slot = i;
							    	  			plHunter.getInventory().setItem(i, new ItemStack(Material.COMPASS, CompassItem.getAmount()));
		//					    	  			plHunter.setCompassTarget(target);
							    	  		}
							    	  		else {
		//					    	  			plHunter.setCompassTarget(target);
							    	  		}
							        	}
							      	}
								}
								if(toggle1) {
									plHunter.setCompassTarget(target);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public double GetDistance (Player hunter, Player speedrunner) {
		String nameH = hunter.getName();
		String nameS = speedrunner.getName();
		double result = -1;
		if(hunter.getWorld().equals(speedrunner.getWorld())) {
			result = 0;
			result = hunter.getLocation().distance(speedrunner.getLocation());
		}
		else {
			if(speedrunner.getWorld().getEnvironment().equals(Environment.THE_END)) {
				result = 0;
				result+=hunter.getLocation().distance(PortalEnd.get(nameS));
			}
			else {
				if(PortalFrom.containsKey(nameS)) {
					result = 0;
					//log.info("from: " + PortalFrom.get(speedrunner).toString());
					//log.info("to: " + PortalTo.get(speedrunner).toString());
					if(hunter.getWorld().getEnvironment().equals(PortalFrom.get(nameS).getWorld().getEnvironment()) && speedrunner.getWorld().getEnvironment().equals(PortalTo.get(nameS).getWorld().getEnvironment())) {
						result += hunter.getLocation().distance(PortalFrom.get(nameS));
						result += speedrunner.getLocation().distance(PortalTo.get(nameS));
					}
				}
				else {
					if(HuntersPortalTo.containsKey(nameH) && HuntersPortalFrom.containsKey(nameH)) {
						//log.info(HuntersPortalTo.containsKey(nameH) + "");
						//log.info(HuntersPortalFrom.containsKey(nameH) + "");
						//if(PortalFrom.containsKey(nameS)) log.info(hunter.getWorld().getEnvironment().equals(PortalFrom.get(nameS).getWorld().getEnvironment()) + "");
						//if(PortalTo.containsKey(nameS)) log.info(speedrunner.getWorld().getEnvironment().equals(PortalTo.get(nameS).getWorld().getEnvironment()) + "");
						result = 0;
						if(!HuntersPortalTo.get(nameH).getWorld().getEnvironment().equals(hunter.getWorld().getEnvironment())) {
							log.info("skipped1");
							return -1;
						}
						result += hunter.getLocation().distance(HuntersPortalTo.get(nameH));
						if(!HuntersPortalFrom.get(nameH).getWorld().getEnvironment().equals(speedrunner.getWorld().getEnvironment())) {
							log.info("skipped2");
							return -1;
						}
						result += speedrunner.getLocation().distance(HuntersPortalFrom.get(nameH));
						//Bukkit.broadcastMessage(ChatColor.RED + "Almost a error, speedrunner got into another dimension without plugin seeing this.");
					}
					else {
						Bukkit.broadcastMessage(ChatColor.RED + "ERROR! A hunter got into another dimension without plugin seeing this.");
					}
				}
			}
		}
		return result;
	}
	
	public Location GetTargetLocation (Player hunter, Player speedrunner) {
		String nameH = hunter.getName();
		String nameS = speedrunner.getName();
		Location result = hunter.getLocation();
		if(hunter.getWorld().equals(speedrunner.getWorld())) {
			result = speedrunner.getLocation();
			//log.info("same world");
		}
		else {
			if(speedrunner.getWorld().getEnvironment().equals(Environment.THE_END)) {
				result = PortalEnd.get(nameS);
			}
			else {
				if(PortalFrom.containsKey(nameS)) {
					result = PortalFrom.get(nameS);
				}
				else {
					if(HuntersPortalTo.containsKey(nameH)) {
						result = HuntersPortalTo.get(nameH);
					}
					else {
						log.info(ChatColor.RED + "Error 0001");
					}
				}
			}
		}
		return result;
	}
	
	public void GmCommand(CommandSender sender, String[] args) {
		if(!ConfigData.allowGmCommandForOps) {
			sender.sendMessage(ChatColor.RED + "This command was disabled in the config.");
			return;
		}
		Player p = Bukkit.getPlayerExact(sender.getName());
		if(sender instanceof ConsoleCommandSender || p.isOp() == true){
			if(args.length == 0){
				sender.sendMessage(ChatColor.RED + "Please, insert gamemode number.");
			}
			else if(args.length == 1){
				if(sender instanceof ConsoleCommandSender) {
					sender.sendMessage(ChatColor.RED + "You can only change the players' gamemodes from console!");
					return;
				}
				if(args[0].equalsIgnoreCase("0")){
					p.setGameMode(GameMode.SURVIVAL);
				}
				else if(args[0].equalsIgnoreCase("1")){
					p.setGameMode(GameMode.CREATIVE);
				}
				else if(args[0].equalsIgnoreCase("2")){
					p.setGameMode(GameMode.ADVENTURE);
				}
				else if(args[0].equalsIgnoreCase("3")){
					p.setGameMode(GameMode.SPECTATOR);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Please, insert correct gamemode number.");
				}
			}
			else if(args.length == 2){
				if(Bukkit.getPlayerExact(args[1]) != null){
					if(args[0].equalsIgnoreCase("0")){
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.SURVIVAL);
					}
					else if(args[0].equalsIgnoreCase("1")){
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.CREATIVE);
					}
					else if(args[0].equalsIgnoreCase("2")){
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.ADVENTURE);
					}
					else if(args[0].equalsIgnoreCase("3")){
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.SPECTATOR);
					}
					else {
						sender.sendMessage(ChatColor.RED + "Please, insert correct gamemode number.");
					}
				}
				else {
					if(args[1].equals("0") || args[1].equals("1") || args[1].equals("2") || args[1].equals("3")) {
						sender.sendMessage(ChatColor.RED + "Usage for other players: /gm <number 0-3> <player>");
					}
					else {
						sender.sendMessage(ChatColor.RED + "Such player isn't online.");
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Please, type the command in such order: /gm <number 0-3> <player>");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "You can't use this command.");
		}
	}
	
	public static String coloredName(String name) {
		String s = "";
		
		if(name.equals("[CONSOLE]")) {
			s += ChatColor.GOLD + "" + ChatColor.BOLD + name;
		}
		else if(Hunters.contains(name)) {
			s += ChatColor.RED + "" + ChatColor.BOLD + name;
		}
		else if(Speedrunners.contains(name)) {
			s += ChatColor.GREEN + "" + ChatColor.BOLD + name;
		}
		else {
			s += ChatColor.WHITE + "" + ChatColor.BOLD + name;
		}
		return s;
	}
	
	public static String coloredHealSentence(String from, String to) {
		String s = "";
		
		if(to.equals("[CONSOLE]")) {
			s += ChatColor.GOLD + "" + ChatColor.BOLD;
		}
		else if(Hunters.contains(to)) {
			s += ChatColor.RED + "" + ChatColor.BOLD;
		}
		else if(Speedrunners.contains(to)) {
			s += ChatColor.GREEN + "" + ChatColor.BOLD;
		}
		else {
			s += ChatColor.WHITE + "" + ChatColor.BOLD;
		}
		s += to;
		
		s += ChatColor.RESET + " was healed";
		
		if(!from.equals(to)) {
			s += " by ";
			if(from.equals("[CONSOLE]")) {
				s += ChatColor.GOLD + "" + ChatColor.BOLD;
			}
			if(Hunters.contains(from)) {
				s += ChatColor.RED + "" + ChatColor.BOLD;
			}
			else if(Speedrunners.contains(from)) {
				s += ChatColor.GREEN + "" + ChatColor.BOLD;
			}
			else {
				s += ChatColor.WHITE + "" + ChatColor.BOLD;
			}
			s += from;
		}
		return s;
	}
	
	public static boolean logHealInfo = false; // false
	
	public static void sendOthersTheAlert(String pAlert, String pName, String notifOthers) {
		if(!pAlert.equals(pName)) {
			Player pAlertP = Bukkit.getPlayerExact(pAlert);
			if(pAlertP!=null) {
				pAlertP.sendMessage(notifOthers);
			}
		}
	}
	
	public static void HealCommand(CommandSender sender, Player p, String pName, String[] args) {
		boolean console = sender instanceof ConsoleCommandSender;
		if(!ConfigData.allowHealCommandForOps) {
			if(p!=null) {
				p.sendMessage(ChatColor.RED + "This command was disabled in the config.");
			}
			return;
		}
		if(console) {
			pName = "[CONSOLE]";
		}
		else if(pName==null) {
			pName = p.getName();
		}
		if(p==null) {
			p = Bukkit.getPlayerExact(pName);
		}
		String pFrom = pName;
		String notifpersonal;
		String colorNameFrom = coloredName(pFrom);
		String notifOthers;
		if(args.length == 0) {
			if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage(ChatColor.RED + "You can't heal yourself as the console!");
				return;
			}
			if(p!=null) {
				p.setHealth(20);
				p.setFoodLevel(20);
			}
			notifpersonal = ChatColor.GRAY + "You have been healed";
			notifOthers = colorNameFrom + ChatColor.RESET + "" + ChatColor.GRAY + " was healed";
			if(p!=null) {
				p.sendMessage(notifpersonal);
			}
			if(logHealInfo) log("notifpersonal: " + notifpersonal);
			if(Hunters.contains(pName) || Speedrunners.contains(pName)) {
				if(logHealInfo) log("notifOthers: " + notifOthers);
				for (String pAlert : Hunters) {
					sendOthersTheAlert(pAlert, pName, notifOthers);
				}
				for (String pAlert : Speedrunners) {
					sendOthersTheAlert(pAlert, pName, notifOthers);
				}
			}
		}
		else if(args.length==1) {
			String pTo = args[0];
			Player pToP = Bukkit.getPlayerExact(pTo);
			if(pToP!=null || logHealInfo) {
				String colorNameTo = coloredName(pTo);
				boolean atLeastOneImportantPlayer = Hunters.contains(pFrom) || Speedrunners.contains(pFrom) || Hunters.contains(pTo) || Speedrunners.contains(pTo);
				if(pToP!=null) {
					pToP.setHealth(20);
					pToP.setFoodLevel(20);
				}
				
				if(pTo.equals(pFrom)) {
					notifpersonal = ChatColor.GRAY + "You have been healed";
					if(p!=null) {
						p.sendMessage(notifpersonal);
					}
					notifOthers = colorNameFrom + ChatColor.RESET + "" + ChatColor.GRAY + " was healed";
				}
				else {
					String notifToThatOne = ChatColor.GRAY + "You have been healed by " + colorNameFrom;
					notifpersonal = ChatColor.GRAY + "You have healed " + colorNameTo;
					if(pToP!=null) {
						pToP.sendMessage(notifToThatOne);
						if(logHealInfo) log("notifToThatOne: " + notifToThatOne);
					}
					if(p!=null) {
						p.sendMessage(notifpersonal);
					}
					notifOthers = coloredHealSentence(colorNameFrom, colorNameTo);
				}
				
				if(logHealInfo) log("notifpersonal: " + notifpersonal);
				
				if(atLeastOneImportantPlayer) {
					if(logHealInfo) log("notifOthers: " + notifOthers);
					for (String pAlert : Hunters) {
						if (!pAlert.equals(pTo)) {
							sendOthersTheAlert(pAlert, pName, notifOthers);
						}
					}
					for (String pAlert : Speedrunners) {
						if (!pAlert.equals(pTo)) {
							sendOthersTheAlert(pAlert, pName, notifOthers);
						}
					}
				}
			}
			else {
				String problem = ChatColor.RED + "Such player isn't online.";
				if(logHealInfo) log("personal error to " + pName + ": " + problem);
				if(p!=null) {
					p.sendMessage(problem);
				}
			}
		}
		else {
			String problem = ChatColor.RED + "Please, type the command in such order: /heal <player>.";
			log("personal error to " + pName + ": " + problem);
			if(p!=null) {
				p.sendMessage(problem);
			}
		}
	}

	public void AddItemStack(Player p, ItemStack ItemStack, int slot) {
		if(!p.getInventory().contains(ItemStack)) {
			if(p.getInventory().getItem(slot) == null) p.getInventory().setItem(slot, ItemStack);
			else p.getInventory().addItem(ItemStack);
		}
	}

	public void RemoveMaterial(Player p, Material material) {
		if(p.getInventory().contains(material)) {
			p.getInventory().remove(material);
		}
	}
	
	public void RemoveMaterial(Player p, int slot) {
		//p.getInventory().remove(p.getInventory().getItem(slot));
		p.getInventory().setItem(slot, new ItemStack(Material.AIR));
	}
	
	@EventHandler
	public void PlayerPortalEvent (PlayerPortalEvent e) {
		Player p = e.getPlayer();
		String nameP = p.getName();
		if(Speedrunners.contains(nameP)) {
			if(e.getTo().getWorld().getEnvironment().equals(Environment.THE_END)) {
				PortalEnd.put(nameP, e.getTo());
			}
			else {
				if(PortalFrom.containsKey(nameP)) PortalFrom.remove(nameP);
				if(PortalTo.containsKey(nameP)) PortalTo.remove(nameP);
				PortalFrom.put(nameP, e.getFrom());
				PortalTo.put(nameP, e.getTo());
			}
		}
		if(Hunters.contains(nameP)) {
			if(e.getTo().getWorld().getEnvironment().equals(Environment.THE_END)) {
				//PortalEnd.put(nameP, e.getTo());
			}
			else {
				if(HuntersPortalFrom.containsKey(nameP)) HuntersPortalFrom.remove(nameP);
				if(HuntersPortalTo.containsKey(nameP)) HuntersPortalTo.remove(nameP);
				HuntersPortalFrom.put(nameP, e.getFrom());
				HuntersPortalTo.put(nameP, e.getTo());
			}
		}
		checkNetherCompassInHand(p, e.getTo(), p.getInventory().getHeldItemSlot(), false);
		findTarget();
	}

	@EventHandler
	public void PlayerDies (PlayerDeathEvent e) {
		Player p = e.getEntity();
		String nameP = p.getName();
		if(PortalFrom.containsKey(nameP) && PortalTo.containsKey(nameP) && p.getWorld().getEnvironment().equals(Environment.NETHER)) {
			Location from = new Location(PortalFrom.get(nameP).getWorld(), PortalFrom.get(nameP).getX(), PortalFrom.get(nameP).getY(), PortalFrom.get(nameP).getZ());
			PortalFrom.put(nameP, PortalTo.get(nameP));
			PortalTo.put(nameP, from);
		}
		//p.getInventory().remove(new ItemStack(Material.COMPASS));
		if(Hunters.contains(nameP)) {
			for(int i = 0; i<p.getInventory().getSize(); i++) {
				try {
					if(e.getDrops().get(i).getType().equals(Material.COMPASS)) e.getDrops().remove(i); //TODO java.lang.IndexOutOfBoundsException: Index 8 out of bounds for length 8, 233 | Index 1 out of bounds for length 0
				}
				catch (IndexOutOfBoundsException e1) {
					
				}
			}
		}
			//for(int i = 0; i<p.getInventory().getSize()-1; i++) {
			//p.getInventory().remove(new ItemStack(Material.COMPASS));
			//Iterator<ItemStack> interators = e.getDrops().iterator();
			/*while(interators.hasNext()){
				ItemStack stack = interators.next();
				//if(interators.next().isSimilar(new ItemStack(Material.COMPASS))) {
				if(stack.getType().equals(Material.COMPASS)){
					stack.
				}
			}*/
			//e.getDrops().remove(new ItemStack(Material.COMPASS));
			//e.getDrops().remove(new ItemStack(Material.COMPASS));
	}
	
	@EventHandler
	public void Respawn (PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		String nameP = p.getName();
		if(Hunters.contains(nameP)) {
			AddItemStack(p, new ItemStack(Material.COMPASS), 0);
			findTarget();
		}
	}
	
	@EventHandler
	public void onScroll(PlayerItemHeldEvent e) {
		checkNetherCompassInHand(e.getPlayer(), e.getNewSlot());
	}
	
	public void checkNetherCompassInHand(Player p, int slot) {
		checkNetherCompassInHand(p, p.getLocation(), slot);
	}
	
	public void checkNetherCompassInHand(Player p, Location loc, int slot) {
		checkNetherCompassInHand(p, loc, slot, true);
	}
	public void checkNetherCompassInHand(Player p, Location loc, int slot, boolean checkMeta) {
//		log.info("check");
		if(loc.getWorld().getEnvironment().equals(Environment.THE_END) || loc.getWorld().getEnvironment().equals(Environment.NETHER)) {
//			log.info("check2");
			if(p.getInventory().getItem(slot)!=null) {
//				log.info("check3");
				ItemStack maybeCompass = p.getInventory().getItem(slot);
				if(maybeCompass.getType().equals(Material.COMPASS)){
//					log.info("check4");
					boolean toggle1 = false;
					if(checkMeta) {
						//log.info(e.getPlayer().getInventory().getItem(e.getNewSlot()).getItemMeta().getAttributeModifiers().containsKey(e)+ "");
						CompassMeta meta = (CompassMeta) maybeCompass.getItemMeta();
		    	  		if(meta.getLodestone() != null) {
		    	  			//e.setCancelled(true);
		    	  			toggle1 = true;
		    	  		}
					}
					else {
						toggle1 = true;
					}
					if(toggle1) {
						p.sendMessage(ChatColor.RED + "Sorry, but the compass will not update while you are holding it in the Nether (or End)");
					}
				}
			}
		}
	}
	
	public void MhStart(int delay) {
		MhStart(delay, 2);
	}
	
	public void MhStart(int delay, int speedrunnerDelay) {
		for(String speendrunner : Speedrunners) {
			Frozen.add(speendrunner);
			Player spPl = Bukkit.getPlayerExact(speendrunner);
			FrozenLocs.put(spPl, spPl.getLocation());
			if(spPl.getGameMode().equals(GameMode.SURVIVAL) || spPl.getGameMode().equals(GameMode.ADVENTURE)) {
				if(!spPl.getGameMode().equals(GameMode.CREATIVE) && !spPl.getGameMode().equals(GameMode.SPECTATOR)) {
					spPl.setFlying(false);
					spPl.setAllowFlight(false);
				}
			}
			Bukkit.getPlayerExact(speendrunner).sendMessage(ChatColor.YELLOW + "You have 2 seconds to prepare");
		}
		for(String hunter: Hunters) {
			Player pHunter = Bukkit.getPlayerExact(hunter);
			Frozen.add(hunter);
			Player hrPl = Bukkit.getPlayerExact(hunter);
			FrozenLocs.put(hrPl, hrPl.getLocation());
			if(hrPl.getGameMode().equals(GameMode.SURVIVAL) || hrPl.getGameMode().equals(GameMode.ADVENTURE)) {
				if(!hrPl.getGameMode().equals(GameMode.CREATIVE) && !hrPl.getGameMode().equals(GameMode.SPECTATOR)) {
					hrPl.setFlying(false);
					hrPl.setAllowFlight(false);
				}
			}
			pHunter.sendMessage(ChatColor.GRAY + "The speedrunners have " + speedrunnerDelay + " seconds to prepare...");
			pHunter.sendTitle(ChatColor.GRAY + "Wait...", "", 5, 18*speedrunnerDelay, 4);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				for(String speendrunner : Speedrunners) {
					Frozen.remove(speendrunner);
					Bukkit.getPlayerExact(speendrunner).sendMessage(ChatColor.YELLOW + "Now run!");
					Player spPl = Bukkit.getPlayerExact(speendrunner);
					FrozenLocs.remove(spPl);
					if(!spPl.getGameMode().equals(GameMode.CREATIVE) && !spPl.getGameMode().equals(GameMode.SPECTATOR)) {
						spPl.setFlying(false);
						spPl.setAllowFlight(false);
					}
				}
				for(String hunter: Hunters) {
					Player pHunter = Bukkit.getPlayerExact(hunter);
					//Frozen.add(hunter);
					final Player theHunter = pHunter;
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							Frozen.remove(theHunter.getName());
							FrozenLocs.remove(theHunter);
							if(!theHunter.getGameMode().equals(GameMode.CREATIVE) && !theHunter.getGameMode().equals(GameMode.SPECTATOR)) {
								theHunter.setFlying(false);
								theHunter.setAllowFlight(false);
							}
						}
					}, 20*delay);
					for (int i = delay; i>=0; i--) {
						final int iCopy = i;
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								//hunter.sendMessage(""+iCopy);
								if(iCopy>0) {
									if(iCopy==delay) {
										pHunter.sendTitle(ChatColor.GRAY + "The game starts in", ChatColor.GREEN + "" + iCopy, 5, 21, 0);
									}
									else {
										pHunter.sendTitle(ChatColor.GRAY + "The game starts in", ChatColor.GREEN + "" + iCopy, 0, 21, 0);
									}
								}
								else {
									pHunter.sendTitle(ChatColor.GREEN + "Go!", "", 5, 4, 7);
								}
							}
						}, 20*(delay-i));
					}
				}
			}
		}, 20 * speedrunnerDelay);
		
		
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
    
	public static void sendActionBar(Player player, String message){
//        CraftPlayer p = (CraftPlayer) player;
		
        //IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        //PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, null, null);
        //player.send
        //((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
    }
	
	private static String GetPlayerFacing(Player p) {
		double rot = (p.getLocation().getYaw() - 90) % 360-90;
		if (rot < 0) {
            rot += 360.0;
        }
        if (0 <= rot && rot < 22.5) {
            return "North (Север)";
        } else if (22.5 <= rot && rot < 67.5) {
            return "Northeast (Северо-восток)";
        } else if (67.5 <= rot && rot < 112.5) {
            return "East (Восток)";
        } else if (112.5 <= rot && rot < 157.5) {
            return "Southeast (Юго-восток)";
        } else if (157.5 <= rot && rot < 202.5) {
            return "South (Юг)";
        } else if (202.5 <= rot && rot < 247.5) {
            return "Southwest (Юго-запад)";
        } else if (247.5 <= rot && rot < 292.5) {
            return "West (Запад)";
        } else if (292.5 <= rot && rot < 337.5) {
            return "Northwest (Северо-Запад)";
        } else if (337.5 <= rot && rot < 360.0) {
            return "North (Север)";
        } else {
            return null;
        }
	}
	
	public void mhStopComand(Player p, String[] args) {
		p.sendMessage(ChatColor.RED + "This command is not coded yet");
	}
	
	public void mhProblemsComand(Player p, String[] args) {
		if(!Hunters.contains(p.getName())) {
			p.sendMessage(ChatColor.RED + "You must be a hunter to use this command!");
			return;
		}
		if(args.length==1) {
			if(args[0].equals("give_compass")) {
				if(p.getInventory().contains(Material.COMPASS)) {
					p.sendMessage(ChatColor.RED + "You already have a compass!");
					return;
				}
				AddItemStack(p, new ItemStack(Material.COMPASS), 8);
				p.sendMessage(ChatColor.GREEN + "You been given a compass.");
			}
		}
		else {
			p.sendMessage(ChatColor.RED + "Wrong arguments");
		}
	}
	
	private void initAll() {
		registerListeners(); // not sure if that should be done so early
		RegisterAllCommands();
		initFiles();
	}
	
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	private void initFiles() {
		serverDirectory = Bukkit.getWorldContainer();
		configsDirectory = new File(serverDirectory, "plugins/configs");
		mainConfigDirectory = new File(configsDirectory, "ManhuntAndWorldReset");
		mainConfigDirectory.mkdirs();
	}
	
	public void RegisterAllCommands() {
		getCommand("mhproblems").setTabCompleter(new MhProblemsCompleter());
		getCommand("mhhunter").setTabCompleter(new EmptyCompleterNoArgs());
		getCommand("mhnorole").setTabCompleter(new EmptyCompleterNoArgs());
		getCommand("gm").setTabCompleter(new MhGmCompleter());
		getCommand("mhstart").setTabCompleter(new MhStartCompleter());
		getCommand("mhreloadconfig").setTabCompleter(new EmptyCompleterNoArgs());
		getCommand("resetworld").setTabCompleter(new EmptyCompleterNoArgs());
	}
}