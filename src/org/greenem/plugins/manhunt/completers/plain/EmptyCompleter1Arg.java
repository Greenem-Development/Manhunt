package org.greenem.plugins.manhunt.completers.plain;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class EmptyCompleter1Arg implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> arguments = new ArrayList<>();
		if(args.length==2) {
			return arguments;
		}
		return null;
	}
	
}
