package com.gmail.kvkkuo.JotS.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.gmail.kvkkuo.JotS.classes.*;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.utils.Utils;

public class DamageListener implements Listener {
	
	public static DamageCause[] G_VALUES = new DamageCause[] {
		DamageCause.ENTITY_ATTACK, DamageCause.ENTITY_SWEEP_ATTACK, DamageCause.FIRE, DamageCause.FIRE_TICK,
		DamageCause.DRAGON_BREATH, DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION
	}; // Glacial Shielding
	
	public static DamageCause[] M_VALUES = new DamageCause[] {
		DamageCause.ENTITY_ATTACK, DamageCause.ENTITY_SWEEP_ATTACK			
	}; // Divine Fire Shielding
	
	public static Set<DamageCause> glacialBlocks = new HashSet<>(Arrays.asList(G_VALUES));
	public static Set<DamageCause> meleeAttacks = new HashSet<>(Arrays.asList(M_VALUES));
	
	public JotS plugin;
	
	public DamageListener(JotS plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void DamagedByEntity(EntityDamageByEntityEvent event) {
    	Entity d = event.getDamager();
    	Entity v = event.getEntity();
    	if (v instanceof LivingEntity) {
			// Fireworks
			if (d.getType().equals(EntityType.FIREWORK)) {
				event.setCancelled(true);
				return;
			}
			// Wither skulls
			if (d instanceof WitherSkull) {
				if (v.equals(((WitherSkull) d).getShooter())) {
					event.setCancelled(true);
					return;
				}
			}

    		LivingEntity ev = (LivingEntity) v;
	    	if (d instanceof Player) { // Player-offensive melee abilities
	    		Player pd = (Player) d;
	    		if (meleeAttacks.contains(event.getCause())) {
		    		// Duelist Buffs and AA
		    		if (pd.hasMetadata("rage")) {
		    			event.setDamage(event.getDamage() + Duelist.stackRage(pd, plugin));
		    		}
		    		else if (pd.hasMetadata("fury")) {
	    				Duelist.healFury(pd);
		    		}
		    		if (pd.hasMetadata("empower")) {
		    			int type = pd.getMetadata("empower").get(0).asInt();
		    			if (type == 0) {
		    				Duelist.applyDaze(ev);
		    			}
		    			else if (type == 1) {
		    				Duelist.applyCripple(ev);
		    			}
		    			else if (type == 2) {
		    				Duelist.applyShatter(ev, v.getLocation().subtract(d.getLocation()).toVector());
		    			}
		    			else if (type == 3) {
		    				Duelist.applyBleed(ev, pd, plugin);
		    			}
		    			pd.removeMetadata("empower", plugin);
		    		}
		    		if (pd.hasMetadata("spellblade")) {
		    			Duelist.applySpellblade(ev, pd);
		    		}
		    		if (pd.hasMetadata("silencer")) {
		    			Duelist.applySilence(ev, pd, plugin);
		    		}
		    		// Assassin Umbra Soul & Stealth
		    		if (pd.hasMetadata("soul")) {
						event.setCancelled(true);
					}
		    		if (pd.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						Assassin.exitStealth((Player) d, ev, plugin);
					}
	    		}
	    	}
			if (v instanceof Player) { // Player-defensive abilities
				Player pv = (Player) v;
				// Assassin stealth
				if (pv.hasMetadata("illusion")) {
					Assassin.IllusionT(pv, plugin);
				}
				else if (pv.hasMetadata("deception")) {
					Assassin.DeceptionT(pv, plugin);
				}
				else if (pv.hasMetadata("soul")) {
					event.setCancelled(true);
				}
				// Spine shield
				if (pv.hasMetadata("spines")) {
					if (d instanceof LivingEntity) {
						((LivingEntity) d).damage(0.5, v);
					}
				}
				// Paladin Divine Fire
				if (pv.hasMetadata("divine")) {
					if (Paladin.consumeFire(pv, plugin)) {
						d.setFireTicks(60);
						event.setDamage(0);
					}
				}
			}
			// Paladin Ice Spray
			if (d instanceof Snowball) {
				if (d.hasMetadata("ice")) {
					ev.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
					Utils.magicDamage((LivingEntity) ((Snowball)d).getShooter(), ev, 4, plugin);
				}
			}
    	}
	}
	
	@EventHandler
	public void DamagedByEnvironment(EntityDamageEvent event) {
		Entity v = event.getEntity();
		if (v instanceof LivingEntity) {
			LivingEntity lv = (LivingEntity) v;
			DamageCause cause = event.getCause();
			if (v instanceof Player) {
				Player p = (Player) v;
				// Raider Fall Resistance
				if (cause.equals(DamageCause.FALL)) {
					if (p.hasMetadata("nofall"))  {
						p.removeMetadata("nofall", plugin);
						event.setCancelled(true);
					}
				}
				// Paladin Shielding
				if (p.hasMetadata("guard") && glacialBlocks.contains(cause)) {
					if (Paladin.consumeGuard(p)) {
						event.setDamage(0);
					}
				}
				if (!event.isCancelled() && p.hasMetadata("redemption") && event.getFinalDamage() > p.getHealth()) {
					event.setCancelled(true);
					p.removeMetadata("redemption", plugin);
					Paladin.Revive(p, plugin);
				}
				// Guardian Shields
				if (p.hasMetadata("starve")) {
					double newDamage = Guardian.StarvingTrigger(p, event.getDamage());
					event.setDamage(newDamage);
				}
				if (p.hasMetadata("kinetic")) {
					int kineticStacks = p.getMetadata("kinetic").get(0).asInt();
					p.setMetadata("kinetic", new FixedMetadataValue(plugin, kineticStacks + 1));
				}
			}
			// Witherknight passive
			if (cause.equals(DamageCause.WITHER)) {
				Utils.applyNearbyPlayers(lv.getLocation(), 8, (LivingEntity le) -> {
					Player p = (Player) le;
					Integer passiveUpgrade = plugin.upgrades.get(p.getUniqueId())[1];
					if (passiveUpgrade != null) {
						if (passiveUpgrade == 0) {
							Witherknight.Barrier(p, plugin);
						}
						if (passiveUpgrade == 1) {
							Witherknight.Drain(p, plugin);
						}
						if (passiveUpgrade == 2) {
							Witherknight.Wisp(p, plugin);
						}
						if (passiveUpgrade == 3) {
							Witherknight.Rebirth(p, plugin);
						}
					}
				});
			}
		}
	}
	
	// Raider Potions
	@EventHandler
	public void PotionSplash(PotionSplashEvent event) {
		ThrownPotion tp = event.getPotion();
		String name = tp.getItem().getItemMeta().getDisplayName();
		if (tp.getShooter() instanceof Player) {
			Player p = (Player) tp.getShooter();
			if (name != null) {
				if (name.equals("blind")) {
					for (LivingEntity le:event.getAffectedEntities()) {
						if (!le.equals(p)) {
							le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
						}
					}
				}
				if (name.equals("famine")) {
					for (LivingEntity le:event.getAffectedEntities()) {
						if (!le.equals(p)) {
							le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 3));
						}
					}
				}
			}
			for (PotionEffect pe:tp.getEffects()) {
				if (pe.getType().equals(PotionEffectType.HARM)) {
					event.setCancelled(true);
					for (LivingEntity le:event.getAffectedEntities()) {
						double dmg = Math.ceil(3*Math.pow(2, pe.getAmplifier())*event.getIntensity(le));
						Utils.magicDamage(p, le, (int) dmg, plugin);
					}
				}
			}
		}
	}
    
    // Witherknight/Raider Explosives
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
    	Entity proj = e.getEntity();
    	if (proj.getType().equals(EntityType.WITHER_SKULL)) {
    		WitherSkull ws = (WitherSkull) proj;
    		e.setCancelled(true);
    		ws.getWorld().spawnParticle(Particle.SMOKE_NORMAL, e.getLocation(), 20, 0, 0, 0, 0.2);
    		ws.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, e.getLocation(), 1, 0, 0, 0);
			Utils.applyNearby(ws.getLocation(), (LivingEntity) ws.getShooter(), 4, 4, 4, (LivingEntity le) -> {
				le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1));
			});
    	}
    	if (proj.getType().equals(EntityType.FIREBALL) && proj.hasMetadata("shot")) {
    		Fireball fire = (Fireball) proj;
    		Player p = (Player) fire.getShooter();
    		e.setCancelled(true);
			Raider.detonate(fire, p, plugin);
    	}
    }
    
     // No environmental fire
//    @EventHandler
//    public void FireballFire(BlockIgniteEvent e) {
//    	e.setCancelled(true);
//    }
    // No burning in sun
    @EventHandler
    public void onEntityCombust(EntityCombustEvent e){
    	e.setCancelled(true);
    }
}
