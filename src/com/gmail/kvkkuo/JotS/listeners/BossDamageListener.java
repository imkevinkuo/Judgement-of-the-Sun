package com.gmail.kvkkuo.JotS.listeners;

import com.gmail.kvkkuo.JotS.JotS;
import com.gmail.kvkkuo.JotS.bosses.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BossDamageListener implements Listener {

	public JotS plugin;

	public BossDamageListener(JotS plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void DamagedByEntity(EntityDamageByEntityEvent event) {
    	Entity e = event.getDamager();
    	Entity de = event.getEntity();
    	// Player hurt
		if (de instanceof Player) {
			final Player p = (Player) de;
			// Less knockback Skelly warlock
			if (e.getType().equals(EntityType.ARROW)) {
				Arrow a = (Arrow) e;
				if (a.getShooter() instanceof LivingEntity) {
					LivingEntity le = (LivingEntity) a.getShooter();
					if (le.hasMetadata("boss")) {
						if (le.getCustomName().contains("Warlock")) {
							if (p.getLocation().distance(le.getLocation()) > 10) { 
								p.setVelocity(new Vector());
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									public void run() {
										p.setVelocity(new Vector());
									}
				                }, 1L);
							}
							if (a.getFireTicks() > 0) {
								event.setDamage(6);
								p.setFireTicks(80);
							}
						}
					}
				}
			}
			if (e instanceof LivingEntity) {
				LivingEntity boss = (LivingEntity) e;
				if (boss.hasMetadata("boss")) {
					if (boss.getCustomName().contains("Hunter")) {
						if (Hunter.invisible) {
							event.setCancelled(true);
						}
					}
					if (boss.getCustomName().contains("Ninja")) {
						if (Ninja.assaulting) {
							event.setCancelled(true);
						}
					}
					if (boss.getCustomName().contains("Marauder")) {
						if (boss.hasPotionEffect(PotionEffectType.SPEED)) {
							boss.removePotionEffect(PotionEffectType.SPEED);
							boss.removePotionEffect(PotionEffectType.JUMP);
							if (Math.random() < 0.8) {
								Marauder.Uppercut(boss, p, plugin);
							}
							else {
								p.setVelocity(p.getLocation().subtract(boss.getLocation()).toVector().normalize());
								Marauder.Quake(boss, p, plugin);
							}
						}
					}
					if (boss.getCustomName().contains("Rogue")) {
						if (boss.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							p.sendMessage("<" + boss.getCustomName() + "> " + p.getName() + ", I am always one step ahead of you!");
							p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
							boss.removePotionEffect(PotionEffectType.INVISIBILITY);
							boss.getWorld().playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 4, 0);
							event.setDamage(event.getDamage()*2);
						}
					}
				}
			}
		}
		// Player attacks
		if (e instanceof Player) {
			final Player p = (Player) e;
			if (de instanceof LivingEntity) {
				final LivingEntity boss = (LivingEntity) de;
				if (boss.hasMetadata("boss")) {
					if (boss.hasMetadata("noknockback")) {
						boss.setVelocity(new Vector());
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								boss.setVelocity(new Vector());
							}
						}, 1);
					}
					if (boss.getCustomName().contains("Shaman")) {
						Double d = Math.random();
						if (d > 0.7) {
							Shaman.Retreat(boss, plugin);
						}
					}
					if (boss.getCustomName().contains("Assassin")) {
						if (boss.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
							boss.removePotionEffect(PotionEffectType.INVISIBILITY);
							boss.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 4, 0);
						}
						else {
							if (Math.random() > 0.5) {
								Rogue.Step(boss, p, plugin);
							}
						}
					}
					if (boss.getCustomName().contains("Marauder")) {
						if (boss.getHealth() < 50 && !Marauder.enraged) {
		                	Marauder.Enrage(boss, plugin);
		                }
					}
					if (boss.getCustomName().contains("Warlock")) {
						//  Bone Shield
						for (Entity en:p.getNearbyEntities(5,5,5)) {
							if (en.getType().equals(EntityType.DROPPED_ITEM)) {
								Item item = (Item) en;
								ItemStack is = item.getItemStack();
								ItemMeta im = is.getItemMeta();
								String name = im.getDisplayName();
								if (is.getType().equals(Material.BONE) && name != null && name.startsWith("skbone")) {
									event.setDamage(1);
									Skeleton minion = (Skeleton) item.getWorld().spawnEntity(item.getLocation(), EntityType.SKELETON);
									minion.setMetadata("minion", new FixedMetadataValue(plugin, true));
									minion.setHealth(1);
									p.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.QUARTZ_BLOCK);
									item.remove();
									//no knockback
									boss.setVelocity(new Vector());
									Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										public void run() {
											boss.setVelocity(new Vector());
										}
									}, 1);
									break;
								}
							}
						}
		                if (boss.getHealth() < 50) {
		                	Warlock.Twin(boss, plugin);
		                }
					}
					if (boss.getCustomName().contains("Hunter")) {
						if (boss.getHealth() < 50 && !Hunter.wolfed) {
							event.setCancelled(true);
		                	Hunter.WolfPack(boss, plugin);
		                }
					}
					if (boss.getCustomName().contains("Ninja")) {
		                if (boss.getHealth() < 50 && !Ninja.assaulted) {
							event.setCancelled(true);
		                	Ninja.Assault(boss, plugin);
		                }
					}
				}
			}
		}
		// No Boss/Minion Friendly fire
		if (de.getType().equals(EntityType.SKELETON) || de.getType().equals(EntityType.WOLF)) {
			if (de.hasMetadata("boss") || de.hasMetadata("minion")) {
				if (e instanceof Arrow) {
					Arrow a = (Arrow) e;
					if (a.getShooter() instanceof LivingEntity) {
						if (a.hasMetadata("boss") || a.hasMetadata("minion")) {
							event.setCancelled(true);
						}
					}
				}
				else {
					if (e.hasMetadata("boss") || e.hasMetadata("minion")) {
						event.setCancelled(true);
					}
				}
			}
		}
		if (e.getType().equals(EntityType.FIREWORK)) {
			event.setCancelled(true);
		}
	}
    // No boss suffocate
    @EventHandler
    public void EnvironmentDmg(EntityDamageEvent e) {
    	Entity en = e.getEntity();
		if (e.getCause().equals(DamageCause.SUFFOCATION)) {
			if (en instanceof LivingEntity) {
				if (((LivingEntity) en).isCustomNameVisible()) {
					e.setCancelled(true);
				}
			}
		}
		if (e.getCause().equals(DamageCause.FALL)) {
			if (en instanceof LivingEntity) {
				if (en.hasMetadata("minion")) {
					e.setCancelled(true);
				}
				if (en.hasMetadata("hardfall")) {
					en.removeMetadata("hardfall", plugin);
					e.setDamage(2 + e.getDamage());
					en.getWorld().playEffect(en.getLocation(), Effect.STEP_SOUND, 152);
					en.getWorld().playSound(en.getLocation(), Sound.ENTITY_GENERIC_BIG_FALL, 3, 0);
				}
			}
		}
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity le = event.getEntity();
    	if (le.hasMetadata("minion")) {
    		if (le instanceof Skeleton) {
	    		event.getDrops().clear();
	    		ItemStack is = new ItemStack(Material.SKELETON_SKULL);
	    		ItemMeta im = is.getItemMeta();
	    		im.setDisplayName("blastbone" + Math.random());
	    		is.setItemMeta(im);
	    		Item item = le.getWorld().dropItem(le.getLocation(), is);
	    		item.setPickupDelay(Integer.MAX_VALUE);
	    		item.setMetadata("blastbone", new FixedMetadataValue(plugin, true));
				le.getWorld().spawnParticle(Particle.SMOKE_NORMAL, le.getLocation(), 10, 0, 0, 0);
    		}
    	}
    }
    
    @EventHandler
    public void sunBurn(EntityCombustEvent event) {
    	if (event.getDuration() == 8) { // sun ticks, not sure if same for others
    		event.setCancelled(true);
    	}
    }
}
