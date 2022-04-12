package org.greenem.plugins.manhunt.events;

import static org.greenem.plugins.manhunt.util.SimpleLogger.log;
import static org.greenem.plugins.manhunt.util.SimpleLogger.debug;
import static org.greenem.plugins.manhunt.data.References.Frozen;
import static org.greenem.plugins.manhunt.data.References.FrozenLocs;
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
import static org.greenem.plugins.manhunt.compass.CompassUpdating.getEnvironment;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.greenem.plugins.manhunt.compass.CompassUpdating;
import org.greenem.plugins.manhunt.util.Stash;

public class ManhuntEvents implements Listener {
	@EventHandler
	public void Move(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		String pName = p.getName();
		if(Speedrunners.contains(pName) || Hunters.contains(pName)) CompassUpdating.findTarget();
		if(Frozen.contains(p.getName())) {
			cancelMovement(e);
		}
	}
	
	private static void cancelMovement(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!e.getFrom().getWorld().equals(e.getTo().getWorld())) return;
		Location loc = e.getTo().clone();
		p.setAllowFlight(true);
		p.setFlying(true);
		loc.setX(FrozenLocs.get(p).getX());
		loc.setY(FrozenLocs.get(p).getY());
		loc.setZ(FrozenLocs.get(p).getZ());
		p.teleport(loc);
	}
	
	@EventHandler
	public void PlayerPortalEvent (PlayerPortalEvent e) {
		Player p = e.getPlayer();
		String pName = p.getName();
//		debug(e.getFrom());
//		debug(e.getTo());
//		debug(e.getPlayer().getLocation());
		if(Speedrunners.contains(pName)) {
			if(getEnvironment(e.getTo())==Environment.THE_END) {
				PortalEndFrom.put(pName, e.getFrom());
//				debug(PortalEndFrom);
				PortalEndTo.put(pName, e.getTo());
//				debug(PortalEndTo);
			}
			else {
				PortalFrom.put(pName, e.getFrom());
				PortalTo.put(pName, e.getTo());
			}
		}
		if(Hunters.contains(pName)) {
			if(getEnvironment(e.getTo())==Environment.THE_END) {
				HuntersPortalEndFrom.put(pName, e.getFrom());
				HuntersPortalEndTo.put(pName, e.getTo());
			}
			else {
//				HuntersPortalFrom.remove(pName);
//				HuntersPortalTo.remove(pName);
				HuntersPortalFrom.put(pName, e.getFrom());
				HuntersPortalTo.put(pName, e.getTo());
//				debug(HuntersPortalTo.get(pName));
			}
		}
		checkNetherCompassInHand(p, e.getTo(), p.getInventory().getHeldItemSlot(), false, p.getInventory().getItemInOffHand());
		CompassUpdating.findTarget(p.getName(), e.getTo());
	}

	@EventHandler
	public void PlayerDies (PlayerDeathEvent e) {
		Player p = e.getEntity();
		String pName = p.getName();
		if(PortalFrom.containsKey(pName) && PortalTo.containsKey(pName) && p.getWorld().getEnvironment().equals(Environment.NETHER)) {
			Location from = PortalFrom.get(pName).clone();
			PortalFrom.put(pName, PortalTo.get(pName));
			PortalTo.put(pName, from);
		}
		if(Hunters.contains(pName)) {
			clearCompassItems(e, p);
		}
	}
	
	public static void clearCompassItems(PlayerDeathEvent e, Player p) {
		p.getInventory().remove(Material.COMPASS); // not tested
		for(int i = 0; i<p.getInventory().getSize(); i++) {
			try {
				if(e.getDrops().get(i).getType().equals(Material.COMPASS)) e.getDrops().remove(i); //TODO java.lang.IndexOutOfBoundsException: Index 8 out of bounds for length 8, 233 | Index 1 out of bounds for length 0
			}
			catch (IndexOutOfBoundsException e1) {
				
			}
		}
	}
	
	@EventHandler
	public void Respawn (PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		String nameP = p.getName();
		if(Hunters.contains(nameP)) {
			Stash.AddItemStack(p, new ItemStack(Material.COMPASS), 8);
			CompassUpdating.findTarget();
		}
	}
	
	@EventHandler
	public void onScroll(PlayerItemHeldEvent e) {
		checkNetherCompassInHand(e.getPlayer(), e.getNewSlot());
	}
	
	public static void checkNetherCompassInHand(Player p, int slot) {
		checkNetherCompassInHand(p, p.getLocation(), slot);
	}
	
	public static void checkNetherCompassInHand(Player p, Location loc, int slot) {
		checkNetherCompassInHand(p, loc, slot, true, null);
	}
	
	public static void checkNetherCompassInHand(Player p, Location loc, int slot, boolean checkMeta, ItemStack is) {
//		log.info("check");
		if(loc.getWorld().getEnvironment().equals(Environment.THE_END) || loc.getWorld().getEnvironment().equals(Environment.NETHER)) {
//			log.info("check2");
			if(p.getInventory().getItem(slot)!=null) {
//				log.info("check3");
				ItemStack maybeCompass = p.getInventory().getItem(slot);
				if(maybeCompass.getType().equals(Material.COMPASS)){
//					log.info("check4");
					boolean warn = false;
					if(checkMeta) {
						//log.info(e.getPlayer().getInventory().getItem(e.getNewSlot()).getItemMeta().getAttributeModifiers().containsKey(e)+ "");
						CompassMeta meta = (CompassMeta) maybeCompass.getItemMeta();
		    	  		if(meta.getLodestone() != null) {
		    	  			//e.setCancelled(true);
		    	  			warn = true;
		    	  		}
					}
					else {
						warn = true;
					}
					
					if(is!=null) {
						if(is.getType()==Material.COMPASS) warn = true;
					}
					
					if(warn) {
						p.sendMessage(ChatColor.RED + "Sorry, but the compass will not update while you are holding it in the Nether (or End)");
					}
				}
			}
		}
	}
}
