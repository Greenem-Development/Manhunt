package org.greenem.plugins.manhunt.commands;

import static org.greenem.plugins.manhunt.data.References.Frozen;
import static org.greenem.plugins.manhunt.data.References.FrozenLocs;
import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalFrom;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalTo;
import static org.greenem.plugins.manhunt.data.References.PortalEnd;
import static org.greenem.plugins.manhunt.data.References.PortalFrom;
import static org.greenem.plugins.manhunt.data.References.PortalTo;
import static org.greenem.plugins.manhunt.data.References.Speedrunners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.greenem.plugins.manhunt.config.ConfigData;
import org.greenem.plugins.manhunt.data.References;
import org.greenem.plugins.manhunt.main.Main;
import org.greenem.plugins.manhunt.util.Stash;

public class ManhuntCommandActions {
	public static void doNoRole(Player p, String pName) {
		if(Hunters.contains(pName)) {
			Hunters.remove(pName);
		}
		else if(Speedrunners.contains(pName)) {
			Speedrunners.remove(pName);
		}
		if(PortalEnd.containsKey(pName)) PortalEnd.remove(pName);
		if(PortalFrom.containsKey(pName)) PortalFrom.remove(pName);
		if(PortalTo.containsKey(pName)) PortalTo.remove(pName);
		if(HuntersPortalFrom.containsKey(pName)) HuntersPortalFrom.remove(pName);
		if(HuntersPortalTo.containsKey(pName)) HuntersPortalTo.remove(pName);
		Stash.RemoveMaterial(p, Material.COMPASS);
	}
	
	public static void mhStart(int delay) {
		mhStart(delay, ConfigData.speedrunnerDelayMilliseconds);
	}
	
	@Deprecated
	public static void mhStart(int delay, int speedrunnerDelay) { // TO REMAKE, THIS IS TOO BAD LOL
		int delayInSecondsInt = (int) ConfigData.speedrunnerDelayMilliseconds/1000;
		for(String speendrunner : Speedrunners) {
			Player spPl = Bukkit.getPlayerExact(speendrunner);
			if(spPl==null) continue;
			if(ConfigData.clearItemsOnStart) spPl.getInventory().clear();
			Frozen.add(speendrunner); // make it before and put value when back online TOD0
			FrozenLocs.put(spPl, spPl.getLocation());
			if(spPl.getGameMode().equals(GameMode.SURVIVAL) || spPl.getGameMode().equals(GameMode.ADVENTURE)) {
				if(!spPl.getGameMode().equals(GameMode.CREATIVE) && !spPl.getGameMode().equals(GameMode.SPECTATOR)) {
					spPl.setFlying(false);
					spPl.setAllowFlight(false);
				}
			}
			Bukkit.getPlayerExact(speendrunner).sendMessage(ChatColor.YELLOW + "You have " + delayInSecondsInt + " seconds to prepare");
		}
		for(String hunter: Hunters) {
			Player pHunter = Bukkit.getPlayerExact(hunter);
			if(pHunter==null) continue;
			if(ConfigData.clearItemsOnStart) pHunter.getInventory().clear();
			Frozen.add(hunter);
			FrozenLocs.put(pHunter, pHunter.getLocation());
			if(pHunter.getGameMode().equals(GameMode.SURVIVAL) || pHunter.getGameMode().equals(GameMode.ADVENTURE)) {
				if(!pHunter.getGameMode().equals(GameMode.CREATIVE) && !pHunter.getGameMode().equals(GameMode.SPECTATOR)) {
					pHunter.setFlying(false);
					pHunter.setAllowFlight(false);
				}
			}
			if(((int) ConfigData.speedrunnerDelayMilliseconds/1000)>0) {
				pHunter.sendMessage(ChatColor.GRAY + "The speedrunners have " + delayInSecondsInt + " seconds to prepare..");
				pHunter.sendTitle(ChatColor.GRAY + "Wait..", "", 5, 18*speedrunnerDelay/1000, 4);
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				for(String speendrunner : Speedrunners) {
					Frozen.remove(speendrunner);
					Player spPl = Bukkit.getPlayerExact(speendrunner);
					FrozenLocs.remove(spPl);
					if(spPl==null) continue;
					Bukkit.getPlayerExact(speendrunner).sendMessage(ChatColor.YELLOW + "Now run!");
					if(!spPl.getGameMode().equals(GameMode.CREATIVE) && !spPl.getGameMode().equals(GameMode.SPECTATOR)) {
						spPl.setFlying(false);
						spPl.setAllowFlight(false);
					}
				}
				for(String hunter: Hunters) {
					Player pHunter = Bukkit.getPlayerExact(hunter);
					//Frozen.add(hunter);
					final Player theHunter = pHunter;
//					if(theHunter==null) continue;
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
						@Override
						public void run() {
							Frozen.remove(theHunter.getName());
							FrozenLocs.remove(theHunter);
							if(!theHunter.getGameMode().equals(GameMode.CREATIVE) && !theHunter.getGameMode().equals(GameMode.SPECTATOR)) {
								theHunter.setFlying(false);
								theHunter.setAllowFlight(false);
							}
						}
					}, 20*delay);
					for (int i = delay; i>=0; i--) {
						final int iCopy = i;
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
							@Override
							public void run() {	
								if(iCopy>0) {
									if(iCopy==delay) {
										pHunter.sendTitle(ChatColor.GRAY + "The game starts in", ChatColor.GREEN + "" + iCopy, 5, 21, 0);
									}
									else {
										pHunter.sendTitle(ChatColor.GRAY + "The game starts in", ChatColor.GREEN + "" + iCopy, 0, 21, 0);
									}
								}
								else {
									pHunter.sendTitle(ChatColor.GREEN + "Go!", "", 5, 4, 7);
								}
							}
						}, 20*(delay-i));
					}
				}
			}
		}, 20 * speedrunnerDelay/1000);
	}
}
