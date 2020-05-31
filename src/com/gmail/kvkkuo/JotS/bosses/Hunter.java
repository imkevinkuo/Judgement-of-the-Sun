package com.gmail.kvkkuo.JotS.bosses;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.Geometry;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

public class Hunter extends Boss {
	
	public static boolean melee = false;
	public static boolean invisible = false;
	public static boolean wolfed = false;
    
    public static void Spawn(Location l, Plugin plugin, EntityType et) {
    	invisible = false;
    	wolfed = false;
    	LivingEntity boss = (LivingEntity) l.getWorld().spawnEntity(l, et);
    	// Global settings
    	Boss.spawnActions(boss, Boss.getRace(et) + " Hunter", plugin);
    	// Set Equipment
		boss.getEquipment().setArmorContents(hunterarmor);
		boss.getEquipment().setItemInMainHand(hunterbow);
		boss.getEquipment().setItemInMainHandDropChance(100);
    	// Begin abilties
    	Hunter.Fight(boss, plugin);
    }
    
    public static void Fight(LivingEntity boss, Plugin plugin) {
	    	new BukkitRunnable() {
				@Override
				public void run() {
					if (boss.isDead()) {
						this.cancel();
					}
					// Wolf targeting
					// make nearby wolves angry - see if needed
					boolean noknockback = boss.hasMetadata("noknockback");
					boolean invisible = boss.hasMetadata("invisible");
					// Skills
					if (!noknockback && !invisible && !boss.isDead()) {
						melee = false;
						for (Entity e : boss.getNearbyEntities(8, 8, 8)) {
							if (e.getType().equals(EntityType.PLAYER)) {
								melee = true;
							}
						}
						double d = Math.random();
						if (wolfed) {
							double w = Math.random();
							if (w > 0.8) {
								Hunter.Wolf(boss, plugin);
							}
						}
						if (melee) {
							boss.getEquipment().setItemInMainHand(huntersword);
							// 20% chance
							if (d < 0.2) {
								Hunter.Bomb(boss, plugin);
							}
							// 40% chance
							if (d >= 0.2 && d < 0.6) {
								Hunter.Smash(boss, plugin);
							}
							// 40% chance
							if (d >= 0.6) {
								Hunter.Cleave(boss, plugin);
							}
						} else {
							boss.getEquipment().setItemInMainHand(hunterbow);
							// 20% chance
							if (d < 0.2) {
								Hunter.Whip(boss, plugin);
							}
							// 40% chance
							if (d >= 0.2 && d < 0.6) {
								Hunter.Trap(boss, plugin);
							}
							// 40% chance
							if (d >= 0.6) {
								Hunter.Arrows(boss, plugin);
							}
						}
					}
				}
			}.runTaskTimer(plugin, 100, 80);
    }
    
