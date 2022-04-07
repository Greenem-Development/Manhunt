package org.greenem.plugins.Manhunt.events;

import static org.greenem.plugins.Manhunt.Main.Main.Frozen;
import static org.greenem.plugins.Manhunt.Main.Main.FrozenAir;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class FrozenEvents implements Listener {
	@EventHandler(priority=EventPriority.LOW)
	public void flightAttempt(PlayerToggleFlightEvent e) {
		if(Frozen.contains(e.getPlayer().getName())) e.setCancelled(true);
	}
	
	@EventHandler
    public void AuthAttacking(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player Damager = (Player) e.getDamager();
			if (Frozen.contains(Damager.getName()) || Frozen.contains(e.getEntity().getName())) {
				e.setCancelled(true);
			}
		}
		if (e.getDamager() instanceof Projectile) {
            if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
            	if (Frozen.contains(((Player) ((Projectile) e.getDamager()).getShooter()).getName()) || Frozen.contains(e.getEntity().getName())) {
    				e.setCancelled(true);
    			}
    		}
        }
	}
	
	@EventHandler
    public void AuthInteracting(PlayerInteractEntityEvent e) {
		if(Frozen.contains(e.getPlayer().getName())) {
			e.setCancelled(true);
        }
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void EntityTargetEvent(EntityTargetEvent e) {
		if(e.getTarget() instanceof Player) {
			Player p = (Player) e.getTarget();
	        if (e.getTarget() instanceof LivingEntity){
	            if(Frozen.contains(p.getName())) {
	            	e.setCancelled(true);
	            }
	        }
		}
	}
	
	public void PlayerPickupItemEvent(PlayerPickupArrowEvent e) { //cancelling picking an arrow up
		if(Frozen.contains(e.getPlayer().getName())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
    public void onHungingBrake(HangingBreakByEntityEvent e) {
		if(e.getRemover().getType() == EntityType.PLAYER){
			if(Frozen.contains(e.getRemover().getName())) {
				e.setCancelled(true);
    		}
    	}
    }
	
	@EventHandler
	public void PlayerDropItemEvent (PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if(Frozen.contains(p.getName())) {
			e.setCancelled(true);
		}
	}
	
	public void PlayerHitsPlayerEvt(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player) {
			if(Frozen.contains(e.getDamager().getName()) || Frozen.contains(e.getEntity().getName())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void DisableArmorStandIntaract (PlayerInteractAtEntityEvent e) {
		if(Frozen.contains(e.getPlayer().getName())) {
			if(e.getRightClicked() instanceof ArmorStand) {
				e.setCancelled(true);
			}
		}
		if(e.getRightClicked() instanceof ArmorStand) {
			if(Frozen.contains(e.getPlayer().getName())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void DamageRulesManager(EntityDamageEvent e) {
		//log.info(e.getEntity().getType() + "");
		if (e.getEntity().getType().toString().equals("PLAYER")){
			if(Frozen.contains(((Player) e.getEntity()).getName())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent e) {
		if(Frozen.contains(e.getPlayer().getName())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent e) {
		if(Frozen.contains(e.getPlayer().getName())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void PlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        //if(e.getClickedBlock() != null) log.info(e.getClickedBlock().getType().toString());
        if(Frozen.contains(p.getName())) {
        	e.setCancelled(true);
        }
	}
	
	@EventHandler
	public void FoodLevelChangeEvent(FoodLevelChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			if(Frozen.contains(((Player) e.getEntity()).getName())){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void EntityAirChangeEvent(EntityAirChangeEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(Frozen.contains(((Player) e.getEntity()).getName())) {
				if(!FrozenAir.containsKey(p)) FrozenAir.put(p, p.getRemainingAir());
				if(e.getAmount()<FrozenAir.get(p)) {
					//e.setAmount(-300);
					e.setAmount(FrozenAir.get(p));
				}
			}
		}
	}
	
	//DESABLED DUE TO 1.18 REMOVED IT
	/*@EventHandler
	public void EEntityDismount(EntityDismountEvent e) {
		if(e.getEntity() instanceof Player) {
			//log.info("EntityDismountEvent");
			if(Frozen.contains(((Player) e.getEntity()).getName())){
				e.setCancelled(true);
			}
		}
	}*/
}
