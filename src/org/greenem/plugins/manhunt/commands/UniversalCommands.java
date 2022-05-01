package org.greenem.plugins.manhunt.commands;

import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.Speedrunners;
import static org.greenem.plugins.manhunt.util.SimpleLogger.log;

import java.util.ArrayList;
import java.util.List;

import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.Acceleration;
import org.greenem.plugins.manhunt.compass.CompassUpdating;
import org.greenem.plugins.manhunt.data.References;
import org.greenem.plugins.manhunt.main.Main;
import org.greenem.plugins.manhunt.util.Stash;

public class UniversalCommands {
	public static void doAction(AbstractCommand ac) {
		// Player from, Player to, CommandActionType action, GameMode gm
		String msgFromNotNormalEnv = ChatColor.RED + "The player must be in the overworld for you to use this command. (There would be problems with missing portal location. Later this will be avaliable) Requested to apply to " + ac.targetName;
		if(ac.type==CommandActionType.GM) {
			ac.targetPlayer.setGameMode(ac.gm);
		} else if(ac.type==CommandActionType.HEAL) {
			ac.targetPlayer.setHealth(20);
			ac.targetPlayer.setFoodLevel(20);
//			p.setSaturation(20); // ???
		} else if(ac.type==CommandActionType.MAKE_HUNTER) {
			if(!ac.targetPlayer.getWorld().getEnvironment().equals(Environment.NORMAL) && !ac.targetPlayer.equals(ac.senderPlayer)) {
				ac.senderPlayer.sendMessage(msgFromNotNormalEnv);
				return;
			}
			String pName = ac.targetPlayer.getName();
			if(pName==null) {
				pName = ac.targetName;
			}
			if(Speedrunners.contains(pName)) {
				ManhuntCommandActions.doNoRole(ac.targetPlayer, pName);
			}
			if(!ac.targetPlayer.getWorld().getEnvironment().equals(Environment.NORMAL)) {
				if(ac.targetPlayer.equals(ac.senderPlayer)) {
					ac.targetPlayer.sendMessage(ChatColor.RED + "You must be in the overworld to use this command. (There would be problems with missing portal location. Later this will be avaliable)");
				} else {
					ac.targetPlayer.sendMessage(ChatColor.RED + "You must be in the overworld to get the role. (" + ac.senderPlayer.getName() + " tried to change it for you)");
					ac.senderPlayer.sendMessage(msgFromNotNormalEnv);
				}
				return;
			}
			if(!Hunters.contains(pName)) {
				Hunters.add(pName);
				ac.targetPlayer.getInventory().remove(Material.COMPASS);
				Stash.AddItemStack(ac.targetPlayer, new ItemStack(Material.COMPASS), 8);
				CompassUpdating.findTarget();
				if(!ac.targetPlayer.equals(ac.senderPlayer)) {
//					ac.targetPlayer.sendMessage(ChatColor.GOLD + ac.senderPlayer.getName() + " has added you to the hunters list");
				}
				Bukkit.getScheduler().runTaskLater(Main.getInstance().plugin, new Runnable() {
					public void run() {
						ac.targetPlayer.sendMessage(ChatColor.WHITE + "Now you are a " + ChatColor.RESET + "" + ChatColor.RED + "" + ChatColor.BOLD + "hunter");
					}
				}, 2);
			}
			else {
				if(ac.targetPlayer.equals(ac.senderPlayer)) {
					ac.targetPlayer.sendMessage(ChatColor.RED + "You are already a hunter");
				} else {
					ac.targetPlayer.sendMessage(ChatColor.RED + "The player is already a hunter");
				}
			}
		} else if(ac.type==CommandActionType.MAKE_SPEEDRUNNER) { // LINE LINE LINE LINE
			if(!ac.targetPlayer.getWorld().getEnvironment().equals(Environment.NORMAL) && !ac.targetPlayer.equals(ac.senderPlayer)) {
				ac.senderPlayer.sendMessage(msgFromNotNormalEnv);
				return;
			}
			String pName = ac.targetPlayer.getName();
			if(pName==null) {
				pName = ac.targetName;
			}
			if(Hunters.contains(pName)) {
				ManhuntCommandActions.doNoRole(ac.targetPlayer, pName);
			}
			if(!ac.targetPlayer.getWorld().getEnvironment().equals(Environment.NORMAL)) {
				if(ac.targetPlayer.equals(ac.senderPlayer)) {
					ac.targetPlayer.sendMessage(ChatColor.RED + "You must be in the overworld to use this command. (There would be problems with missing portal location. Later this will be avaliable)");
				} else {
					ac.targetPlayer.sendMessage(ChatColor.RED + "You must be in the overworld to get the role. (" + ac.sender.getName() + " tried to change it for you)");
					ac.senderPlayer.sendMessage(msgFromNotNormalEnv);
				}
				return;
			}
			if(!Speedrunners.contains(pName)) {
				Speedrunners.add(pName);
				ac.targetPlayer.getInventory().remove(Material.COMPASS);
				CompassUpdating.findTarget();
				if(!ac.targetPlayer.equals(ac.senderPlayer)) {
//					ac.targetPlayer.sendMessage(ChatColor.GOLD + ac.senderPlayer.getName() + " has added you to the speedrunners list");
				}
				Bukkit.getScheduler().runTaskLater(Main.getInstance().plugin, new Runnable() {
					public void run() {
						ac.targetPlayer.sendMessage(ChatColor.WHITE + "Now you are a " + ChatColor.RESET + "" + ChatColor.GREEN + "" + ChatColor.BOLD + "speedrunner");
					}
				}, 2);
			}
			else {
				if(ac.targetPlayer.equals(ac.senderPlayer)) {
					ac.targetPlayer.sendMessage(ChatColor.RED + "You are already a speedrunner");
				} else {
					ac.targetPlayer.sendMessage(ChatColor.RED + "The player is already a speedrunner");
				}
			}
		} else if(ac.type==CommandActionType.MAKE_NOROLE) { // LINE LINE LINE LINE
			ManhuntCommandActions.doNoRole(ac.targetPlayer, ac.targetName);
		}
	}
	
