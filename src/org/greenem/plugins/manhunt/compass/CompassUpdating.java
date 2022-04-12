package org.greenem.plugins.manhunt.compass;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalFrom;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalTo;
import static org.greenem.plugins.manhunt.data.References.PortalEndFrom;
import static org.greenem.plugins.manhunt.data.References.PortalEndTo;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalEndFrom;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalEndTo;
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
import org.greenem.plugins.manhunt.classes.PairStringDouble;
import org.greenem.plugins.manhunt.data.References;
import org.greenem.plugins.manhunt.main.Main;

public class CompassUpdating {
	public static void findTarget() {
		findTarget(null, null);
	}
	
	public static void findTarget(String playerUpdated, Location theLoc) {
		for (String hunter : Hunters) {
			Player h = Bukkit.getPlayerExact(hunter);
			if(h==null || h.isDead()) continue;
			updateCompass(h, hunter, playerUpdated, theLoc);
		}
	}
	
	public static void updateCompass(Player h, String hunter, String playerUpdated, Location theLoc) {
		String speedrunner = getNearestSpeedrunner(h, hunter, playerUpdated, theLoc);
		if(speedrunner==null) return;
		Player s = Bukkit.getPlayerExact(speedrunner);
		if(s==null) {
			debug("Speedrunner-target is null");
			return;
		}
		Location target = GetTargetLocation(h, s, playerUpdated, theLoc);
//		debug(shortLocation(target));
		
		if(target==null) return; // TODO error logging with no spamming
		
//		if(getEnvironment(target)!=getEnvironment(h)) {
//			// a bad bug fix lol
//			return;
//		}
		
		if(target.getWorld().getEnvironment()==Environment.NORMAL) {
			boolean hasCompass = false;
			for(int i = 0; i < h.getInventory().getSize(); i++) {
		   		ItemStack currentItem = h.getInventory().getItem(i);
		      	if(currentItem!=null && currentItem.getType().equals(Material.COMPASS)) {
		      		hasCompass = true;
	    	  		CompassMeta meta = (CompassMeta) currentItem.getItemMeta();
			      	//log("meta: " + CompassItem.getItemMeta().toString());
	    	  		if(meta.getLodestone()!=null) {
//	    	  			meta.setLodestone(null);
////	    	  			meta.setLodestone(target);
//	    	  			currentItem.setItemMeta(meta);
	    	  			h.getInventory().setItem(i, new ItemStack(Material.COMPASS, currentItem.getAmount()));
//	    	  			debug("DISABLING LODESTONE, target: " + shortLocation(target));
	    	  		}
		      	}
			}
			if(hasCompass) {
//				debug("setting normal compass target");
				if(playerUpdated!=null) {
					Bukkit.getScheduler().runTaskLater(Main.getInstance().plugin, new Runnable() {
						@Override
						public void run() {
							h.setCompassTarget(target);
						}
					}, 1);
				} else {
					h.setCompassTarget(target);
				}
			}
		}
		else {
			boolean isHoldingCompass = isPlayerHoldingACompass(h);
//			debug("1");
			for(int i = 0; i < h.getInventory().getSize(); i++) {
		   		ItemStack currentItem = h.getInventory().getItem(i);
//		   		debug("2");
		      	if(currentItem!=null && currentItem.getType().equals(Material.COMPASS) && !isHoldingCompass) {
//		    	  	if (h.getWorld().getEnvironment()!=s.getWorld().getEnvironment()) {
//		    	  		// because there was a bug
//		    	  	}
//		    	  	else {
		      		CompassMeta meta = (CompassMeta) currentItem.getItemMeta();
	    	  		if(meta.getLodestone() == null) {
	    	  			meta.setLodestoneTracked(false); // do I need to update it every time
	    	  		}
	    	  		meta.setLodestone(target);
	                currentItem.setItemMeta(meta);
//	                debug("ENABLED LODESTONE");
//		    	  	}
		      	}
			}
		}
	}
	
	public static String shortLocation(Location l) {
		return l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ();
	}
	
	public static boolean isPlayerHoldingACompass(Player p) {
		return (p.getInventory().getItemInMainHand()!=null && p.getInventory().getItemInMainHand().getType()==Material.COMPASS) ||
				(p.getInventory().getItemInOffHand()!=null && p.getInventory().getItemInOffHand().getType()==Material.COMPASS);
	}
	
	public static String getNearestSpeedrunner(Player h, String hunter, String playerUpdated, Location theLoc) {
		Map <String, Double> distances = getAllDistances(h, hunter, playerUpdated, theLoc);
		PairStringDouble best = new PairStringDouble();
		
		boolean firstValue = true;
		for (String sp : distances.keySet()) {
			double value = distances.get(sp);
			if(firstValue) {
				best.setDouble(value);
				best.setString(sp);
				firstValue = false;
			}
			else {
				if(best.getDouble() > value) {
					best.setDouble(value);
					best.setString(sp);
				}
			}
		}
		return best.getString();
	}
	
	public static Map<String, Double> getAllDistances(Player h, String hunter, String playerUpdated, Location theLoc) {
		Map <String, Double> distances = new HashMap<>();
		for (String speedrunner : Speedrunners) {
			Player s = Bukkit.getPlayerExact(speedrunner);
			if(s==null || s.isDead()) continue;
			double distance = GetDistance(h, s, playerUpdated, theLoc);
			if(distance<0) {
				debug("distance < 0, h = " + hunter + ", s = " + speedrunner);
				continue;
			}
			distances.put(speedrunner, distance);
		}
		return distances;
	}
	
