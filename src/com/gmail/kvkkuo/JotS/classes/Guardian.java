package com.gmail.kvkkuo.JotS.classes;

import java.util.ArrayList;
import java.util.List;

import com.gmail.kvkkuo.JotS.utils.Geometry;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.util.Vector;

public class Guardian {
	public static String[] SKILLS = Utils.readSkillsFromCSV("guardian.csv");
	
	public static Integer cast(Player p, Integer spell, Integer cooldown, Integer upgrade, Plugin plugin) {
		if (cooldown <= 0) {
			if (spell.equals(0)) {
				if (upgrade.equals(0)) {
					Guardian.Crush(p, plugin);
				}
				if (upgrade.equals(1)) {
					Guardian.Smash(p, plugin);
				}
				if (upgrade.equals(2)) {
					Guardian.Rend(p, plugin);
				}
				if (upgrade.equals(3)) {
					Guardian.Pound(p, plugin);
				}
				cooldown = 12;
			}
			if (spell.equals(1)) {
				if (upgrade.equals(0)) {
					Guardian.Spines(p, plugin);
				}
				if (upgrade.equals(1)) {
					Guardian.Spirit(p, plugin);
				}
				if (upgrade.equals(2)) {
					Guardian.Kinetic(p, plugin);
				}
				if (upgrade.equals(3)) {
					Guardian.Mirror(p, plugin);
				}
				cooldown = 12;
			}
			if (spell.equals(2)) {
				if (upgrade.equals(0)) {
					Guardian.Howl(p, plugin);
				}
				if (upgrade.equals(1)) {
					Guardian.Growl(p, plugin);
				}
				if (upgrade.equals(2)) {
					Guardian.Roar(p, plugin);
				}
				if (upgrade.equals(3)) {
					Guardian.Call(p, plugin);
				}
				cooldown = 12;
			}
			if (spell.equals(3)) {
				Guardian.Totem(p, plugin, upgrade);
				cooldown = 20;
			}
		}
		return cooldown;
	}
	