	public static boolean stringsNotSameAndNotNull(String s1, String s2) {
		if(s1!=null && s2!=null && !s1.equals(s2)) return true;
		else return false;
	}
	
	public static String getNotifToThatOne(AbstractCommand ac, String colorNameFrom) {
		return getNotifToThatOne(ac, colorNameFrom, false);
	}
	
	public static String getNotifToThatOne(AbstractCommand ac, String colorNameFrom, boolean wrongDimension) {
		switch (ac.type) {
		case HEAL: {
			if(!stringsNotSameAndNotNull(ac.senderName, ac.targetName)) return null;
			if(colorNameFrom!=null) return ChatColor.GRAY + "You have been healed by " + colorNameFrom;
			else return ChatColor.GRAY + "You have been healed";
		}
		case GM: {
			if(!stringsNotSameAndNotNull(ac.senderName, ac.targetName)) return null;
			if(colorNameFrom!=null) return ChatColor.GRAY + "Your gamemode has been set to " + getGamemodeName(ac.gm) + " by " + colorNameFrom;
			else return ChatColor.GRAY + "Your gamemode has been set to " + getGamemodeName(ac.gm);
		}
		case MAKE_HUNTER: {
			if(!stringsNotSameAndNotNull(ac.senderName, ac.targetName)) return null;
			if(colorNameFrom!=null) {
				String message = wrongEnvMessage(ac);
				if(message==null) {
					message = ChatColor.GRAY + "You have been added to hunters team by " + colorNameFrom;
				}
				return message;
			}
			else return null;
		}
		case MAKE_SPEEDRUNNER: {
			if(!stringsNotSameAndNotNull(ac.senderName, ac.targetName)) return null;
			if(colorNameFrom!=null) {
				String message = wrongEnvMessage(ac);
				if(message==null) {
					message = ChatColor.GRAY + "You have been added to speedrunners team by " + colorNameFrom;
				}
				return message;
			}
			else return null;
		}
		case MAKE_NOROLE: {
			if(stringsNotSameAndNotNull(ac.senderName, ac.targetName)) return ChatColor.GRAY + "You have been removed from your team by " + colorNameFrom; // else
			else return ChatColor.GRAY + "You have left your team";
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + ac.type);
		}
	}
	
