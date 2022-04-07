package org.greenem.plugins.manhunt.completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class MhGmCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> arguments = new ArrayList<>();
		//sender.sendMessage(commandLabel);
		if(cmd.getName().equalsIgnoreCase("gm")) {
			if(args.length==1) {
				if(args[0].length()==0) {
					arguments.add("0");
					arguments.add("1");
					arguments.add("2");
					arguments.add("3");
				}
				return arguments;
			}
			else if(args.length==2) {
				if((Bukkit.getOnlinePlayers().size()==1 && sender instanceof Player) || sender instanceof ConsoleCommandSender) {
					for (Player thePlayer : Bukkit.getOnlinePlayers()) {
						arguments.add(thePlayer.getName());	
					}
				}
//				if(!(args[0].equals("0") || args[0].equals("1") || args[0].equals("2") || args[0].equals("3"))) {
//					arguments.add("0");
//					arguments.add("1");
//					arguments.add("2");
//					arguments.add("3");
//				}
				return arguments;
			}
			return arguments;
		}
		return null;
	}
	
}
