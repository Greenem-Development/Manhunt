package org.greenem.plugins.manhunt.events;

import static org.greenem.plugins.manhunt.data.References.Frozen;
import static org.greenem.plugins.manhunt.data.References.FrozenLocs;
import static org.greenem.plugins.manhunt.data.References.Hunters;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalFrom;
import static org.greenem.plugins.manhunt.data.References.HuntersPortalTo;
import static org.greenem.plugins.manhunt.data.References.PortalEnd;
import static org.greenem.plugins.manhunt.data.References.PortalFrom;
import static org.greenem.plugins.manhunt.data.References.PortalTo;
import static org.greenem.plugins.manhunt.data.References.Speedrunners;

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
		if(Speedrunners.contains(e.getPlayer().getName())) CompassUpdating.findTarget();
		if(Hunters.contains(e.getPlayer().getName())) CompassUpdating.findTarget();
		if(Frozen.contains(p.getName())) {
			if(!e.getFrom().getWorld().equals(e.getTo().getWorld())) return;
			Location loc = e.getTo().clone();
			p.setAllowFlight(true);
			p.setFlying(true);
			loc.setX(FrozenLocs.get(p).getX());
			loc.setY(FrozenLocs.get(p).getY());
			loc.setZ(FrozenLocs.get(p).getZ());
			p.teleport(loc);
		}
	}
	
	@EventHandler
	public void PlayerPortalEvent (PlayerPortalEvent e) {
		Player p = e.getPlayer();
		String nameP = p.getName();
		if(Speedrunners.contains(nameP)) {
			if(e.getTo().getWorld().getEnvironment().equals(Environment.THE_END)) {
				PortalEnd.put(nameP, e.getTo());
			}
			else {
				if(PortalFrom.containsKey(nameP)) PortalFrom.remove(nameP);
				if(PortalTo.containsKey(nameP)) PortalTo.remove(nameP);
				PortalFrom.put(nameP, e.getFrom());
				PortalTo.put(nameP, e.getTo());
			}
		}
		if(Hunters.contains(nameP)) {
			if(e.getTo().getWorld().getEnvironment().equals(Environment.THE_END)) {
				//PortalEnd.put(nameP, e.getTo());
			}
			else {
				if(HuntersPortalFrom.containsKey(nameP)) HuntersPortalFrom.remove(nameP);
				if(HuntersPortalTo.containsKey(nameP)) HuntersPortalTo.remove(nameP);
				HuntersPortalFrom.put(nameP, e.getFrom());
				HuntersPortalTo.put(nameP, e.getTo());
			}
		}
		checkNetherCompassInHand(p, e.getTo(), p.getInventory().getHeldItemSlot(), false);
		CompassUpdating.findTarget();
	}

	@EventHandler
	public void PlayerDies (PlayerDeathEvent e) {
		Player p = e.getEntity();
		String nameP = p.getName();
		if(PortalFrom.containsKey(nameP) && PortalTo.containsKey(nameP) && p.getWorld().getEnvironment().equals(Environment.NETHER)) {
			Location from = new Location(PortalFrom.get(nameP).getWorld(), PortalFrom.get(nameP).getX(), PortalFrom.get(nameP).getY(), PortalFrom.get(nameP).getZ());
			PortalFrom.put(nameP, PortalTo.get(nameP));
			PortalTo.put(nameP, from);
		}
		if(Hunters.contains(nameP)) {
			for(int i = 0; i<p.getInventory().getSize(); i++) {
				try {
					if(e.getDrops().get(i).getType().equals(Material.COMPASS)) e.getDrops().remove(i); //TODO java.lang.IndexOutOfBoundsException: Index 8 out of bounds for length 8, 233 | Index 1 out of bounds for length 0
				}
				catch (IndexOutOfBoundsException e1) {
					
				}
			}
		}
	}
	
	@EventHandler
	public void Respawn (PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		String nameP = p.getName();
		if(Hunters.contains(nameP)) {
			Stash.AddItemStack(p, new ItemStack(Material.COMPASS), 0);
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
		checkNetherCompassInHand(p, loc, slot, true);
	}
	public static void checkNetherCompassInHand(Player p, Location loc, int slot, boolean checkMeta) {
//		log.info("check");
		if(loc.getWorld().getEnvironment().equals(Environment.THE_END) || loc.getWorld().getEnvironment().equals(Environment.NETHER)) {
//			log.info("check2");
			if(p.getInventory().getItem(slot)!=null) {
//				log.info("check3");
				ItemStack maybeCompass = p.getInventory().getItem(slot);
				if(maybeCompass.getType().equals(Material.COMPASS)){
//					log.info("check4");
					boolean toggle1 = false;
					if(checkMeta) {
						//log.info(e.getPlayer().getInventory().getItem(e.getNewSlot()).getItemMeta().getAttributeModifiers().containsKey(e)+ "");
						CompassMeta meta = (CompassMeta) maybeCompass.getItemMeta();
		    	  		if(meta.getLodestone() != null) {
		    	  			//e.setCancelled(true);
		    	  			toggle1 = true;
		    	  		}
					}
					else {
						toggle1 = true;
					}
					if(toggle1) {
						p.sendMessage(ChatColor.RED + "Sorry, but the compass will not update while you are holding it in the Nether (or End)");
					}
				}
			}
		}
	}
}
