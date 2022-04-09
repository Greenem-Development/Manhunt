package org.greenem.plugins.manhunt.util;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.greenem.plugins.manhunt.data.References.Speedrunners;
import static org.greenem.plugins.manhunt.data.References.Hunters;

import org.greenem.plugins.manhunt.commands.ExtraCommands;

public class Stash {
	public static void AddItemStack(Player p, ItemStack ItemStack, int slot) { // can be better, change this later
		if(!p.getInventory().contains(ItemStack)) {
			if(p.getInventory().getItem(slot) == null) p.getInventory().setItem(slot, ItemStack);
			else p.getInventory().addItem(ItemStack);
		}
	}

	public static void RemoveMaterial(Player p, Material material) {
		if(p.getInventory().contains(material)) {
			p.getInventory().remove(material);
		}
	}
	
	public static void RemoveMaterial(Player p, int slot) {
		//p.getInventory().remove(p.getInventory().getItem(slot));
		p.getInventory().setItem(slot, new ItemStack(Material.AIR));
	}
	
	public static boolean isSenderAPlayer(CommandSender sender) {
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
	
	public static boolean isSenderAPlayerOrConsole(CommandSender sender) {
		if(!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
	
	public void healTests() {
		Speedrunners.add("Greenem");
		Hunters.add("MayX");
		//Main.Hunters.add("Dismov");
		ExtraCommands.HealCommand(null, null, "Greenem", new String[]{});
		log("-----");
		ExtraCommands.HealCommand(null, null, "Greenem", new String[]{"Greenem"});
		log("-----");
		ExtraCommands.HealCommand(null, null, "Greenem", new String[]{"MayX"});
		log("-----");
		ExtraCommands.HealCommand(null, null, "Greenem", new String[]{"Dismov"});
		log("----------------------------------------------");
		ExtraCommands.HealCommand(null, null, "MayX", new String[]{});
		log("-----");
		ExtraCommands.HealCommand(null, null, "MayX", new String[]{"MayX"});
		log("-----");
		ExtraCommands.HealCommand(null, null, "MayX", new String[]{"Greenem"});
		log("-----");
		ExtraCommands.HealCommand(null, null, "MayX", new String[]{"Dismov"});
		log("----------------------------------------------");
		ExtraCommands.HealCommand(null, null, "Dismov", new String[]{});
		log("-----");
		ExtraCommands.HealCommand(null, null, "Dismov", new String[]{"Dismov"});
		log("-----");
		ExtraCommands.HealCommand(null, null, "Dismov", new String[]{"Greenem"});
		log("-----");
		ExtraCommands.HealCommand(null, null, "Dismov", new String[]{"MayX"});
	}
}