	public static void Howl(Player p, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.ENTITY_WOLF_HOWL, 3, 1);
		FireworkPlayer.fire(p.getLocation(), Type.BALL_LARGE, Color.GRAY, false);
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			if (!e.equals(p) && e instanceof LivingEntity) {
				((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 0));
			}
		}
	}
	public static void Growl(Player p, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_DEATH, 3, 1);
		FireworkPlayer.fire(p.getLocation(), Type.BALL_LARGE, Color.ORANGE, false);
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			if (!e.equals(p) && e instanceof LivingEntity) {
				e.setFireTicks(80);
			}
		}
	}
	public static void Roar(Player p, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5, 1);
		FireworkPlayer.fire(p.getLocation(), Type.BALL_LARGE, Color.RED, false);
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			if (!e.equals(p) && e instanceof LivingEntity) {
				((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0));
				((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 80, 1));
			}
		}
	}
	public static void Call(Player p, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 5, 1);
		FireworkPlayer.fire(p.getLocation(), Type.BALL_LARGE, Color.BLACK, false);


		List<Location> list = new ArrayList<Location>();
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			if (e instanceof LivingEntity && !(e.equals(p))) {
				list.add(e.getLocation());
			}
		}
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			if (e instanceof LivingEntity && !(e.equals(p))) {
				LivingEntity le = (LivingEntity) e;
				TELEPORT:
				for (Location loc:list) {
					if (!le.getLocation().equals(loc)) {
						le.teleport(loc);
						list.remove(loc);
						break TELEPORT;
					}
				}
				le.damage(2);
				le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
				if (le instanceof Player) {
					((Player) le).sendMessage(p.getName() + "'s Ender Call has transported you elsewhere...");
				}
			}
		}
	}
	
	public static void Spines(Player p, Plugin plugin) {
		FireworkPlayer.fire(p.getLocation(), Type.BALL, Color.OLIVE, true);
		p.setMetadata("spines", new FixedMetadataValue(plugin, true));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.removeMetadata("spines", plugin);
				p.sendMessage("Your Spine Shield fades away.");
			}
		},	(200));
	}
	public static void Spirit(Player p, Plugin plugin) {
		FireworkPlayer.fire(p.getLocation(), Type.BALL, Color.SILVER, true);
		p.setMetadata("spirit", new FixedMetadataValue(plugin, true));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.removeMetadata("spirit", plugin);
				p.sendMessage("Your Spirit Shield fades away.");
			}
		},	(200));
	}
	public static void Kinetic(Player p, Plugin plugin) {
		FireworkPlayer.fire(p.getLocation(), Type.BALL, Color.PURPLE, true);
		p.setMetadata("kinetic", new FixedMetadataValue(plugin, true));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.removeMetadata("kinetic", plugin);
				p.sendMessage("Your Kinetic Shield releases its energy.");
				
			}
		},	(200));
	}
	public static void Mirror(Player p, Plugin plugin) {
		Integer count = 0;
		Integer interval = 5;
		Integer duration = 160;
		while (count*interval < duration) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 5, 1, 1, 1, 0.2);
					for (Entity e:p.getNearbyEntities(6, 6, 6)) {
						if (e instanceof WitherSkull) {
							p.getWorld().createExplosion(e.getLocation(), 0);
							e.remove();
						}
						if (!(e instanceof LivingEntity)) {
							e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).multiply(0.3));
						}
					}
				}
			},	interval*count);
			count++;
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				p.sendMessage("Your Mirror Shield fades away.");
			}
		},	(duration));
	}
	
	public static void Crush(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);

		int range = 7, count = 0, magnitude = 1;
		Location start = p.getLocation();
		Vector dir = p.getLocation().getDirection().setY(0).normalize().multiply(0.8);
		Vector norm = new Vector(-dir.getZ(), 0, dir.getX()).multiply(0.8);
		RayTrace eye = new RayTrace(start, dir, range, magnitude);
		for (count = 0; count < range; count += magnitude) {
			Location center = eye.next().clone();
			Location groundLoc = Geometry.getGroundLocation(center, 6);
			List<Location> perpLine = Geometry.getPerpendicularLine(groundLoc, norm, 4);
			final int c = count;
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l : perpLine) {
						p.getWorld().spawnParticle(Particle.TOWN_AURA, l, 4, 0.1, 0, 0.1, 0);
						Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
						});
					}
					if (c % 2 == 0) {
						w.playSound(start, Sound.BLOCK_GRAVEL_HIT, 1, 1);
					}
				}
			}.runTaskLater(plugin, count);

			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l : perpLine) {
						p.getWorld().spawnParticle(Particle.CRIT, l, 4, 0.1, 0, 0.1, 0);
						Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
							le.damage(4, p);
						});
					}
					if (c % 2 == 0) {
						w.playSound(start, Sound.BLOCK_GRAVEL_BREAK, 1, 1);
					}
				}
			}.runTaskLater(plugin, 15+count);
		}
	}
	public static void Smash(Player p, Plugin plugin) {
	}
	public static void Rend(Player p, Plugin plugin) {
	}
	public static void Pound(Player p, Plugin plugin) {
	}
	
	public static boolean setTotem(Location loc, Material m, int duration, Plugin plugin) {
		Location loc2 = loc.clone();
		loc2.setY(loc.getY() + 1);
		Block b1 = loc.getBlock();
		Block b2 = loc2.getBlock();
		Material o1 = b1.getType();
		Material o2 = b2.getType();
		if (b1.isEmpty() && b2.isEmpty()) {
			b1.setType(m);
			b2.setType(m);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					b1.setType(o1);
					b2.setType(o2);
				}
			},	(duration));
			return true;
		}
		return false;
	}
	public static void Totem(Player p, Plugin plugin, int type) {
		Material m = Material.GREEN_STAINED_GLASS;
				
		Item item = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(m));
		item.setVelocity(p.getLocation().getDirection().multiply(0.5));
		item.setPickupDelay(Integer.MAX_VALUE);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (item.isOnGround()) {
					Location loc = item.getLocation();
					Integer duration = 200;
					if (setTotem(loc, m, duration, plugin)) {
						Integer count = 0;
						Integer interval = 40;
						for (count = 0; count*interval < duration; count++) {
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() {
									for (Entity e:loc.getWorld().getNearbyEntities(loc, 6, 6, 6)) {
										if (e instanceof LivingEntity) {
											if (type == 0) {
												((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
											}
										}
									}
								}
							},	interval*count);
						}
					}
					else {
						p.sendMessage("Totem requires two blocks of vertical space.");
					}
					FireworkPlayer.fire(loc, Type.BALL_LARGE, Color.FUCHSIA, false);
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
}
