package org.greenem.plugins.manhunt.commands;

import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalFrom;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalTo;
import static org.greenem.plugins.manhunt.data.References.PortalEnd;
import static org.greenem.plugins.manhunt.data.References.PortalFrom;
import static org.greenem.plugins.manhunt.data.References.PortalTo;
import static org.greenem.plugins.manhunt.data.References.Speedrunners;
import static org.greenem.plugins.manhunt.util.Stash.isSenderAPlayerOrConsole;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.greenem.plugins.manhunt.compass.CompassUpdating;
import org.greenem.plugins.manhunt.config.ConfigOperations;
import org.greenem.plugins.manhunt.util.StandartUtils;
import org.greenem.plugins.manhunt.util.Stash;
import org.greenem.plugins.manhunt.worldManagement.WorldResetting;

public class ManhuntCommands {
	public static boolean hunterCommand(Player p) {
		String pName = p.getName();
		if(Speedrunners.contains(pName)) {
//			p.sendMessage(ChatColor.RED + "You are a speedrunner. Use /norole first");
//			return true;
			ManhuntCommandActions.doNoRole(p, pName);
		}
		if(!p.getWorld().getEnvironment().equals(Environment.NORMAL)) {
			p.sendMessage(ChatColor.RED + "You must be in the overworld to use this command");
			return true;
		}
		if(!Hunters.contains(pName)) {
			Hunters.add(pName);
			Stash.AddItemStack(p, new ItemStack(Material.COMPASS), 8);
			CompassUpdating.findTarget();
			p.sendMessage(ChatColor.GOLD + "Now you are a " + ChatColor.RED + "" + ChatColor.BOLD + "hunter");
		}
		else {
			p.sendMessage(ChatColor.RED + "You are already a hunter");
		}
		return true;
	}
	
	public static boolean speedrunnerCommand(Player p) {
		String pName = p.getName();
		if(Hunters.contains(pName)) {
//			p.sendMessage(ChatColor.RED + "You are a hunter. Use /norole");
//			return true;
			ManhuntCommandActions.doNoRole(p, pName);
		}
		if(!p.getWorld().getEnvironment().equals(Environment.NORMAL)) {
			p.sendMessage(ChatColor.RED + "You must be in the overworld to use this command. (There would be problems with missing portal location. Later this will be avaliable)");
			return true;
		}
		if(!Speedrunners.contains(pName)) {
			Speedrunners.add(pName);
			p.sendMessage(ChatColor.GOLD + "Now you are a speedrunner");
		}
		else {
			p.sendMessage(ChatColor.RED + "You are already a " + ChatColor.GREEN + "" + ChatColor.BOLD + "speedrunner");
		}
		return true;
	}
	
	public static boolean noRoleCommand(Player p) {
		String pName = p.getName();
		ManhuntCommandActions.doNoRole(p, pName);
		p.sendMessage(ChatColor.GOLD + "You have removed your role");
		return true;
	}
	
	public static boolean startGameCommand(Player p, String[] args) {
		if(args.length<1) {
			p.sendMessage(ChatColor.RED + "Insert the delay in seconds. /mhstart <seconds>");
			return true;
		}
		if(args.length==1) {
			if(StandartUtils.isInteger(args[0])) {
				int totalPlayers = Speedrunners.size()+Hunters.size();
				if(totalPlayers==0) {
					p.sendMessage(ChatColor.RED + "You didn't add a single hunter or speedrunner!");
					return true;
				}
				if(Speedrunners.size()==0) {
					p.sendMessage(ChatColor.RED + "You didn't add a single speedrunner!");
					return true;
				}
				if(Hunters.size()==0) {
					p.sendMessage(ChatColor.YELLOW + "You are starting the game against NO HUNTERS"); //. If that's a mistake, do /mhstop // add later
				}
				ManhuntCommandActions.mhStart(Integer.parseInt(args[0]));
			}
			else {
				p.sendMessage(ChatColor.RED + "Use a number as the argument");
				return true;
			}
		}
		if(args.length>1) {
			p.sendMessage(ChatColor.RED + "Incorrect arguments. /mhstart <seconds>");
			return true;
		}
		return true;
	}
	
	public static boolean stopGameCommand(Player p, String[] args) {
		p.sendMessage(ChatColor.RED + "This command is not coded yet");
		return true;
	}
	
	public static boolean problemsCommand(Player p, String[] args) {
		if(!Hunters.contains(p.getName())) {
			p.sendMessage(ChatColor.RED + "You must be a hunter to use this command!");
			return true; // ???
		}
		if(args.length==1) {
			if(args[0].equals("give_compass")) {
				if(p.getInventory().contains(Material.COMPASS)) {
					p.sendMessage(ChatColor.RED + "You already have a compass!");
					return true;
				}
				Stash.AddItemStack(p, new ItemStack(Material.COMPASS), 8);
				p.sendMessage(ChatColor.GREEN + "You been given a compass");
			}
		}
		else {
			p.sendMessage(ChatColor.RED + "Wrong arguments");
		}
		return true;
	}
	
	public static boolean reloadConfigCommand(CommandSender sender) {
		ConfigOperations.loadConfig();
		sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config");
		return true;
	}
	
	public static boolean resetWorldCommand(CommandSender sender) {
		WorldResetting.resetWorld(sender);
		return true;
	}
}
