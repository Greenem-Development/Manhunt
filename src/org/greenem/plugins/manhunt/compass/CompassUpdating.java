package org.greenem.plugins.manhunt.compass;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalFrom;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalTo;
import static org.greenem.plugins.manhunt.data.References.PortalEnd;
import static org.greenem.plugins.manhunt.data.References.PortalFrom;
import static org.greenem.plugins.manhunt.data.References.PortalTo;
import static org.greenem.plugins.manhunt.data.References.Speedrunners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class CompassUpdating {
	public static void findTarget() {
		for (String hunter : Hunters) {
			Player plHunter = Bukkit.getPlayerExact(hunter);
			if(plHunter==null) {
				continue;
			}
			if(plHunter.isDead()==false) { //TODO java.lang.NullPointerException: Cannot invoke "org.bukkit.entity.Player.isDead()" because "plHunter" is null, 274
				Map <String, Double> Distations = new HashMap<>();
				for (String speedrunner : Speedrunners) {
					Player speedrunnerPl = Bukkit.getPlayerExact(speedrunner);
					if(speedrunnerPl==null) {
						continue;
					}
					if(speedrunnerPl.isDead()==false) { //TODO java.lang.NullPointerException: Cannot invoke "org.bukkit.entity.Player.isDead()" because "speedrunnerPl" is null
						double distance = GetDistance(plHunter, speedrunnerPl);
						if(distance<0) {
							log("skipped.");
							return;
						}
						Distations.put(speedrunner, distance);
					}
				}
				//log.info(Distations.toString());
				double nearest = -1;
				int times = 0;
				for (double dist : Distations.values()) {
					if(times == 0) {
						nearest = dist;
						times++;
					}
					else {
						if(nearest>dist) {
							nearest = dist;
						}
					}
				}
				for (String sp : Speedrunners) {
					Player plSp = Bukkit.getPlayerExact(sp);
					if(Bukkit.getPlayerExact(sp).isDead()==false) {
						if(Distations.get(sp).equals(nearest)) {
							Location target = GetTargetLocation(plHunter, plSp);
							if(target.getWorld().getEnvironment().equals(Environment.NETHER) || target.getWorld().getEnvironment().equals(Environment.THE_END)) {
								for(int i = 0; i < plHunter.getInventory().getSize(); i++) {
							   		ItemStack CompassItem = plHunter.getInventory().getItem(i);
							      	if(CompassItem != null) {
							    	  	if(CompassItem.getType().equals(Material.COMPASS)) {
								    	  	if(plHunter.getInventory().getItem(plHunter.getInventory().getHeldItemSlot())!=null && plHunter.getInventory().getItem(plHunter.getInventory().getHeldItemSlot()).getType().equals(Material.COMPASS)) {
								    	  	
								    	  	}
								    	  	else if (plHunter.getWorld().getEnvironment()!=plSp.getWorld().getEnvironment()) {
								    	  		// because there was a bug
								    	  	}
								    	  	else {
								    	  		CompassMeta meta = (CompassMeta) CompassItem.getItemMeta();
								    	  		if(meta.getLodestone() == null) meta.setLodestoneTracked(false); //если нужно только
								    	  		meta.setLodestone(target);
								                CompassItem.setItemMeta(meta);
								    	  	}
							        	}
							      	}
								}
							}
							if(target.getWorld().getEnvironment().equals(Environment.NORMAL)) {
								boolean toggle1 = false;
								for(int i = 0; i < plHunter.getInventory().getSize(); i++) {
							   		ItemStack CompassItem = plHunter.getInventory().getItem(i);
							      	if(CompassItem != null) {
							    	  	if(CompassItem.getType().equals(Material.COMPASS)) {
							    	  		toggle1 = true;
							    	  		//log.info("has: " + CompassItem.getItemMeta().toString());
							    	  		CompassMeta meta = (CompassMeta) CompassItem.getItemMeta();
							    	  		CompassItem.setItemMeta(meta);
							    	  		if(meta.getLodestone()!=null) {
							    	  			//int slot = i;
							    	  			plHunter.getInventory().setItem(i, new ItemStack(Material.COMPASS, CompassItem.getAmount()));
		//					    	  			plHunter.setCompassTarget(target);
							    	  		}
							    	  		else {
		//					    	  			plHunter.setCompassTarget(target);
							    	  		}
							        	}
							      	}
								}
								if(toggle1) {
									plHunter.setCompassTarget(target);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static double GetDistance (Player hunter, Player speedrunner) {
		String nameH = hunter.getName();
		String nameS = speedrunner.getName();
		double result = -1;
		if(hunter.getWorld().equals(speedrunner.getWorld())) {
			result = 0;
			result = hunter.getLocation().distance(speedrunner.getLocation());
		}
		else {
			if(speedrunner.getWorld().getEnvironment().equals(Environment.THE_END)) {
				result = 0;
				result+=hunter.getLocation().distance(PortalEnd.get(nameS));
			}
			else {
				if(PortalFrom.containsKey(nameS)) {
					result = 0;
					//log.info("from: " + PortalFrom.get(speedrunner).toString());
					//log.info("to: " + PortalTo.get(speedrunner).toString());
					if(hunter.getWorld().getEnvironment().equals(PortalFrom.get(nameS).getWorld().getEnvironment()) && speedrunner.getWorld().getEnvironment().equals(PortalTo.get(nameS).getWorld().getEnvironment())) {
						result += hunter.getLocation().distance(PortalFrom.get(nameS));
						result += speedrunner.getLocation().distance(PortalTo.get(nameS));
					}
				}
				else {
					if(HuntersPortalTo.containsKey(nameH) && HuntersPortalFrom.containsKey(nameH)) {
						//log.info(HuntersPortalTo.containsKey(nameH) + "");
						//log.info(HuntersPortalFrom.containsKey(nameH) + "");
						//if(PortalFrom.containsKey(nameS)) log.info(hunter.getWorld().getEnvironment().equals(PortalFrom.get(nameS).getWorld().getEnvironment()) + "");
						//if(PortalTo.containsKey(nameS)) log.info(speedrunner.getWorld().getEnvironment().equals(PortalTo.get(nameS).getWorld().getEnvironment()) + "");
						result = 0;
						if(!HuntersPortalTo.get(nameH).getWorld().getEnvironment().equals(hunter.getWorld().getEnvironment())) {
							log("skipped1");
							return -1;
						}
						result += hunter.getLocation().distance(HuntersPortalTo.get(nameH));
						if(!HuntersPortalFrom.get(nameH).getWorld().getEnvironment().equals(speedrunner.getWorld().getEnvironment())) {
							log("skipped2");
							return -1;
						}
						result += speedrunner.getLocation().distance(HuntersPortalFrom.get(nameH));
						//Bukkit.broadcastMessage(ChatColor.RED + "Almost a error, speedrunner got into another dimension without plugin seeing this.");
					}
					else {
						Bukkit.broadcastMessage(ChatColor.RED + "ERROR! A hunter got into another dimension without plugin seeing this.");
					}
				}
			}
		}
		return result;
	}
	
	public static Location GetTargetLocation (Player hunter, Player speedrunner) {
		String nameH = hunter.getName();
		String nameS = speedrunner.getName();
		Location result = hunter.getLocation();
		if(hunter.getWorld().equals(speedrunner.getWorld())) {
			result = speedrunner.getLocation();
			//log.info("same world");
		}
		else {
			if(speedrunner.getWorld().getEnvironment().equals(Environment.THE_END)) {
				result = PortalEnd.get(nameS);
			}
			else {
				if(PortalFrom.containsKey(nameS)) {
					result = PortalFrom.get(nameS);
				}
				else {
					if(HuntersPortalTo.containsKey(nameH)) {
						result = HuntersPortalTo.get(nameH);
					}
					else {
						log(ChatColor.RED + "Error 0001");
					}
				}
			}
		}
		return result;
	}
}
