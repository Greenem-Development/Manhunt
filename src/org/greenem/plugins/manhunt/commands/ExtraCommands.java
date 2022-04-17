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
import org.greenem.plugins.manhunt.config.ConfigData;
import org.greenem.plugins.manhunt.util.Stash;

public class ExtraCommands {
	public static boolean logHealInfo = false; // false
	
	public static void GmCommand(CommandSender sender, String[] args) { // maybe boolean? (false not tested)
		if(!ConfigData.allowGmCommandForOps) {
			sender.sendMessage(ChatColor.RED + "This command was disabled in the config");
			return;
		}
		Player p = Bukkit.getPlayerExact(sender.getName());
		if(sender instanceof ConsoleCommandSender || p.isOp() == true){
			if(args.length == 0){
				sender.sendMessage(ChatColor.RED + "Please, insert gamemode number");
			}
			else if(args.length == 1){
				if(sender instanceof ConsoleCommandSender) {
					sender.sendMessage(ChatColor.RED + "You can only change the players' gamemodes from console!");
					return;
				}
				int value = -1;
				try {
					value = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "You should specify a number there");
					return;
				}
				if(value==0) { // 00?
					p.setGameMode(GameMode.SURVIVAL);
				}
				else if(value==1) {
					p.setGameMode(GameMode.CREATIVE);
				}
				else if(value==2) {
					p.setGameMode(GameMode.ADVENTURE);
				}
				else if(value==3) {
					p.setGameMode(GameMode.SPECTATOR);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Please, insert correct gamemode number");
				}
			}
			else if(args.length == 2){
				int value = -1;
				try {
					value = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.RED + "You should specify a number there");
					return;
				}
				if(Bukkit.getPlayerExact(args[1])!=null){	
					if(value==0) {
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.SURVIVAL);
					}
					else if(value==1) {
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.CREATIVE);
					}
					else if(value==2) {
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.ADVENTURE);
					}
					else if(value==3) {
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.SPECTATOR);
					}
					else {
						sender.sendMessage(ChatColor.RED + "Please, insert correct gamemode number");
					}
				}
				else {
					value = -1;
					try {
						value = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						
					}
					if(value==0 || value==1 || value==2 || value==3) {
						sender.sendMessage(ChatColor.RED + "Usage for other players: /gm <number 0-3> <player>");
					}
					else {
						sender.sendMessage(ChatColor.RED + "Such player isn't online");
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Please, type the command in such order: /gm <number 0-3> <player>");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "You can't use this command");
		}
	}
	
	public static boolean HealCommand(CommandSender sender, Player p, String pName, String[] args) {
		boolean console = sender instanceof ConsoleCommandSender;
		if(!ConfigData.allowHealCommandForOps) {
			if(p!=null) {
				p.sendMessage(ChatColor.RED + "This command was disabled in the config");
			}
			return false;
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
		String colorNameFrom = UniversalCommands.coloredName(pFrom);
		String notifOthers;
		if(args.length == 0) {
			if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage(ChatColor.RED + "You can't heal yourself as the console!");
				return false;
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
					UniversalCommands.sendOthersTheAlert(pAlert, pName, notifOthers);
				}
				for (String pAlert : Speedrunners) {
					UniversalCommands.sendOthersTheAlert(pAlert, pName, notifOthers);
				}
			}
		}
		else if(args.length==1) {
			String pTo = args[0];
			Player pToP = Bukkit.getPlayerExact(pTo);
			if(pToP!=null || logHealInfo) {
				String colorNameTo = UniversalCommands.coloredName(pTo);
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
					notifOthers = UniversalCommands.coloredHealSentence(colorNameFrom, colorNameTo);
				}
				
				if(logHealInfo) log("notifpersonal: " + notifpersonal);
				
				if(atLeastOneImportantPlayer) {
					if(logHealInfo) log("notifOthers: " + notifOthers);
					for (String pAlert : Hunters) {
						if (!pAlert.equals(pTo)) {
							UniversalCommands.sendOthersTheAlert(pAlert, pName, notifOthers);
						}
					}
					for (String pAlert : Speedrunners) {
						if (!pAlert.equals(pTo)) {
							UniversalCommands.sendOthersTheAlert(pAlert, pName, notifOthers);
						}
					}
				}
			}
			else {
				String problem = ChatColor.RED + "Such player isn't online";
				if(logHealInfo) log("personal error to " + pName + ": " + problem);
				if(p!=null) {
					p.sendMessage(problem);
				}
			}
		}
		else {
			String problem = ChatColor.RED + "Please, type the command in such order: /heal <player>";
			log("personal error to " + pName + ": " + problem);
			if(p!=null) {
				p.sendMessage(problem);
			}
		}
		return true;
	}
}
