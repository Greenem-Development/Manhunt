package org.greenem.plugins.manhunt.commands;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;

import java.util.ArrayList;
import java.util.List;

import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;

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
	
	public List<String> targetsList = new ArrayList<>();
	public String removeFrom = null;
	
	public boolean lessInfoLotsOfPlayers = false;
	
	public void completeInitialisation() {
		if(targetName!=null) {
			targetPlayer = Bukkit.getPlayerExact(targetName);
			debug("first target name");
		}
		if(sender!=null) {
			senderName = sender.getName();
			if(sender instanceof ConsoleCommandSender) {
				senderDecorName = "[CONSOLE]";
			} else {
				senderDecorName = senderName;
				if(sender instanceof Player) {
					senderPlayer = (Player) sender;
					debug("sender instanceof Player");
				}
			}
		}
		if(senderPlayer==null) {
			senderPlayer = Bukkit.getPlayerExact(senderName);
			debug("get exact");
		}
		if(targetPlayer==null) {
			targetPlayer = senderPlayer; 
		}
		if(targetPlayer!=null && targetName==null) {
			targetName = targetPlayer.getName();
		}
	}
}
