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

public class CompassUpdating {
	public static void findTarget() {
		for (String hunter : Hunters) {
			Player h = Bukkit.getPlayerExact(hunter);
			if(h==null || h.isDead()) continue;
			updateCompass(h, hunter);
		}
	}
	
	public static void updateCompass(Player h, String hunter) {
		String speedrunner = getNearestSpeedrunner(h, hunter);
		if(speedrunner==null) return;
		Player s = Bukkit.getPlayerExact(speedrunner);
		if(s==null) {
			debug("Speedrunner-target is null");
			return;
		}
		Location target = GetTargetLocation(h, s);
//		debug(target);
		
		if(target==null) return; // TODO error logging with no spamming
		
		if(target.getWorld().getEnvironment()==Environment.NORMAL) {
			boolean hasCompass = false;
			for(int i = 0; i < h.getInventory().getSize(); i++) {
		   		ItemStack currentItem = h.getInventory().getItem(i);
		      	if(currentItem!=null && currentItem.getType().equals(Material.COMPASS)) {
		      		hasCompass = true;
	    	  		CompassMeta meta = (CompassMeta) currentItem.getItemMeta();
			      	//log("meta: " + CompassItem.getItemMeta().toString());
	    	  		if(meta.getLodestone()!=null) {
	    	  			meta.setLodestone(null);
	    	  			currentItem.setItemMeta(meta);
	    	  			debug("DISABLING LODESTONE");
	    	  		}
		      	}
			}
			if(hasCompass) {
				h.setCompassTarget(target);
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
	                debug("ENABLED LODESTONE");
//		    	  	}
		      	}
			}
		}
	}
	
	public static boolean isPlayerHoldingACompass(Player p) {
		return (p.getInventory().getItemInMainHand()!=null && p.getInventory().getItemInMainHand().getType()==Material.COMPASS) ||
				(p.getInventory().getItemInOffHand()!=null && p.getInventory().getItemInOffHand().getType()==Material.COMPASS);
	}
	
	public static String getNearestSpeedrunner(Player h, String hunter) {
		Map <String, Double> distances = getAllDistances(h, hunter);
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
	
	public static Map<String, Double> getAllDistances(Player h, String hunter) {
		Map <String, Double> distances = new HashMap<>();
		for (String speedrunner : Speedrunners) {
			Player s = Bukkit.getPlayerExact(speedrunner);
			if(s==null || s.isDead()) continue;
			double distance = GetDistance(h, s);
			if(distance<0) {
				debug("distance < 0, h = " + hunter + ", s = " + speedrunner);
				continue;
			}
			distances.put(speedrunner, distance);
		}
		return distances;
	}
	
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
						double distance = GetDistance(plHunter, speedrunnerPl);
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
			if(getEnvironment(speedrunner)==Environment.THE_END && getEnvironment(speedrunner)==Environment.NORMAL) {
				result = 0;
				result+=hunter.getLocation().distance(PortalEndFrom.get(nameS));
			}
			else if(getEnvironment(hunter)==Environment.THE_END && !PortalEndTo.containsKey(nameS)) {
				result = 0;
				if(HuntersPortalEndTo.containsKey(nameH)) {
					result+=hunter.getLocation().distance(HuntersPortalEndTo.get(nameH));
				} else {
					log("Error code: 0002");
				}
			}
			else {
				if(PortalFrom.containsKey(nameS)) {
					result = 0;
					if(hunter.getWorld().getEnvironment().equals(PortalFrom.get(nameS).getWorld().getEnvironment()) && speedrunner.getWorld().getEnvironment().equals(PortalTo.get(nameS).getWorld().getEnvironment())) {
						result += hunter.getLocation().distance(PortalFrom.get(nameS));
						result += speedrunner.getLocation().distance(PortalTo.get(nameS));
					}
				}
				else {
					if(HuntersPortalTo.containsKey(nameH) && HuntersPortalFrom.containsKey(nameH)) {
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
	
	public static Location GetTargetLocation (Player hunter, Player speedrunner) {
		String h = hunter.getName();
		String s = speedrunner.getName();
		Location result = hunter.getLocation(); // like a placehodler
		if(hunter.getWorld().equals(speedrunner.getWorld())) {
			result = speedrunner.getLocation();
		}
		else {
			if(getEnvironment(speedrunner)==Environment.THE_END && getEnvironment(hunter)==Environment.NORMAL) {
				result = PortalEndFrom.get(s);
			}
			else if(getEnvironment(speedrunner)!=Environment.THE_END && getEnvironment(hunter)==Environment.THE_END) {
				result = PortalEndTo.get(s);
				if(result==null) {
					result = HuntersPortalEndTo.get(h);
				}
			}
//			else if(getEnvironment(hunter)==Environment.THE_END && getEnvironment(speedrunner)!=Environment.THE_END) {
//				result = Porta
//			}
			else if(getEnvironment(hunter)==Environment.NETHER && !PortalFrom.containsKey(s)) {
				result = HuntersPortalTo.get(h);
				debug(1);
			}
			else {
				if(PortalFrom.containsKey(s)) {
					result = PortalFrom.get(s);
					debug(2);
				}
				else {
					if(HuntersPortalTo.containsKey(h)) {
						result = HuntersPortalTo.get(h);
						debug(3);
					}
					else {
						log(ChatColor.RED + "Error 0001");
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
