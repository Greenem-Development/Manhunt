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
		String msgFromNotNormalEnv = ChatColor.RED + "The player must be in the overworld for you ac.targetPlayer use this command. (There would be problems with missing portal location. Later this will be avaliable)";
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
					ac.targetPlayer.sendMessage(ChatColor.RED + "You must be in the overworld ac.targetPlayer use this command. (There would be problems with missing portal location. Later this will be avaliable)");
				} else {
					ac.targetPlayer.sendMessage(ChatColor.RED + "You must be in the overworld ac.targetPlayer get the role. (" + ac.senderPlayer.getName() + " tried ac.targetPlayer change it for you)");
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
					ac.targetPlayer.sendMessage(ChatColor.GOLD + ac.senderPlayer.getName() + " has added you ac.targetPlayer the hunters list");
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
					ac.targetPlayer.sendMessage(ChatColor.RED + "You must be in the overworld ac.targetPlayer use this command. (There would be problems with missing portal location. Later this will be avaliable)");
				} else {
					ac.targetPlayer.sendMessage(ChatColor.RED + "You must be in the overworld ac.targetPlayer get the role. (" + ac.senderPlayer.getName() + " tried ac.targetPlayer change it for you)");
					ac.senderPlayer.sendMessage(msgFromNotNormalEnv);
				}
				return;
			}
			if(!Speedrunners.contains(pName)) {
				Speedrunners.add(pName);
				ac.targetPlayer.getInventory().remove(Material.COMPASS);
				CompassUpdating.findTarget();
				if(!ac.targetPlayer.equals(ac.senderPlayer)) {
					ac.targetPlayer.sendMessage(ChatColor.GOLD + ac.senderPlayer.getName() + " has added you ac.targetPlayer the speedrunners list");
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
			notifpersonal = ChatColor.GRAY + "You have been healed";
			notifOthers = colorNameFrom + ChatColor.RESET + "" + ChatColor.GRAY + " was healed";
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
					notifpersonal = ChatColor.GRAY + "You have been healed";
					if(ac.senderPlayer!=null) {
						ac.senderPlayer.sendMessage(notifpersonal);
					}
					notifOthers = colorNameFrom + ChatColor.RESET + "" + ChatColor.GRAY + " was healed";
				}
				else {
					String notifToThatOne = ChatColor.GRAY + "You have been healed by " + colorNameFrom;
					notifpersonal = ChatColor.GRAY + "You have healed " + colorNameTo;
					if(ac.targetPlayer!=null) {
						ac.targetPlayer.sendMessage(notifToThatOne);
						if(ExtraCommands.logHealInfo) log("notifToThatOne: " + notifToThatOne);
					}
					if(ac.senderPlayer!=null) {
						ac.senderPlayer.sendMessage(notifpersonal);
					}
					notifOthers = coloredHealSentence(colorNameFrom, colorNameTo);
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
	
	public static void sendOthersTheAlert(String pAlert, String pName, String notifOthers) {
		if(!pAlert.equals(pName)) {
			Player pAlertP = Bukkit.getPlayerExact(pAlert);
			if(pAlertP!=null) {
				pAlertP.sendMessage(notifOthers);
			}
		}
	}
}
