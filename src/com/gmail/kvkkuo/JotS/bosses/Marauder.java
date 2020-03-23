package com.gmail.kvkkuo.JotS.bosses;

import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Marauder extends Boss {
    
	public static boolean enraged = false;
	
    public static void Spawn(Location l, Plugin plugin, EntityType et) {
    	enraged = false;
    	LivingEntity boss = (LivingEntity) l.getWorld().spawnEntity(l, et);
    	// Global settings
    	Boss.spawnActions(boss, Boss.getRace(et) + " Marauder", plugin);
    	// Set Equipment
		boss.getEquipment().setItemInMainHand(marauderaxe);
		boss.getEquipment().setArmorContents(marauderarmor);
		boss.getEquipment().setItemInMainHandDropChance(80);
    	// Begin abilties
    	Marauder.Fight(boss, plugin);
    }
    
    public static void Fight(LivingEntity boss, Plugin plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				boolean noknockback = boss.hasMetadata("noknockback");
				if (!noknockback) {
					if (!boss.isDead()) {
						Player p = Utils.getNearestPlayer(boss.getLocation(), 15);
						if (p != null) {
							boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 5, 1);
							boss.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, boss.getEyeLocation(), 10, 1, 1, 1, 1);
							if (p.getLocation().distance(boss.getLocation()) <= 4) {
								Marauder.Run(boss, plugin);
							}
							else if (p.getLocation().distance(boss.getLocation()) <= 8){
								Ready(boss, 20, Particle.VILLAGER_ANGRY, plugin);
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									public void run() {
										Marauder.Pull(boss, plugin);
									}
								},	20);
							}
							else if (p.getLocation().distance(boss.getLocation()) <= 12){
								Marauder.Quake(boss, p, plugin);
							}
						}
					}
					else {
						this.cancel();
					}
				}
			}
		}.runTaskTimer(plugin, 80, 100);
    }

	// Marauder gains a speed boost - next attack also triggers an ability
    public static void Run(LivingEntity boss, Plugin plugin) {
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 5));
		boss.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 1));
    }
    
    // Jumps offensively, landing on the ground and sending shockwaves
    public static void Quake(LivingEntity boss, LivingEntity hostile, Plugin plugin) {
    	boss.setVelocity(hostile.getLocation().toVector().subtract(boss.getLocation().toVector()).multiply(0.1).setY(1));
    	boss.setFallDistance(-20);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				boss.setVelocity(hostile.getLocation().toVector().subtract(boss.getLocation().toVector()).multiply(0.1).setY(-1));
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!boss.isDead()) {
							if (boss.isOnGround()) {
								for (int i = 1; i < 4; i++) {
									int in = i*2;
									Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										public void run() {
											boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_GRASS_BREAK, 2, 1);
											for (Location loca:Utils.getCirclePoints(boss.getLocation(), Utils.Plane.XZ, in, in*20)) {
												Location newloc = loca.clone();
												Location below = loca.add(0, -1, 0);
												while (below.getBlock().isPassable()) {
													newloc = newloc.add(0, -1, 0);
													below = below.add(0, -1, 0);
												}
												boss.getWorld().spawnParticle(Particle.BLOCK_CRACK, newloc, 3, 0, 0, 0, 0, below.getBlock().getBlockData());
											}
											Utils.applyNearbyPlayers(boss.getLocation(), in, (LivingEntity le) -> {
												le.damage(2, boss);
											});
										}
									},	(in*2));
								}
								this.cancel();
							}
						}
						else {
							this.cancel();
						}
					}
				}.runTaskTimer(plugin, 1, 1);
			}
		},	(20));
    }
    
    public static void Uppercut(LivingEntity boss, Player p, Plugin plugin) {
		boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2, 1);
		Vector v = boss.getLocation().getDirection().multiply(0.5).setY(1);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.setVelocity(v);
			}
		},	(1));	
		if (enraged) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					boss.setFallDistance(-20);
					boss.setVelocity(v.multiply(2).setY(1));
				}
			},	(5));
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_GRASS_BREAK, 2, 1);
					for (Location loca:Utils.getCirclePoints(boss.getLocation(), Utils.Plane.XZ, 3)) {
						boss.getWorld().spawnParticle(Particle.BLOCK_CRACK, loca, 1, 0, 1, 0, 1, Material.DIRT.createBlockData());
					}
					Utils.applyNearbyPlayers(boss.getLocation(),  3, (LivingEntity le) -> {
						boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2, 1);
						le.setVelocity(boss.getLocation().getDirection().normalize().setY(-0.4));
						le.setMetadata("hardfall", new FixedMetadataValue(plugin, true));
			    	});
				}
			},	(15));
		}
    }
    
    public static void Pull(LivingEntity boss, Plugin plugin) {
		for (int i = 3; i > 0; i--) {
			int ii = i;
			int in = 3-i;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_GRASS_BREAK, 2, 1);
					for (Location loca:Utils.getCirclePoints(boss.getLocation(), Utils.Plane.XZ, ii*2)) {
						boss.getWorld().spawnParticle(Particle.BLOCK_CRACK, loca, 1, 0, 1, 0, 1, Material.DIRT.createBlockData());
					}
				}
			},	(in*3));
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Utils.applyNearbyPlayers(boss.getLocation(),  8, (LivingEntity le) -> {
					le.damage(2, boss);
					le.setVelocity(boss.getLocation().toVector().subtract(le.getLocation().toVector()).normalize().setY(0.4));
				});
			}
		},	(4));
    }
    
    public static void Enrage(LivingEntity boss, Plugin plugin) {
    	enraged = true;
		Boss.Ready(boss, 30, Particle.CLOUD, plugin);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
		    	boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 10));
			}
		},	(30));
    }
}
