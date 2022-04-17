package org.greenem.plugins.manhunt.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AbstractCommand {
	public CommandActionType type;
	public AdditionalCommandActionType additionalType;
	
	public GameMode gm;
	
	public CommandSender sender;
	public String targetName;
	
	public String senderName;
	public String senderDecorName;
	public Player senderPlayer;
	public Player targetPlayer;
	
	public void completeInitialisation() {
		if(targetName==null) {
			targetPlayer = Bukkit.getPlayerExact(targetName);
		}
		if(senderPlayer==null) {
			senderPlayer = Bukkit.getPlayerExact(senderName);
		}
		if(sender!=null) {
			senderName = sender.getName();
			if(sender instanceof ConsoleCommandSender) {
				senderDecorName = "[CONSOLE]";
			} else {
				senderDecorName = senderName;
				if(sender instanceof Player) {
					senderPlayer = (Player) sender;
				}
			}
		}
		if(targetName==null) {
			targetPlayer = senderPlayer; 
		}
		if(targetPlayer!=null && targetName==null) {
			targetName = targetPlayer.getName();
		}
	}
}
