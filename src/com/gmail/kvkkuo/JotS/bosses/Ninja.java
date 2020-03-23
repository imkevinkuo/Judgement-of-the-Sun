package com.gmail.kvkkuo.JotS.bosses;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Ninja extends Boss {
	
	// Personal Variables (Ultimate Skills and Abilities)
	public static boolean invisible = false;
	public static boolean assaulted = false;
	public static boolean assaulting = false;
	public static int timer = 0;
    
    public static void Spawn(Location l, Plugin plugin, EntityType et) {
    	// Set Personal Variable true/false
    	invisible = false;
    	assaulted = false;
    	// Spawn in Boss
    	LivingEntity boss = (LivingEntity) l.getWorld().spawnEntity(l, et);
    	// Global settings
    	Boss.spawnActions(boss, Boss.getRace(et) + " Ninja", plugin);
    	// Set Equipment
		boss.getEquipment().setArmorContents(ninjaarmor);
		boss.getEquipment().setItemInMainHand(ninjasword);
		boss.getEquipment().setItemInMainHandDropChance(100);
    	// Begin abilties
    	Ninja.Fight(boss, plugin);
    }
    
    public static void Fight(LivingEntity boss, Plugin plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				boolean noknockback = boss.hasMetadata("noknockback");
				if (!assaulting && !invisible && !noknockback && !boss.isDead()) {
					Player p = Utils.getNearestPlayer(boss.getLocation(), 12);
					if (p != null) {
						double distance = boss.getLocation().distance(p.getLocation());
						double m = Math.random();
						if (distance <= 5) {
							if (m > 0.5) {
								Ready(boss, 20, Particle.SMOKE_NORMAL, plugin);
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									public void run() {
										Steel(boss, plugin);
									}
								},	20);
							} else {
								Backflip(boss, plugin);
							}
						} 
						else {
							if (m > 0.5) {
								Ready(boss, 20, Particle.FIREWORKS_SPARK, plugin);
								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									public void run() {
										Slice(boss, plugin);
									}
								},	20);
							} else {
								Rake(boss, plugin);
							}
						}
					}
				}
			}
		}.runTaskTimer(plugin, 100, 80);
    }
	
	public static void Steel(LivingEntity boss, Plugin plugin) {
    	for (int i = 0; i < 3; i++) {
			int in = i + 2;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					boss.getWorld().playSound(Utils.getBlockRelative(boss.getLocation(), 1, 0, 0), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 10, 1);
					for (Location l : Utils.getCirclePoints(bodyLocation(boss), Utils.Plane.XZ, in, (in * 5) + 4)) {
						boss.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 1, 0, 0, 0, 0);
					}
					if (in == 4) {
						Utils.applyNearbyPlayers(boss.getLocation(), 4, (LivingEntity le) -> {
							le.damage(4, boss);
							le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						});
					}
				}
			}, i * 4);
		}
    }
	
    // Jump backwards and throw 3 stars
    public static void Backflip(LivingEntity boss, Plugin plugin) {
		boss.setFallDistance(-20);
    	LivingEntity hostile = Utils.getNearestPlayer(boss.getLocation(), 10);
		if (hostile != null) {
			boss.setVelocity(boss.getLocation().toVector().subtract(hostile.getLocation().toVector()).normalize().multiply(0.6).setY(1));
			for (int i = 0; i < 3; i++) {
				int in = i;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_EGG_THROW, 5, 0);
						Vector starv = hostile.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize();
						ItemStack is = new ItemStack(Material.NETHER_STAR);
						ItemMeta im = is.getItemMeta();
						im.setDisplayName("flyingblade" + in);
						is.setItemMeta(im);
						Item item = boss.getWorld().dropItem(boss.getEyeLocation(), is);
						item.setPickupDelay(Integer.MAX_VALUE);
						item.setVelocity(starv);
						new BukkitRunnable() {
							@Override
							public void run() {
								if (item.isOnGround()) {
									item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.QUARTZ_BLOCK);
									item.remove();
									this.cancel();
								}
								Utils.applyNearbyPlayers(item.getLocation(), 2, (LivingEntity le) -> {
									item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.QUARTZ_BLOCK);
									item.remove();
									le.damage(5, boss);
									this.cancel();
								});
							}
						}.runTaskTimer(plugin, 1, 1);
					}
				}, (5 * i));
			}
		}
    }
    
    public static void Rake(LivingEntity boss, Plugin plugin) {
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3));
		boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 5, 0);
    	int[] angles = {-21, -14, -7, 0, 7, 14, 21};
		Location location = boss.getEyeLocation();
		Vector direction = boss.getLocation().getDirection();
	    direction.normalize();
	    // some trick, to get a vector pointing in the player's view direction, but on the x-z-plane only and without problems when looking straight up (x, z = 0 then)
	    Vector dirY = (new Location(location.getWorld(), 0, 0, 0, location.getYaw(), 0)).getDirection();
	    for (int angle : angles) {
		    Vector vec;
		    if (angle != 0) {
		        vec = Utils.rotateYAxis(dirY, angle);
		        vec.multiply(Math.sqrt(vec.getX() * vec.getX() + vec.getZ() * vec.getZ())).subtract(dirY);
		        vec = direction.clone().add(vec).normalize();
		    } else {
		        vec = direction.clone();
		    }
		    ItemStack is = new ItemStack(Material.NETHER_STAR);
    		ItemMeta im = is.getItemMeta();
		    im.setDisplayName("flyingblade" + angle);
		    is.setItemMeta(im);
    		Item item = boss.getWorld().dropItem(boss.getEyeLocation(), is);
    		item.setPickupDelay(Integer.MAX_VALUE);
    		item.setVelocity(vec.multiply(1.4));
    		BukkitTask bu = new BukkitRunnable() {
				@Override
				public void run() {
					if (!item.isOnGround()) {
						Utils.applyNearbyPlayers(item.getLocation(), 2, (LivingEntity le) -> {
							le.damage(4, boss);
						});
					}
					else {
						this.cancel();
					}
				}
			}.runTaskTimer(plugin, 1, 1);
    		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 5, 0);
					Set<LivingEntity> damaged = new HashSet<>();
					bu.cancel();
					item.setVelocity(boss.getEyeLocation().toVector().subtract(item.getLocation().toVector()).multiply(0.2));
					BukkitTask bt = new BukkitRunnable() {
						@Override
						public void run() {
					    	if (item.getLocation().distance(boss.getEyeLocation()) <= 1) {
					    		item.remove();
					    		this.cancel();
					    	}
							Utils.applyNearbyPlayers(item.getLocation(), 2, (LivingEntity le) -> {
								if (!damaged.contains(le)) {
									le.setVelocity(boss.getLocation().toVector().subtract(le.getLocation().toVector()).multiply(0.2).setY(0.1));
									le.damage(4, boss);
									damaged.add(le);
								}
							});
						}
					}.runTaskTimer(plugin, 1, 1);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							item.remove();
							bu.cancel();
							bt.cancel();
						}
					},	3);
				}
			},	8);
    	}
    }

	public static void Slice(LivingEntity boss, Plugin plugin) {
		Player p = Utils.getNearestPlayer(boss.getLocation(), 12);
		if (p != null) {
			// Skill preset
			invisible = true;
			boss.getEquipment().clear();
			boss.setNoDamageTicks(100);
			boss.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 8));
			boss.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 1));
			FireworkPlayer.fire(boss.getEyeLocation(), Type.BALL, Color.WHITE, false);
			// Creating slice
			Location l1 = boss.getEyeLocation();
			Vector direction = p.getEyeLocation().toVector().subtract(l1.toVector()).normalize();
			Integer slicedistance = (int) Math.floor((p.getEyeLocation().distance(boss.getEyeLocation()) * 2));
			if (slicedistance < 10) {
				slicedistance = 10;
			}
			RayTrace r1 = new RayTrace(l1, direction, slicedistance, 1);
			Integer count = 0;
			while (r1.hasNext()) {
				count++;
				Location l = r1.next();
				if (l.getBlock().isPassable()) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							boss.teleport(l);
							boss.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, l, 4, 0, 0, 0, 0);
							Utils.applyNearbyPlayers(l, 2, (LivingEntity le) -> {
								boss.getWorld().spawnParticle(Particle.BLOCK_DUST, le.getLocation(), 8, 0, 0, 0, 0, Material.QUARTZ_BLOCK.createBlockData());
								le.damage(3);
							});
						}
					}, count);
				}
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					invisible = false;
					boss.getEquipment().setArmorContents(ninjaarmor);
					boss.getEquipment().setItemInMainHand(ninjasword);
					boss.setNoDamageTicks(0);
					boss.removePotionEffect(PotionEffectType.SLOW);
					boss.removePotionEffect(PotionEffectType.INVISIBILITY);
					FireworkPlayer.fire(boss.getEyeLocation(), Type.BALL, Color.WHITE, false);
				}
			}, count);
		}
		else {
			Steel(boss, plugin);
		}
	}
    
    public static void Starstorm(LivingEntity boss, Plugin plugin) {
		for (int i = 0; i < 10; i ++) {
			int in = i;
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					if (in%4 == 0) {
						Player p = Utils.getNearestPlayer(boss.getLocation(), 10);
						if (p != null) {
							if (boss.getLocation().distance(p.getLocation()) < 4) {
								int s = (int) (Math.random() + 0.5) * 2;
								int b = 0;
								if (Math.random() > 0.5) {
									s = s * -1;
								}
								b = (int) (Math.random() + 0.8) * (-2);
								Location l = Utils.getBlockRelative(boss.getLocation(), b, s, 0);
								while (!l.getBlock().isPassable()) {
									l.add(new Vector(0, 1, 0));
								}
								l.setDirection(boss.getLocation().getDirection());
								boss.teleport(l);
							}
						}
					}
					Vector starv = boss.getLocation().getDirection();
					Random rand = new Random();
					starv.add(new Vector((rand.nextDouble() - 0.5), (rand.nextDouble() - 0.5)/2, (rand.nextDouble() - 0.5))); //Add the velocity by a random number
					ItemStack is = new ItemStack(Material.NETHER_STAR);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName("flyingblade" + in);
					is.setItemMeta(im);
					Item item = boss.getWorld().dropItem(boss.getEyeLocation(), is);
					item.setPickupDelay(Integer.MAX_VALUE);
					item.setVelocity(starv);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (item.isOnGround()) {
								item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.QUARTZ_BLOCK);
								item.remove();
								this.cancel();
							}
							Utils.applyNearbyPlayers(item.getLocation(), 2, (LivingEntity le) -> {
								item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.QUARTZ_BLOCK);
								item.remove();
								le.damage(5, boss);
								this.cancel();
							});
						}
					}.runTaskTimer(plugin, 1, 1);
				}
			},	(i*3));
		}
    }
    
    public static void Assault(LivingEntity boss, Plugin plugin) {
		assaulted = true;
		assaulting = true;
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 8));
		Player p = Utils.getNearestPlayer(boss.getLocation(), 20);
		Boss.Ready(boss, 30, Particle.CLOUD, plugin);
		if (p != null) {
			// Skill preset
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					invisible = true;
					boss.getEquipment().clear();
					boss.setNoDamageTicks(200);
					boss.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0));
					FireworkPlayer.fire(boss.getEyeLocation(), Type.BALL, Color.WHITE, false);
				}
			},	(30));
			for (int i = 1; i < 8; i++) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						FireworkPlayer.fire(boss.getEyeLocation(), Type.BURST, Color.WHITE, false);
						// Creating slice
						Location l1 = boss.getEyeLocation();
						Vector direction = p.getEyeLocation().toVector().subtract(l1.toVector()).normalize().multiply(0.8);
						Integer sliceDistance = (int) Math.floor((p.getEyeLocation().distance(boss.getEyeLocation())*2));
						if (sliceDistance < 40) {
							if (sliceDistance < 10) {
								sliceDistance = 10;
							}
							RayTrace r1 = new RayTrace(l1, direction, sliceDistance, 1);
							Integer count = 0;
							while (r1.hasNext()) {
								count++;
								Location l = r1.next();
								if (l.getBlock().isPassable()) {
									Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										public void run() {
											boss.teleport(l);
											boss.getWorld().spawnParticle(Particle.CLOUD, l, 4, 0, 0, 0, 0);
											Utils.applyNearbyPlayers(l, 2, (LivingEntity le) -> {
												le.damage(2);
												le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, 2));
												le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 6));
											});
										}
									},	count);
								}
							}
						}
					}
				},	(i*20) + 30);
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					invisible = false;
					assaulting = false;
					boss.getEquipment().setArmorContents(ninjaarmor);
					boss.getEquipment().setItemInMainHand(ninjasword);
					boss.setNoDamageTicks(0);
					boss.removePotionEffect(PotionEffectType.SLOW);
					boss.removePotionEffect(PotionEffectType.INVISIBILITY);
					FireworkPlayer.fire(boss.getEyeLocation(), Type.BALL, Color.WHITE, false);
				}
			},	200);
		}
    }
}
