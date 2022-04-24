package org.greenem.plugins.manhunt.completers;

import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.Speedrunners;
import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
				String c = args[0].toLowerCase();
				if(c.equals("add") || c.equals("remove")) {
					if(Speedrunners.size()==0 && Hunters.size()==0) {
						if(c.equals("add")) {
							return null;
						} else { // "remove"
							return arguments;
						}
					}
					for (Player p : Bukkit.getOnlinePlayers()) {
						String pName = p.getName();
						if((c.equals("add") && (cmd.getName().equalsIgnoreCase("mhhunter") && !Hunters.contains(pName)) || (cmd.getName().equalsIgnoreCase("mhspeedrunner") && !Speedrunners.contains(pName))) ||
						c.equals("remove") && (cmd.getName().equalsIgnoreCase("mhhunter") && Hunters.contains(pName)) || (cmd.getName().equalsIgnoreCase("mhspeedrunner") && Speedrunners.contains(pName))) {
							arguments.add(pName);
						}
					}
					for (String s : Speedrunners) {
						if(!arguments.contains(s) && (c.equals("add") && (!Speedrunners.contains(s))) || (c.equals("remove") && (Speedrunners.contains(s)))) {
							arguments.add(s);
						}
					}
					for (String s : Hunters) {
						if(!arguments.contains(s) && (c.equals("add") && (!Hunters.contains(s))) || (c.equals("remove") && (Hunters.contains(s)))) {
							arguments.add(s);
						}
					}
					return arguments;
				}
			}
		}
		else if(cmd.getName().equalsIgnoreCase("mhnorole")) {
			if(args.length==1) {
				arguments.add("apply");
				arguments.add("apply-to-remaining");
			}
			if(args.length==2) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					String pName = p.getName();
					if(Speedrunners.contains(pName) || Hunters.contains(pName)) {
						arguments.add(pName);
					}
				}
				for (String s : Speedrunners) {
					if(!arguments.contains(s) && (Speedrunners.contains(s) || Hunters.contains(s))) {
						arguments.add(s);
					}
				}
				for (String s : Hunters) {
					if(!arguments.contains(s) && (Speedrunners.contains(s) || Hunters.contains(s))) {
						arguments.add(s);
					}
				}
				return arguments;
			}
		}
		return arguments;
	}
}