    // Knocks back players in a straight line
    public static void Smash(LivingEntity boss, Plugin plugin) {
		Ready(boss, 20, Particle.SMOKE_NORMAL, plugin);
		Player p = Utils.getNearestPlayer(boss.getLocation(), 8);
		if (p != null) {
			Location l1 = boss.getEyeLocation();
			Vector direction = p.getEyeLocation().toVector().subtract(l1.toVector()).normalize();
			RayTrace r1 = new RayTrace(l1, direction,7, 1);
			Integer count = 0;
			while (r1.hasNext()) {
				count++;
				Location l = r1.next();
				if (l.getBlock().isPassable()) {
					double factor = (count * 0.125) + 1;
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							boss.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, l, 10, (float) 0.5, (float) 0.6, (float) 0.5);
							Utils.applyNearbyPlayers(l, 2, (LivingEntity le) -> {
								le.damage(6, boss);
								if (factor > 1.5) {
									le.setVelocity(direction.clone().setY(0.5));
								}
								le.setVelocity(direction.clone().multiply(factor));
								boss.getWorld().playSound(le.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
							});
						}
					}, (count * 2) + 10);
				}
			}
		}
    }
    
    public static void Cleave(LivingEntity boss, Plugin plugin) {
		Ready(boss, 20, Particle.SMOKE_NORMAL, plugin);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_WITHER_SHOOT, 10, 1);
				for (int radius = 1; radius < 5; radius++) {
					List<Location> points = Geometry.getCirclePoints(boss.getEyeLocation(), Geometry.Plane.XZ, radius, radius*6);
					int count = 1;
					for (Location loc : points) {
						count++;
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								boss.getWorld().spawnParticle(Particle.REDSTONE, loc, 4, 0, 0, 0);
								boss.getWorld().spawnParticle(Particle.REDSTONE, loc.add(0, -1, 0), 4, 0, 0, 0);
							}
						}, ((int)count/3));
					}
				}
				Utils.applyNearbyPlayers(boss.getLocation(), 5, (LivingEntity le) -> {
					le.damage(6, boss);
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 3));
				});
			}
		}, (10));
    }
    
    public static void Wolf(LivingEntity boss, Plugin plugin) {
    	for (Entity e:boss.getNearbyEntities(25, 25, 25)) {
    		if (e.getType().equals(EntityType.PLAYER)) {
    			Player p = (Player) e;
    			Wolf w = (Wolf) p.getWorld().spawnEntity(boss.getLocation(), EntityType.WOLF);
				w.setMetadata("minion", new FixedMetadataValue(plugin, true));
    			w.setAngry(true);
    			w.setTarget(p);
    			w.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    			w.setHealth(6);
    			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    				public void run() {
    					w.damage(0, p);
    				}
    			},	(1));
    		}
    	}
    }

    // Ranged Skills
    public static void Arrows(LivingEntity boss, Plugin plugin) {
    	for (Entity e:boss.getNearbyEntities(20, 20, 20)) {
    		if (e instanceof Player) {
    			Player p = (Player) e;
		    	int[] angles = {30, 20, 10, 0, -10, -20, -30};
				Location location = boss.getLocation();
				Vector direction = p.getLocation().toVector().subtract(boss.getLocation().toVector());
			    direction.normalize();
			    // to get a vector pointing in the player's view direction, but on the x-z-plane
				// only without problems when looking straight up (x, z = 0 then)
			    Vector dirY = (new Location(location.getWorld(), 0, 0, 0, location.getYaw(), 0)).getDirection();
			    for (int r = 0; r < 3; r ++) {
				    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
						    for (int angle : angles) {
							    Vector vec;
							    if (angle != 0) {
							        vec = Geometry.rotateYAxis(dirY, angle);
							        vec.multiply(Math.sqrt(vec.getX() * vec.getX() + vec.getZ() * vec.getZ())).subtract(dirY);
							        vec = direction.clone().add(vec).normalize();
							    } else {
							        vec = direction.clone();
							    }
							    Arrow arrow = boss.launchProjectile(Arrow.class);
							    arrow.setShooter(boss);
							    arrow.setVelocity(vec.clone().multiply(2).add(new Vector((Math.random() - 0.5)/2, 0.2, (Math.random() - 0.5)/2)));
					    	}
						}
					},	(r*3));
			    }
			    break;
    		}
    	}
    }
    
    public static void Whip(LivingEntity boss, Plugin plugin) {
    	boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 4, 1);

		for (int i = 0; i < 8; i ++) {
			ItemStack is = new ItemStack(Material.TRIPWIRE_HOOK);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("hook" + i + boss.getCustomName());
			is.setItemMeta(im);
			
			Item item = boss.getWorld().dropItem(boss.getEyeLocation(), is);
			item.setVelocity(boss.getLocation().getDirection().multiply(0.4 + 0.12*i));
			item.setPickupDelay(Integer.MAX_VALUE);
			 
			BukkitTask bt = new BukkitRunnable() {
				@Override
				public void run() {
			    	for (Entity e : item.getNearbyEntities(1, 1, 1)) {
			    		if (e.getType().equals(EntityType.PLAYER)) {
			    			Hunter.Cleave(boss, plugin);
    						LivingEntity le = (LivingEntity) e;
    						le.damage(2, boss);
    						le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 4));
    						Vector vl = (boss.getEyeLocation().toVector().subtract(le.getLocation().toVector()).multiply(0.2));
							le.setVelocity(vl);
    						for (Entity en : boss.getWorld().getNearbyEntities(e.getLocation(), 20,20, 20)) {
    							if (en.getType().equals(EntityType.DROPPED_ITEM)) {
    								Item ie = (Item) en;
    								if (ie.getItemStack().getType().equals(Material.TRIPWIRE_HOOK)) {
    									if (ie.getItemStack().hasItemMeta()) {
    										ItemMeta em = ie.getItemStack().getItemMeta();
    										if (em.getDisplayName().startsWith("hook") && em.getDisplayName().endsWith(boss.getCustomName())) {
			    								Vector ve = (boss.getEyeLocation().toVector().subtract(en.getLocation().toVector()).multiply(0.3));
			    								en.setVelocity(ve);
					    						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					    	    					public void run() {
					    	    						en.remove();
					    	    					}
					    	    				}, 5);
    										}
    									}
	    							}
    							}
		    				}
    						this.cancel();
			    		}
			    	}
				}
			}.runTaskTimer(plugin, 1, 1);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					if (item.getTicksLived() >= 55) {
						boss.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.TRIPWIRE_HOOK);
					}
					item.remove();
					bt.cancel();
				}
			}, 60);
		}
    }
    
    public static void Trap(LivingEntity boss, Plugin plugin) {
		Item item = boss.getWorld().dropItem(boss.getEyeLocation(), new ItemStack(Material.CACTUS));
		item.setPickupDelay(Integer.MAX_VALUE);
		item.setVelocity(new Vector(0,1,0));
		new BukkitRunnable() {
		@Override
			public void run() {
				if (item.isOnGround()) {
					BukkitTask bt = new BukkitRunnable() {
						@Override
						public void run() {
						for (Location loca:Geometry.getCirclePoints(item.getLocation(), Geometry.Plane.XZ, 5, 100)) {
							Location newloc = loca.clone();
							Location below = loca.add(0, -1, 0);
							while (below.getBlock().isPassable()) {
								newloc = newloc.add(0, -1, 0);
								below = below.add(0, -1, 0);
							}
							while (!newloc.getBlock().isPassable()) {
								newloc = newloc.add(0, 1, 0);
								below = below.add(0, 1, 0);
							}
							boss.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, newloc, 1, 0, 0, 0);
						}
						for (Entity en : item.getNearbyEntities(5, 5, 5)) {
							if (en.getType().equals(EntityType.PLAYER)) {
								LivingEntity le = (LivingEntity) en;
								le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
								le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
							}
						}
						}
					}.runTaskTimer(plugin, 20, 40);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							bt.cancel();
						}
					}, 200);
					FireworkPlayer.fire(item.getLocation(), Type.BALL_LARGE, Color.GREEN, false, false, false);
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
    
    public static void Bomb(LivingEntity boss, Plugin plugin) {
    	LivingEntity le = Utils.getNearestPlayer(boss.getLocation(), 25);
		// Skill
    	Item item = boss.getWorld().dropItem(boss.getEyeLocation(), new ItemStack(Material.QUARTZ_BLOCK));
    	item.setPickupDelay(Integer.MAX_VALUE);
    	if (le != null) {
    		item.setVelocity(le.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize());
    	}
    	else {
    		item.setVelocity(boss.getLocation().getDirection().normalize());
    	}
		new BukkitRunnable() {
		@Override
			public void run() {
		    	if (item.isOnGround()) {
		    		Utils.applyNearbyPlayers(item.getLocation(), 3, (LivingEntity le) -> {
						le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 6));
						le.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 80, 149));
					});
		    		FireworkPlayer.fire(item.getLocation(), Type.BALL, Color.GRAY, false, false, false);
		    		item.remove();
		    		this.cancel();
		    	}
			}
		}.runTaskTimer(plugin, 1, 1);
    }

    public static void WolfPack(LivingEntity boss, Plugin plugin) {
		wolfed = true;
		// Charging
		boss.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 8));
		Location cp = boss.getLocation().subtract(0, 1, 0);
		List<Location> circle = Geometry.getCirclePoints(cp, Geometry.Plane.XZ, 2);
		Boss.Ready(boss, 30, Particle.REDSTONE, plugin);
		// Spawn Wolves
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				for (int i = 0; i < 5; i++) {
					Wolf w = (Wolf) boss.getWorld().spawnEntity(boss.getEyeLocation(), EntityType.WOLF);
					w.setMetadata("minion", new FixedMetadataValue(plugin, true));
					for (Entity e : w.getNearbyEntities(20, 20, 20)) {
						if (e.getType().equals(EntityType.PLAYER)) {
							Player p = (Player) e;
							w.setAngry(true);
							w.setTarget(p);
							w.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
							w.setHealth(6);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() {
									w.damage(0, p);
								}
							}, (1));
						}
					}
				}
			}
		},	(30));
    }
}