	public static String wrongEnvMessage(AbstractCommand ac) {
		if(!ac.targetPlayer.getWorld().getEnvironment().equals(Environment.NORMAL)) {
			if(ac.targetPlayer.equals(ac.senderPlayer)) {
				return ChatColor.RED + "You must be in the overworld to use this command. (There would be problems with missing portal location. Later this will be avaliable)";
			} else {
				return ChatColor.RED + "You must be in the overworld to get the role. (" + ac.senderPlayer.getName() + " tried to change it for you)";
			}
		}
		else return null;
	}
	
	public static String getNotifToTheSender(AbstractCommand ac, String NameTo) {
		switch (ac.type) {
		case HEAL: {
			if(NameTo!=null) return ChatColor.GRAY + "You have healed " + coloredName(NameTo);
			else return null;
		}
		case GM: {
			if(NameTo!=null) return ChatColor.GRAY + "You have set " + coloredName(NameTo) + "'s" + ChatColor.GRAY + " gamemode to " + getGamemodeName(ac.gm);
			else return null;
		}
		case MAKE_HUNTER: {
			if(NameTo!=null) return ChatColor.GRAY + "You have added " + ChatColor.RED + ChatColor.BOLD + NameTo + ChatColor.GRAY + " to hunters team";
			else return null;
		}
		case MAKE_SPEEDRUNNER: {
			if(NameTo!=null) return ChatColor.GRAY + "You have added " + ChatColor.GREEN + ChatColor.BOLD + NameTo + ChatColor.GRAY + " to speedrunners team";
			else return null;
		}
		case MAKE_NOROLE: {
			String c = whiteBald();
			if(ac.removeFrom!=null && ac.removeFrom.equals("h")) c = red();
			if(ac.removeFrom!=null && ac.removeFrom.equals("s")) c = green();
			if(NameTo!=null) {
				return ChatColor.GRAY + "You have removed " + c + NameTo + ChatColor.GRAY + " from their team";
			}
			else return ChatColor.GRAY + "You have left your team";
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + ac.type);
		}
	}
	
	public static String whiteBald() {
		return ChatColor.RESET + "" + ChatColor.WHITE + ChatColor.BOLD;
	}
	
	public static String white() {
		return ChatColor.RESET + "" + ChatColor.WHITE;
	}
	
	public static String red() {
		return ChatColor.RESET + "" + ChatColor.RED + ChatColor.BOLD;
	}
	
	public static String green() {
		return ChatColor.RESET + "" + ChatColor.GREEN + ChatColor.BOLD;
	}
	
