package org.greenem.plugins.manhunt.completers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MhRoleCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> arguments = new ArrayList<>();
		//sender.sendMessage(commandLabel);
		if(cmd.getName().equalsIgnoreCase("mhhunter") || cmd.getName().equalsIgnoreCase("mhspeedrunner")) { // ??
			if(args.length==1) {
				arguments.add("add");
				arguments.add("remove");
				arguments.add("add-remaining");
				arguments.add("remove-all");
				return arguments;
			}
			if(args.length==2) {
				if(args[0].equals("add") || args[0].equals("remove")) {
					return null;
				}
			}
		}
		else if(cmd.getName().equalsIgnoreCase("mhnorole")) {
			if(args.length==1) {
				arguments.add("apply");
				arguments.add("apply-to-remaining");
			}
			if(args.length==2) return null;
		}
		return arguments;
	}
}
