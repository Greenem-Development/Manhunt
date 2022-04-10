package org.greenem.plugins.manhunt.main;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.manhunt.data.References.Speedrunners;
import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.PortalTo;
import static org.greenem.plugins.manhunt.data.References.PortalFrom;
import static org.greenem.plugins.manhunt.data.References.PortalEnd;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalFrom;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalTo;
import static org.greenem.plugins.manhunt.data.References.Frozen;
import static org.greenem.plugins.manhunt.data.References.FrozenLocs;
import static org.greenem.plugins.manhunt.data.References.serverDirectory;
import static org.greenem.plugins.manhunt.data.References.configsDirectory;
import static org.greenem.plugins.manhunt.data.References.pluginConfigDirectory;

import static org.greenem.plugins.manhunt.util.Stash.isSenderAPlayer;
import static org.greenem.plugins.manhunt.util.Stash.isSenderAPlayerOrConsole;

import java.io.File;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.greenem.plugins.manhunt.commands.ExtraCommands;
import org.greenem.plugins.manhunt.commands.ManhuntCommands;
import org.greenem.plugins.manhunt.completers.MhGmCompleter;
import org.greenem.plugins.manhunt.completers.MhStartCompleter;
import org.greenem.plugins.manhunt.completers.plain.EmptyCompleterNoArgs;
import org.greenem.plugins.manhunt.config.ConfigOperations;
import org.greenem.plugins.manhunt.data.References;
import org.greenem.plugins.manhunt.events.FrozenEvents;
import org.greenem.plugins.manhunt.events.ManhuntEvents;
import org.greenem.plugins.manhunt.util.OsDependent;
import org.greenem.plugins.manhunt.util.SimpleLogger;
//import org.spigotmc.event.entity.EntityDismountEvent;
import org.greenem.plugins.manhunt.completers.MhProblemsCompleter;

public class Main extends JavaPlugin implements Listener {
	public Plugin plugin = this;

	private static Main instance;
	public static Main getInstance() { // not synchronized, but that's ok
		return instance;
	}
	
	@Override
	public void onEnable() {
		log(ChatColor.GREEN + "Your Manhunt plugin has been enabled!");
		initAll();
		ConfigOperations.updateAndLoadConfig();
		finishInit();
		instance = this;
		OsDependent.getSystemType();
	}
	
	@Override
	public void onDisable() {
		log("Your Manhunt plugin has been disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = null;
//		String name = sender.getName();
		try {
			p = (Player) sender;
		} catch (ClassCastException e) {}
		
		if(cmd.getName().equalsIgnoreCase("gm")){
			if(isSenderAPlayerOrConsole(sender)) {
				ExtraCommands.GmCommand(sender, args);
			}
		}
		else if(cmd.getName().equalsIgnoreCase("heal")){
			if(isSenderAPlayerOrConsole(sender)) {
				ExtraCommands.HealCommand(sender, p, null, args);
			}
		}
		if(cmd.getName().equalsIgnoreCase("mhhunter")){
			if(!isSenderAPlayer(sender)) return true;
			ManhuntCommands.hunterCommand(p);
		}
		else if(cmd.getName().equalsIgnoreCase("mhspeedrunner")){
			if(!isSenderAPlayer(sender)) return true;
			ManhuntCommands.speedrunnerCommand(p);
		}
		else if(cmd.getName().equalsIgnoreCase("mhnorole")){
			if(!isSenderAPlayer(sender)) return true;
			ManhuntCommands.noRoleCommand(p);
		}
		else if(cmd.getName().equalsIgnoreCase("mhstart")){
			if(!isSenderAPlayer(sender)) return true;
			ManhuntCommands.startGameCommand(p, args);
		}
		else if(cmd.getName().equalsIgnoreCase("mhstop")){
			if(!isSenderAPlayerOrConsole(sender)) return true;
			ManhuntCommands.stopGameCommand(p, args);
		}
		else if(cmd.getName().equalsIgnoreCase("mhproblems")){
			if(!isSenderAPlayer(sender)) return true;
			ManhuntCommands.problemsCommand(p, args);
		}
		else if (cmd.getName().equalsIgnoreCase("resetworld")) {
			if(!isSenderAPlayerOrConsole(sender)) return true;
			ManhuntCommands.resetWorldCommand(sender);
		}
		else if (cmd.getName().equalsIgnoreCase("mhreloadconfig")) {
			if(!isSenderAPlayerOrConsole(sender)) return true;
			ManhuntCommands.reloadConfigCommand(sender);
		}
		return true; //false??? //TODO
	}
    
	public static void sendActionBar(Player player, String message){
//        CraftPlayer p = (CraftPlayer) player;
		
        //IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        //PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, null, null);
        //player.send
        //((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
    }
	
	private void initAll() {
		References.os = OsDependent.getSystemType();
		registerListeners(); // not sure if that should be done so early
		RegisterAllCommands();
		initFiles();
	}
	
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new FrozenEvents(), this);
		Bukkit.getPluginManager().registerEvents(new ManhuntEvents(), this);
	}
	
	private void RegisterAllCommands() {
		getCommand("mhproblems").setTabCompleter(new MhProblemsCompleter());
		getCommand("mhhunter").setTabCompleter(new EmptyCompleterNoArgs());
		getCommand("mhnorole").setTabCompleter(new EmptyCompleterNoArgs());
		getCommand("gm").setTabCompleter(new MhGmCompleter());
		getCommand("mhstart").setTabCompleter(new MhStartCompleter());
		getCommand("mhreloadconfig").setTabCompleter(new EmptyCompleterNoArgs());
		getCommand("resetworld").setTabCompleter(new EmptyCompleterNoArgs());
	}
	
	private void initFiles() {
		serverDirectory = Bukkit.getWorldContainer();
		configsDirectory = new File(serverDirectory, "plugins/configs");
		pluginConfigDirectory = new File(configsDirectory, "ManhuntAndWorldReset");
		pluginConfigDirectory.mkdirs();
	}
	
	private void finishInit() {
		if(SimpleLogger.debugOutputEnabled) {
			log(ChatColor.AQUA + "Debug mode is enabled");
		}
	}
}