	public static String getNotifToTheOthers(AbstractCommand ac, String NameTo) {
		switch (ac.type) {
		case HEAL: {
			if(!ac.senderName.equals(ac.targetName)) return coloredName(NameTo) + white() + " was healed by " + ac.senderDecorName; //TODO colored console
			else return coloredName(NameTo) + white() + " was healed ";
		}
		case GM: {
			if(!ac.senderName.equals(ac.targetName)) return coloredName(ac.senderDecorName) + white() + " has set " + coloredName(NameTo) + "'s" + white() + " gamemode to " + ChatColor.GRAY + "" + ChatColor.BOLD + getGamemodeName(ac.gm);
			else return coloredName(ac.senderDecorName) + white() + " has set own gamemode to " + ChatColor.GRAY + ChatColor.BOLD + getGamemodeName(ac.gm);
		}
		case MAKE_HUNTER: {
			if(!ac.senderName.equals(ac.targetName)) return coloredName(ac.senderDecorName) + white() + " has added " + ChatColor.RED + "" + ChatColor.BOLD + NameTo + white() + " to hunters team";
			else return ChatColor.RED + "" + ChatColor.BOLD + ac.senderDecorName + white() + " has joined the hunters team";
		}
		case MAKE_SPEEDRUNNER: {
			if(!ac.senderName.equals(ac.targetName)) return coloredName(ac.senderDecorName) + white() + " has added " + ChatColor.GREEN + "" + ChatColor.BOLD + NameTo + " to speedrunners team";
			else return ChatColor.GREEN + "" + ChatColor.BOLD + ac.senderDecorName + white() + " has joined the speedrunners team";
		}
		case MAKE_NOROLE: {
			if(!ac.senderName.equals(ac.targetName)) return coloredName(ac.senderDecorName) + white() + " has removed " + coloredName(NameTo) + " from their team";
			else return coloredName(ac.senderDecorName) + white() + " has left their team";
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + ac.type);
		}
	}
	
