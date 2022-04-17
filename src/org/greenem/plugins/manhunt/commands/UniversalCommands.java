package org.greenem.plugins.manhunt.commands;

import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.Speedrunners;
import static org.greenem.plugins.manhunt.util.SimpleLogger.log;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.greenem.plugins.manhunt.compass.CompassUpdating;
import org.greenem.plugins.manhunt.util.Stash;

public class UniversalCommands {
	public static void doAction(AbstractCommand ac) {
		// Player from, Player to, CommandActionType action, GameMode gm
		String msgFromNotNormalEnv = ChatColor.RED + "The player must be in the overworld for you to use this command. (There would be problems with missing portal location. Later this will be avaliable)";
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
					ac.targetPlayer.sendMessage(ChatColor.GOLD + ac.senderPlayer.getName() + " has added you to the hunters list");
				}
				ac.targetPlayer.sendMessage(ChatColor.GOLD + "Now you are a " + ChatColor.RESET + "" + ChatColor.RED + "" + ChatColor.BOLD + "hunter");
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
			if(Hunters.contains(pName)) {
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
			if(!Speedrunners.contains(pName)) {
				Speedrunners.add(pName);
				ac.targetPlayer.getInventory().remove(Material.COMPASS);
				CompassUpdating.findTarget();
				if(!ac.targetPlayer.equals(ac.senderPlayer)) {
					ac.targetPlayer.sendMessage(ChatColor.GOLD + ac.senderPlayer.getName() + " has added you to the speedrunners list");
				}
				ac.targetPlayer.sendMessage(ChatColor.GOLD + "Now you are a " + ChatColor.RESET + "" + ChatColor.GREEN + "" + ChatColor.BOLD + "speedrunner");
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
	
	public static String getNotifToThatOne(AbstractCommand ac, String colorNameFrom) {
		switch (ac.type) {
		case HEAL: {
			return ChatColor.GRAY + "You have been healed by " + colorNameFrom;
		}
		case GM: {
			return ChatColor.GRAY + "Your gamemode has been set to " + getGamemodeName(ac.gm) + " by " + colorNameFrom;
		}
		case MAKE_HUNTER: {
			return ChatColor.GRAY + "You have been added to hunters team by " + colorNameFrom;
		}
		case MAKE_SPEEDRUNNER: {
			return ChatColor.GRAY + "You have been added to speedrunners team by " + colorNameFrom;
		}
		case MAKE_NOROLE: {
			return ChatColor.GRAY + "You have been removed from your team by " + colorNameFrom;
		}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + ac.type);
		}
	}
	
	public static void universalCommand(AbstractCommand ac) {
		ac.targetPlayer = Bukkit.getPlayerExact(ac.targetName);
		if(ac.targetPlayer==null) {
			ac.targetPlayer = Bukkit.getPlayerExact(ac.sender.getName());
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
		if(ac.additionalType == AdditionalCommandActionType.SELF) {
			if(ac.sender instanceof ConsoleCommandSender) {
				ac.sender.sendMessage(ChatColor.RED + "You can't apply the result to yourself as the console!");
				return;
			}
			doAction(ac);
			notifpersonal = ChatColor.GRAY + "You have been healed"; // TODO
			notifOthers = colorNameFrom + ChatColor.RESET + "" + ChatColor.GRAY + " was healed"; // TODO
			if(ac.targetPlayer!=null) {
				ac.targetPlayer.sendMessage(notifpersonal);
			}
			if(ExtraCommands.logHealInfo) log("notifpersonal: " + notifpersonal);
			if(Hunters.contains(ac.targetName) || Speedrunners.contains(ac.targetName)) {
				if(ExtraCommands.logHealInfo) log("notifOthers: " + notifOthers);
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
				boolean atLeastOneImportantPlayer = Hunters.contains(ac.senderName) || Speedrunners.contains(ac.senderName) || Hunters.contains(ac.targetName) || Speedrunners.contains(ac.targetName);
				if(ac.targetPlayer!=null) {
					doAction(ac);
				}
				
				if(ac.targetName.equals(ac.senderName)) {
					notifpersonal = ChatColor.GRAY + "You have been healed"; // TODO
					if(ac.senderPlayer!=null) {
						ac.senderPlayer.sendMessage(notifpersonal);
					}
					notifOthers = colorNameFrom + ChatColor.RESET + "" + ChatColor.GRAY + " was healed"; // TODO
				}
				else {
					String notifToThatOne = getNotifToThatOne(ac, colorNameFrom);
					notifpersonal = ChatColor.GRAY + "You have healed " + colorNameTo; // TODO
					if(ac.targetPlayer!=null) {
						ac.targetPlayer.sendMessage(notifToThatOne);
						if(ExtraCommands.logHealInfo) log("notifToThatOne: " + notifToThatOne);
					}
					if(ac.senderPlayer!=null) {
						ac.senderPlayer.sendMessage(notifpersonal);
					}
					notifOthers = coloredHealSentence(colorNameFrom, colorNameTo, ac);
				}
				
				if(ExtraCommands.logHealInfo) log("notifpersonal: " + notifpersonal);
				
				if(atLeastOneImportantPlayer) {
					if(ExtraCommands.logHealInfo) log("notifOthers: " + notifOthers);
					for (String pAlert : Hunters) {
						if (!pAlert.equals(ac.targetName)) {
							sendOthersTheAlert(pAlert, ac.senderDecorName, notifOthers);
						}
					}
					for (String pAlert : Speedrunners) {
						if (!pAlert.equals(ac.targetName)) {
							sendOthersTheAlert(pAlert, ac.senderDecorName, notifOthers);
						}
					}
				}
			}
			else {
				String problem = ChatColor.RED + "Such player isn't online";
				if(ExtraCommands.logHealInfo) log("personal error to " + ac.senderDecorName + ": " + problem);
				if(ac.senderPlayer!=null) {
					ac.senderPlayer.sendMessage(problem);
				}
			}
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
		else if(Hunters.contains(to)) {
			color1 = ChatColor.RED + "" + ChatColor.BOLD;
		}
		else if(Speedrunners.contains(to)) {
			color1 = ChatColor.GREEN + "" + ChatColor.BOLD;
		}
		else {
			color1 = ChatColor.WHITE + "" + ChatColor.BOLD;
		}
		
		String part1 = color1 + to + ChatColor.RESET;
		
		String color2 = "";
		if(to!=null && !from.equals(to)) {
			if(from.equals("[CONSOLE]")) {
				color2 = ChatColor.GOLD + "" + ChatColor.BOLD;
			}
			if(Hunters.contains(from)) {
				color2 = ChatColor.RED + "" + ChatColor.BOLD;
			}
			else if(Speedrunners.contains(from)) {
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
				s = part1 + " has set own gamemode to " + getGamemodeName(ac.gm) + " mode";
			}
			else {
				s = part1 + "'s gamemode was set to " + getGamemodeName(ac.gm) + " by " + part2;
			}
			break;
		}
		case HEAL: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + " was healed";
			}
			else {
				s = part1 + " was healed by " + part2;
			}
			break;
		}
		case MAKE_HUNTER: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + " joined the hunters team";
			}
			else {
				s = part1 + " was added to the hunters team by " + part2;
			}
			break;
		}
		case MAKE_SPEEDRUNNER: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + " joined the speedrunners team";
			}
			else {
				s = part1 + " was added to the speedrunners team by " + part2;
			}
			break;
		}
		case MAKE_NOROLE: {
			if (ac.additionalType == AdditionalCommandActionType.SELF) {
				s = part1 + " has left their team";
			}
			else {
				s = part1 + " was removed from their team by " + part2;
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + ac.type );
		}
		
		s += ChatColor.RESET + " was healed";
		s += " by ";
		
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
