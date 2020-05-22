package com.gmail.kvkkuo.JotS.bosses;

import com.gmail.kvkkuo.JotS.utils.Geometry;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.*;
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

import java.util.Random;

public class Warlock extends Boss {
	
	public static Boolean cloned = false;
	public static Boolean cloning = false;
    
    public static void Spawn(Location l, Plugin plugin, EntityType et) {
    	LivingEntity boss = (LivingEntity) l.getWorld().spawnEntity(l, et);
    	// Global settings
    	Boss.spawnActions(boss, Boss.getRace(et) + " Warlock", plugin);
    	// Set Armor
    	boss.getEquipment().setArmorContents(warlockarmor);
		boss.getEquipment().setItemInMainHand(warlockbow);
		boss.getEquipment().setHelmetDropChance(80);
		boss.getEquipment().setItemInMainHandDropChance(80);
    	// Begin abilties
    	cloned = false;
    	Warlock.Fight(boss, plugin);
    }
    
    public static void RemoveItems(LivingEntity le, Plugin plugin) {
    	for (Entity en:le.getNearbyEntities(100, 100, 100)) {
			if (en.getType().equals(EntityType.DROPPED_ITEM)) {
				Item item = (Item) en;
				if (item.hasMetadata("blastbone")) {
					le.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation(), 4, 0, 0, 0);
					item.remove();
				}
				ItemStack is = item.getItemStack();
				ItemMeta im = is.getItemMeta();
				String name = im.getDisplayName();
				if (is.getType().equals(Material.BONE) && name != null && name.startsWith("skbone")) {
					le.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation(), 4, 0, 0, 0);
					item.remove();
				}
			}
			if (en.getType().equals(EntityType.SKELETON)) {
				en.removeMetadata("minion", plugin);
			}
		}
    }
    
    public static void Fight(LivingEntity sk, Plugin plugin) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!sk.isDead()) {
						if (!cloning) {
							double d = Math.random();
							if (d < 0.4) {
								Warlock.Skulls(sk, plugin);
							}
							if (d >= 0.4 && d < 0.6) {
								Warlock.Push(sk, plugin);
							}
							if (d >= 0.6 && d < 0.7) {
								Warlock.FireArrows(sk, plugin);
							}
							if (d >= 0.7) {
								Warlock.ArrowStorm(sk, plugin);
							}
						}
					}
					else {
						this.cancel();
					}
				}
			}.runTaskTimer(plugin, 80, 100);
    }
    
    public static void Push(LivingEntity boss, Plugin plugin) {
    	if (Utils.getNearestPlayer(boss.getLocation(), 6) == null) {
			Warlock.Aura(boss, plugin);
		}
    	else {
			boss.getWorld().createExplosion(boss.getLocation(), 0);
			Utils.applyNearbyPlayers(boss.getLocation(), 6, (LivingEntity le) -> {
				le.setVelocity(le.getLocation().toVector().subtract(boss.getLocation().toVector()).normalize().multiply(2).setY(0.5));
			});
		}
    }
    
    public static void Skulls(LivingEntity boss, Plugin plugin) {
    	Integer count = 0;
    	for (Entity e:boss.getNearbyEntities(12, 12, 12)) {
			if (e.getType().equals(EntityType.DROPPED_ITEM)) {
				Item item = (Item) e;
				if (item.hasMetadata("blastbone")) {
					count++;
					Integer cn = count;
					BukkitTask bt = new BukkitRunnable() {
						@Override
						public void run() {
							Integer live = item.getTicksLived();
							Double d = (double) (live/4);
							Integer rn = (int) Math.ceil(d);
							item.setVelocity(Geometry.getSquareLocation(Geometry.getBlockRelative(boss.getEyeLocation(), 0, 0, 4), cn, rn, 3).toVector().subtract(item.getLocation().toVector()).multiply(0.2));
						}
					}.runTaskTimer(plugin, 4, 1);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							bt.cancel();
							item.setVelocity(boss.getEyeLocation().getDirection());
							new BukkitRunnable() {
								@Override
								public void run() {
							    	if (item.isOnGround()) {
							    		Utils.applyNearbyPlayers(item.getLocation(), 2, (LivingEntity le) -> {
											le.damage(6);
											le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2));
											le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 8));
										});
							    		item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.STONE);
							    		item.remove();
							    		this.cancel();
							    	}
								}
							}.runTaskTimer(plugin, 1, 1);
						}
					}, 40);
				}
			}
		}
    	if (count == 0) {
    		Warlock.Aura(boss, plugin);
    	}
    }
    
    public static void Aura(LivingEntity sk, Plugin plugin) {
		for (int r = 1; r < 5; r ++) {
			Integer rn = r;
			ItemStack is = new ItemStack(Material.BONE);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("skbone" + rn);
			is.setItemMeta(im);
			Item item = sk.getWorld().dropItem(Geometry.getSquareLocation(sk.getEyeLocation(), rn, 0, 2), is);
			item.setPickupDelay(Integer.MAX_VALUE);
			item.setVelocity(new Vector(0, 0.2, 0));
			new BukkitRunnable() {
				@Override
				public void run() {
					if (item.getTicksLived() < 100) {
						Integer pre = item.getTicksLived();
						Double d = (double) (pre/4);
						Integer lived = (int) Math.ceil(d);
						item.setVelocity(Geometry.getSquareLocation(sk.getEyeLocation(), rn, lived, 2).toVector().subtract(item.getLocation().toVector()).multiply(0.2).add(new Vector(0, 0.1, 0)));
					}
					else {
						sk.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, item.getLocation(), 4, 1, 1, 1);
						sk.getWorld().playSound(sk.getLocation(), Sound.ENTITY_WITCH_DRINK, 1, 10);
						item.remove();
						this.cancel();
					}
				}
			}.runTaskTimer(plugin, 4, 1);
		}
    }
    
    public static void Twin(LivingEntity boss, Plugin plugin) {
    	if (!cloned) {
	    	cloned = true;
	    	cloning = true;
			// Preparation
			boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_SKELETON_DEATH, 1, 10);
			boss.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, boss.getLocation(), 12, 1, 2, 1);
	    	for (Entity e:boss.getNearbyEntities(12, 12, 12)) {
	    		if (e.getType().equals(EntityType.DROPPED_ITEM)) {
	    			Item item = (Item) e;
	    			Material mat = item.getItemStack().getType();
	    			if (mat.equals(Material.SKELETON_SKULL) || mat.equals(Material.BONE)) {
						boss.getWorld().spawnParticle(Particle.SMOKE_NORMAL, item.getLocation(), 8, 1, 1, 1);
	    				item.remove();
	    			}
	    		}
			}
	    	boss.getEquipment().clear();
	    	boss.setNoDamageTicks(60);
	    	boss.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 8));
	    	boss.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0));
			// Create itemdrops, will spawn clones
			for (int r = 0; r < 3; r ++) {
				Vector v = new Vector(Math.random(), 0, Math.random());
				for (int r2 = 0; r2 < 2; r2++) {
					if (r2 == 1) {
						v.multiply(-1);
					}
					ItemStack is = new ItemStack(Material.BONE);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName("twinbone" + r + r2);
					is.setItemMeta(im);
					Item item = boss.getWorld().dropItem(boss.getLocation(), is);
					item.setPickupDelay(Integer.MAX_VALUE);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							item.setVelocity(new Vector(0, 1, 0));
						}
					}, 20);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							item.setVelocity(v);
						}
					}, 40);
					if (r != 0) {
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								item.remove();
								item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.STONE);
								// Spawn skelly
								LivingEntity clone = (LivingEntity) boss.getWorld().spawnEntity(item.getLocation(), EntityType.SKELETON);
								clone.getEquipment().setArmorContents(warlockarmor);
								clone.getEquipment().setItemInMainHand(warlockbow);
								clone.setHealth(6);
								clone.setCustomName("Skeleton Warlock");
								clone.setCustomNameVisible(true);
								clone.setMetadata("minion", new FixedMetadataValue(plugin, true));
							}
						}, 60);
					} else {
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								item.remove();
								item.getWorld().playEffect(item.getLocation(), Effect.STEP_SOUND, Material.STONE);
								boss.teleport(item.getLocation());
								boss.removePotionEffect(PotionEffectType.INVISIBILITY);
								boss.getEquipment().setArmorContents(warlockarmor);
								boss.getEquipment().setItemInMainHand(warlockbow);
								cloning = false;
							}
						}, 60);
					}
				}
				// Allow velocity
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						cloning = false;
					}
				}, (60));
			}
    	}
    }
    
    public static void FireArrows(LivingEntity boss, Plugin plugin) {
    	int[] angles = {45, 30, 15, 0, -15, -30, -45};
		Location location = boss.getLocation();
		Vector direction = boss.getLocation().getDirection();
	    direction.normalize();
	    // some trick, to get a vector pointing in the player's view direction, but on the x-z-plane only and without problems when looking straight up (x, z = 0 then)
	    Vector dirY = (new Location(location.getWorld(), 0, 0, 0, location.getYaw(), 0)).getDirection();
	    Integer count = 0;
	    for (int angle : angles) {
	    	count++;
	    	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
				    Vector vec;
				    if (angle != 0) {
				        vec = Geometry.rotateYAxis(dirY, angle);
				        vec.multiply(Math.sqrt(vec.getX() * vec.getX() + vec.getZ() * vec.getZ())).subtract(dirY);
				        vec = direction.clone().add(vec).normalize();
				    } else {
				        vec = direction.clone();
				    }
				    Arrow arrow = boss.launchProjectile(Arrow.class);
				    arrow.setFireTicks(Integer.MAX_VALUE);
				    arrow.setShooter(boss);
				    arrow.setVelocity(vec.clone().multiply(2).add(new Vector(0, 0.2, 0)));
				}
			},	(count*3));
    	}
    }
    public static void RainArrows(LivingEntity sk, Plugin plugin) {
    	for (int r = 0; r < 20; r ++) {
    		Arrow arrow = sk.launchProjectile(Arrow.class);
		    arrow.setShooter(sk);
		    arrow.setVelocity(new Vector((new Random().nextDouble() - 0.5)/2, 1, (new Random().nextDouble() - 0.5)/2));
    	}
    }
    
    public static void ArrowStorm(LivingEntity boss, Plugin plugin) {
    	Integer count = 0;
    	LivingEntity target = Utils.getNearestPlayer(boss.getLocation(), 12);
    	for (Entity e:boss.getNearbyEntities(5, 5, 5)) {
			if (e.getType().equals(EntityType.ARROW)) {
				boss.getWorld().spawnParticle(Particle.SMOKE_NORMAL, e.getLocation(), 4, 0, 0, 0);
				Arrow a = boss.launchProjectile(Arrow.class);
			    a.setShooter(boss);
			    a.teleport(Geometry.getBlockRelative(e.getLocation(), 0, 0, 1));
			    e.remove();
				count++;
				a.setVelocity(new Vector(0, 0.8, 0));
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						if (target != null) {
							Vector v = target.getEyeLocation().toVector().subtract(a.getLocation().toVector()).normalize();
							a.setVelocity(v.setY(v.getY()*0.5));
						}
						else {
							a.setVelocity(boss.getEyeLocation().toVector().subtract(a.getLocation().toVector()).normalize().multiply(-1).add(new Vector(0, 0.2, 0)));
						}
					}
				}, 10);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						a.remove();
					}
				}, 25);
			}
		}
    	if (count <= 6) {
    		Warlock.RainArrows(boss, plugin);
    	}
    }
}