	@Deprecated
	public static void findTarget_() {
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
						double distance = GetDistance(plHunter, speedrunnerPl, null, null);
						if(distance<0) {
							log("skipped");
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
							Location target = GetTargetLocation(plHunter, plSp, null, null);
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
	
	public static double GetDistance (Player hunter, Player speedrunner, String playerUpdated, Location theLoc) {
		String nameH = hunter.getName();
		String nameS = speedrunner.getName();
		
		Location spLocation = null;
		if(speedrunner.getName().equals(playerUpdated)) spLocation = theLoc;
		if(spLocation==null) spLocation = speedrunner.getLocation();
		Location hLocation = null;
		if(hunter.getName().equals(playerUpdated)) hLocation = theLoc;
		if(hLocation==null) hLocation = hunter.getLocation();
		
		double result = -1;
		if(hLocation.getWorld().equals(spLocation.getWorld())) {
			result = 0;
			result = hLocation.distance(spLocation);
		}
		else {
			if(getEnvironment(spLocation)==Environment.THE_END && getEnvironment(spLocation)==Environment.NORMAL) {
				result = 0;
				result+=hLocation.distance(PortalEndFrom.get(nameS));
			}
			else if(getEnvironment(hLocation)==Environment.THE_END && !PortalEndTo.containsKey(nameS) ||
					getEnvironment(hLocation)==Environment.THE_END && PortalEndTo.containsKey(nameS) && getEnvironment(spLocation)!=Environment.THE_END) {
				result = 0;
				if(HuntersPortalEndTo.containsKey(nameH)) {
					result+=hLocation.distance(HuntersPortalEndTo.get(nameH));
				} else {
					log("Error code: 0002");
				}
			}
			else {
				if(PortalFrom.containsKey(nameS)) {
					result = 0;
					if(hLocation.getWorld().getEnvironment().equals(PortalFrom.get(nameS).getWorld().getEnvironment()) && spLocation.getWorld().getEnvironment().equals(PortalTo.get(nameS).getWorld().getEnvironment())) {
						result += hLocation.distance(PortalFrom.get(nameS));
						result += spLocation.distance(PortalTo.get(nameS));
					}
				}
				else {
					if(HuntersPortalTo.containsKey(nameH) && HuntersPortalFrom.containsKey(nameH)) {
						result = 0;
						if(!HuntersPortalTo.get(nameH).getWorld().getEnvironment().equals(hLocation.getWorld().getEnvironment())) {
//							result += spLocation.distance(HuntersPortalFrom.get(nameH));
							debug("HuntersPortalFrom: " + HuntersPortalFrom);
							debug("HuntersPortalTo: " + HuntersPortalTo);
							debug("hLocation: " + hLocation);
							debug("skipped1");
							return -1;
						}
						result += hLocation.distance(HuntersPortalTo.get(nameH));
						if(!HuntersPortalFrom.get(nameH).getWorld().getEnvironment().equals(spLocation.getWorld().getEnvironment())) {
							debug("skipped2");
							return -1;
						}
						result += spLocation.distance(HuntersPortalFrom.get(nameH));
						//Bukkit.broadcastMessage(ChatColor.RED + "Almost a error, speedrunner got into another dimension without plugin seeing this");
					}
					else {
						Bukkit.broadcastMessage(ChatColor.RED + "ERROR! A hunter got into another dimension without plugin seeing this");
					}
				}
			}
		}
		return result;
	}
	
	public static Location GetTargetLocation (Player hunter, Player speedrunner, String playerUpdated, Location theLoc) {
		String h = hunter.getName();
		String s = speedrunner.getName();
		
		Location spLocation = null;
		if(speedrunner.getName().equals(playerUpdated)) spLocation = theLoc;
		if(spLocation==null) spLocation = speedrunner.getLocation();
		Location hLocation = null;
		if(hunter.getName().equals(playerUpdated)) hLocation = theLoc;
		if(hLocation==null) hLocation = hunter.getLocation();
		
		Location result = hLocation; // like a placehodler
		if(hLocation.getWorld().equals(spLocation.getWorld())) {
			result = spLocation;
		}
		else {
			if(getEnvironment(spLocation)==Environment.THE_END && getEnvironment(hLocation)==Environment.NORMAL) {
				result = PortalEndFrom.get(s);
			}
			else if(getEnvironment(spLocation)!=Environment.THE_END && getEnvironment(hLocation)==Environment.THE_END) {
				result = PortalEndTo.get(s);
//				debug(4);
				if(result==null) {
					result = HuntersPortalEndTo.get(h);
//					debug(5);
				}
				if(result==null) {
//					debug(6);
				}
			}
//			else if(getEnvironment(hunter)==Environment.THE_END && getEnvironment(speedrunner)!=Environment.THE_END) {
//				result = Porta
//			}
			else if(getEnvironment(hLocation)==Environment.NETHER && !PortalFrom.containsKey(s)) {
				result = HuntersPortalTo.get(h);
//				debug(1);
			}
			else {
				if(PortalFrom.containsKey(s)) {
					result = PortalFrom.get(s);
//					debug(2);
				}
				else {
					if(HuntersPortalTo.containsKey(h)) {
						result = HuntersPortalTo.get(h);
//						debug(3);
					}
					else {
						log(ChatColor.RED + "Error code: 0001");
					}
				}
			}
		}
		if(result==null) debug("result is null!");
		return result;
	}
	
	public static Environment getEnvironment(Player p) {
		return p.getWorld().getEnvironment();
	}
	
	public static Environment getEnvironment(Location l) {
		return l.getWorld().getEnvironment();
	}
}