	public static void universalCommand(AbstractCommand ac, List<String> newMessageListeners) {
		debug(ac.targetPlayer + " " + ac.type + " " + ac.additionalType);
		if(ac.targetPlayer==null && !(ac.sender instanceof ConsoleCommandSender && (ac.additionalType==AdditionalCommandActionType.REMAINING_PLAYERS || ac.additionalType==AdditionalCommandActionType.ALL_PLAYERS))) {
			ac.sender.sendMessage(ChatColor.RED + "Can't find such player!");
			return;
		}
		if(!(ac.sender instanceof ConsoleCommandSender) && ac.type==CommandActionType.MAKE_NOROLE && ac.targetName!=null && !Speedrunners.contains(ac.targetName) && !Hunters.contains(ac.targetName)) {
			if(ac.additionalType==null) {
				debug(ChatColor.RED + "addition type is null, maybe command was not initialized correctly");
			}
			if(ac.additionalType==AdditionalCommandActionType.SELF || (ac.additionalType==AdditionalCommandActionType.OTHER_PLAYER && ac.senderName.equals(ac.targetName))) ac.sender.sendMessage(ChatColor.RED + "You have no role already");
			else if(ac.additionalType==AdditionalCommandActionType.OTHER_PLAYER) ac.sender.sendMessage(ChatColor.RED + "The player doesn't have a role already");
			else if(ac.additionalType==AdditionalCommandActionType.REMAINING_PLAYERS || ac.additionalType==AdditionalCommandActionType.ALL_PLAYERS) ac.sender.sendMessage(ChatColor.RED + "No one has a role already");
			return;
		}
		Runnable finalActions = null;
		if(ac.targetName!=null) ac.targetPlayer = Bukkit.getPlayerExact(ac.targetName); // dublicate?
		if(ac.targetPlayer==null) {
			debug("targetPlayer null " + ac.type + " " + ac.additionalType);
			if(ac.additionalType!=AdditionalCommandActionType.OTHER_PLAYER) {
				ac.targetPlayer = Bukkit.getPlayerExact(ac.sender.getName());
			}
		}
//		if(!ConfigData.allowHealCommandForOps) { // later and not here
//			if(sender!=null) {
//				sender.sendMessage(ChatColor.RED + "This command was disabled in the config");
//			}
//			return;
//		}
		
		String colorNameFrom = coloredName(ac.senderDecorName);
		String notifpersonal;
		String notifOthers;
		debug(ac.additionalType);
		debug(ac.removeFrom);
		if(ac.additionalType == AdditionalCommandActionType.SELF) {
			if(ac.sender instanceof ConsoleCommandSender) {
				ac.sender.sendMessage(ChatColor.RED + "You can't apply the result to yourself as the console!");
				return;
			}
			finalActions = new Runnable() {
				@Override
				public void run() {
					doAction(ac);
				}
			};
			notifpersonal = getNotifToThatOne(ac, ac.targetName); // TODO was null
			notifOthers = getNotifToTheOthers(ac, ac.targetName);
			debug(notifOthers);
			if(ac.targetPlayer!=null) {
				if(notifpersonal!=null) ac.targetPlayer.sendMessage(notifpersonal);
			}
			if(ExtraCommands.logHealInfo) debug("notifpersonal1: " + notifpersonal);
			if(Hunters.contains(ac.targetName) || Speedrunners.contains(ac.targetName)
					|| ac.type==CommandActionType.MAKE_HUNTER || ac.type==CommandActionType.MAKE_SPEEDRUNNER || ac.type==CommandActionType.MAKE_NOROLE) {
				if(ExtraCommands.logHealInfo) debug("notifOthers: " + notifOthers);
				for (String pAlert : Hunters) {
					sendOthersTheAlert(pAlert, ac.targetName, notifOthers);
				}
				for (String pAlert : Speedrunners) {
					sendOthersTheAlert(pAlert, ac.targetName, notifOthers);
				}
			}
		}
		else if (ac.additionalType == AdditionalCommandActionType.OTHER_PLAYER) {
			if(ac.targetPlayer!=null || ExtraCommands.logHealInfo) {
				String colorNameTo = coloredName(ac.targetName);
				boolean atLeastOneImportantPlayer = Hunters.contains(ac.senderName) || Speedrunners.contains(ac.senderName) || Hunters.contains(ac.targetName) || Speedrunners.contains(ac.targetName)
						|| ac.type==CommandActionType.MAKE_HUNTER || ac.type==CommandActionType.MAKE_SPEEDRUNNER || ac.type==CommandActionType.MAKE_NOROLE;
				if(ac.targetPlayer!=null) {
					finalActions = new Runnable() {
						@Override
						public void run() {
							doAction(ac);
						}
					};
				}
				
//				if(ac.targetPlayer)
				
				if(ac.targetName.equals(ac.senderName)) {
					notifpersonal = getNotifToThatOne(ac, colorNameFrom);
					if(ac.senderPlayer!=null) {
						Bukkit.getScheduler().runTaskLater(Main.getInstance().plugin, new Runnable() {
							public void run() {
									if(notifpersonal!=null && ac.senderPlayer!=null && ac.senderPlayer.getWorld().getEnvironment()!=Environment.THE_END) ac.senderPlayer.sendMessage(notifpersonal);
							}
						}, 1);
					}
					notifOthers = getNotifToTheOthers(ac, ac.targetName); //getNotifToTheSender(ac, ac.targetName);
					debug("OTTHERS; " + notifOthers);
				}
				else {
					String notifToThatOne = getNotifToThatOne(ac, colorNameFrom);
					notifpersonal = getNotifToTheSender(ac, ac.targetName);
					if(ac.targetPlayer!=null) {
						Bukkit.getScheduler().runTaskLater(Main.getInstance().plugin, new Runnable() {
							public void run() {
								if(notifpersonal!=null && ac.targetPlayer!=null) {
									if (ac.targetPlayer.getWorld().getEnvironment()==Environment.NORMAL) { 
										ac.targetPlayer.sendMessage(notifToThatOne);
									} else {
										ac.targetPlayer.sendMessage(notifToThatOne); // TODO WTF
									}
								}
							}
						}, 1);
						if(ExtraCommands.logHealInfo) debug("notifToThatOne: " + notifToThatOne);
					}
					if(ac.senderPlayer!=null) {
						if(notifpersonal!=null) {
							if(ac.targetPlayer!=null) {
								if(ac.targetPlayer.getWorld().getEnvironment()==Environment.NORMAL) { 
									ac.senderPlayer.sendMessage(notifpersonal);
								} else {
									
								}
							}
						}
					}
					notifOthers = coloredHealSentence(colorNameFrom, colorNameTo, ac);
				}
				
				if(ExtraCommands.logHealInfo) debug("notifpersonal2: " + notifpersonal);
				
				if(atLeastOneImportantPlayer) {
					if(ExtraCommands.logHealInfo) debug("notifOthers: " + notifOthers);
					List<String> applyTo = new ArrayList<>();
					if(newMessageListeners!=null) {
						applyTo.addAll(newMessageListeners);
					}
							for (String pAlert : Hunters) {
								if (!pAlert.equals(ac.targetName)) {
									sendOthersTheAlert(pAlert, ac.senderDecorName, notifOthers);
									applyTo.remove(pAlert);
								}
							}
							for (String pAlert : Speedrunners) {
								if (!pAlert.equals(ac.targetName)) {
									sendOthersTheAlert(pAlert, ac.senderDecorName, notifOthers);
									applyTo.remove(pAlert);
								}
							}
							for (String pAlert : applyTo) {
								if (!pAlert.equals(ac.targetName)) {
									sendOthersTheAlert(pAlert, ac.senderDecorName, notifOthers);
								}
							}
				}
			}
			else {
				String problem = ChatColor.RED + "Such player isn't online";
				if(ExtraCommands.logHealInfo) debug("personal error to " + ac.senderDecorName + ": " + problem);
				if(ac.senderPlayer!=null) {
					ac.senderPlayer.sendMessage(problem);
				}
			}
		}
		else if (ac.additionalType == AdditionalCommandActionType.ALL_PLAYERS) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if(ac.removeFrom!=null && ac.removeFrom.equals("s") && Speedrunners.contains(p.getName())) {
					ac.targetsList.add(p.getName());
				} else if(ac.removeFrom!=null && ac.removeFrom.equals("h") && Hunters.contains(p.getName())) {
					ac.targetsList.add(p.getName());
				} else if(ac.removeFrom!=null && ac.removeFrom.equals("") && (Hunters.contains(p.getName()) || Speedrunners.contains(p.getName()))) {
					ac.targetsList.add(p.getName());
				}
			}
			if(ac.targetsList.size()==0) {
				ac.sender.sendMessage(ChatColor.RED + "There are no targets to apply the action on");
				return;
			}
			recurcy(ac);
		}
		else if (ac.additionalType == AdditionalCommandActionType.REMAINING_PLAYERS) {
			if(ac.type==CommandActionType.MAKE_HUNTER) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if(!Hunters.contains(p.getName()) && !Speedrunners.contains(p.getName())) ac.targetsList.add(p.getName());
				}
			}
			else if(ac.type==CommandActionType.MAKE_SPEEDRUNNER) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if(!Hunters.contains(p.getName()) && !Speedrunners.contains(p.getName())) ac.targetsList.add(p.getName());
				}
			}
			else if(ac.type==CommandActionType.MAKE_NOROLE) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if(Hunters.contains(p.getName()) || Speedrunners.contains(p.getName())) ac.targetsList.add(p.getName());
				}
			}
			if(ac.targetsList.size()==0) {
				ac.sender.sendMessage(ChatColor.RED + "There are no targets to apply the action on");
				return;
			}
			recurcy(ac);
		}
		if(finalActions!=null) finalActions.run();
	}
	
	public static void recurcy(AbstractCommand ac) {
		for (String s : ac.targetsList) {
			AbstractCommand ac2 = new AbstractCommand();
			ac2.type = ac.type;
			ac2.sender = ac.sender;
			if(s.equals(ac.senderName)) ac2.additionalType = AdditionalCommandActionType.SELF;
			else ac2.additionalType = AdditionalCommandActionType.OTHER_PLAYER;
			ac2.targetName = s;
			if(ac2.targetName!=null && Hunters.contains(ac2.targetName)) ac2.removeFrom = "h";
			if(ac2.targetName!=null && Speedrunners.contains(ac2.targetName)) ac2.removeFrom = "s";
			ac2.completeInitialisation();
			debug(ac2.targetName);
			UniversalCommands.universalCommand(ac2, ac.targetsList);
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
	
	public static String coloredHealSentence(String from, String to, AbstractCommand ac) {
		String s = "";
		
		String color1 = "";
		if(to.equals("[CONSOLE]")) {
			color1 = ChatColor.GOLD + "" + ChatColor.BOLD;
		}
		else if(Hunters.contains(to) || ac.type==CommandActionType.MAKE_HUNTER || (ac.removeFrom!=null && ac.removeFrom.equals("h"))) {
			color1 = ChatColor.RED + "" + ChatColor.BOLD;
		}
		else if(Speedrunners.contains(to) || ac.type==CommandActionType.MAKE_SPEEDRUNNER || (ac.removeFrom!=null && ac.removeFrom.equals("s"))) { // TODO You have removed GREEN3M2 from their team WHITE TEXT BUT NOT RED
			color1 = ChatColor.GREEN + "" + ChatColor.BOLD;
		}
		else {
			color1 = ChatColor.WHITE + "" + ChatColor.BOLD;
		}
		
		String part1 = color1 + to;
		
		String color2 = "";
		if(to!=null && !from.equals(to)) {
			if(from.equals("[CONSOLE]")) {
				color2 = ChatColor.GOLD + "" + ChatColor.BOLD;
			}
			if(Hunters.contains(from) || (ac.removeFrom!=null && ac.removeFrom.equals("h"))) {
				color2 = ChatColor.RED + "" + ChatColor.BOLD;
			}
			else if(Speedrunners.contains(from) || (ac.removeFrom!=null && ac.removeFrom.equals("s"))) {
				color2 = ChatColor.GREEN + "" + ChatColor.BOLD;
			}
			else {
				color2 = ChatColor.WHITE + "" + ChatColor.BOLD;
			}
		}
		
		String part2 = color2 + from + ChatColor.RESET;
		
		switch (ac.type) {
		case GM: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + ChatColor.RESET + " has set own gamemode to " + getGamemodeName(ac.gm) + " mode";
			}
			else {
				s = part1 + "'s" + ChatColor.RESET + " gamemode was set to " + getGamemodeName(ac.gm) + " by " + part2;
			}
			break;
		}
		case HEAL: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + ChatColor.RESET + " was healed";
			}
			else {
				s = part1 + ChatColor.RESET + " was healed by " + part2;
			}
			break;
		}
		case MAKE_HUNTER: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + ChatColor.RESET + " joined the hunters team";
			}
			else {
				s = part1 + ChatColor.RESET + " was added to the hunters team by " + part2;
			}
			break;
		}
		case MAKE_SPEEDRUNNER: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + ChatColor.RESET + " joined the speedrunners team";
			}
			else {
				s = part1 + ChatColor.RESET + " was added to the speedrunners team by " + part2;
			}
			break;
		}
		case MAKE_NOROLE: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + ChatColor.RESET + " has left their team";
			}
			else {
				s = part1 + ChatColor.RESET + " was removed from their team by " + part2;
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + ac.type );
		}
		
//		s += ChatColor.RESET + " was healed";
//		s += " by ";
		
		return s;
	}
	
	public static String getGamemodeName(GameMode gm) {
		switch (gm) {
		case SURVIVAL: return "Survival";
		case CREATIVE: return "Creative";
		case ADVENTURE: return "Adventure";
		case SPECTATOR: return "Spectator";
		default:
			throw new IllegalArgumentException("Unexpected value: " + gm);
		}
	}
	
	public static void sendOthersTheAlert(String pAlert, String pName, String notifOthers) {
		if(!pAlert.equals(pName)) {
			Player pAlertP = Bukkit.getPlayerExact(pAlert);
			if(pAlertP!=null) {
				pAlertP.sendMessage(notifOthers);
			}
		}
	}
}
