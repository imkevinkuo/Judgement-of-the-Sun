package com.gmail.kvkkuo.JotS.classes;

import java.util.*;

import com.gmail.kvkkuo.JotS.utils.Geometry;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import org.bukkit.scheduler.BukkitTask;
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
					Guardian.Starving(p, plugin);
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

	public static void Crush(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(new Vector(0, 0.5, 0));
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setVelocity(new Vector(0, -1, 0));
				w.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1, 1);
			}
		}.runTaskLater(plugin, 10);

		int range = 10, magnitude = 1;
		Location start = p.getLocation();
		Vector dir = p.getLocation().getDirection().setY(0).normalize().multiply(0.8);
		Vector norm = new Vector(-dir.getZ(), 0, dir.getX()).multiply(0.8);
		RayTrace eye = new RayTrace(start, dir, range, magnitude);
		for (int count = 0; count < range; count += magnitude) {
			Location center = eye.next().clone();
			Location groundLoc = Geometry.getGroundLocation(center, 6);
			List<Location> perpLine = Geometry.getLine(groundLoc, norm, 4);
			final int c = count;
			for (Location l : perpLine) {
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 1, 0.1, 0, 0.1, 0);
				Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
				});
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l : perpLine) {
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l, 4, 0.1, 0, 0.1, 0, Material.DIRT.createBlockData());
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l.add(0, 1, 0), 4, 0.1, 0, 0.1, 0, Material.DIRT.createBlockData());
						Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
							le.damage(4, p);
							le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
						});
					}
					if (c % 2 == 0) {
						w.playSound(start, Sound.BLOCK_GRASS_BREAK, 1, 1);
					}
				}
			}.runTaskLater(plugin, 10+count);
		}
	}
	public static void Smash(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(new Vector(0, 0.5, 0));
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setVelocity(new Vector(0, -1, 0));
				w.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1, 1);
			}
		}.runTaskLater(plugin, 10);

		int range = 10, magnitude = 1;
		Location start = p.getLocation();
		Vector dir = p.getLocation().getDirection().setY(0).normalize().multiply(0.8);
		Vector norm = new Vector(-dir.getZ(), 0, dir.getX()).multiply(0.8);
		RayTrace eye = new RayTrace(start, dir, range, magnitude);
		for (int count = 0; count < range; count += magnitude) {
			Location center = eye.next().clone();
			Location groundLoc = Geometry.getGroundLocation(center, 6);
			List<Location> perpLine = Geometry.getLine(groundLoc, norm, 4);
			final int c = count;
			for (Location l : perpLine) {
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 1, 0.1, 0, 0.1, 0);
				Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
				});
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l : perpLine) {
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l, 4, 0.1, 0, 0.1, 0, Material.STONE.createBlockData());
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l.add(0, 1, 0), 4, 0.1, 0, 0.1, 0, Material.STONE.createBlockData());
						Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
							le.damage(4, p);
							le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 2));
						});
					}
					if (c % 2 == 0) {
						w.playSound(start, Sound.BLOCK_STONE_BREAK, 1, 1);
					}
				}
			}.runTaskLater(plugin, 10+count);
		}
	}
	public static void Rend(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(new Vector(0, 0.5, 0));
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setVelocity(new Vector(0, -1, 0));
				w.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1, 1);
			}
		}.runTaskLater(plugin, 10);

		int range = 10, magnitude = 1;
		Location start = p.getLocation();
		Vector dir = p.getLocation().getDirection().setY(0).normalize().multiply(0.8);
		Vector norm = new Vector(-dir.getZ(), 0, dir.getX()).multiply(0.8);
		RayTrace eye = new RayTrace(start, dir, range, magnitude);
		for (int count = 0; count < range; count += magnitude) {
			Location center = eye.next().clone();
			Location groundLoc = Geometry.getGroundLocation(center, 6);
			List<Location> perpLine = Geometry.getLine(groundLoc, norm, 4);
			final int c = count;
			for (Location l : perpLine) {
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 1, 0.1, 0, 0.1, 0);
				Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
				});
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l : perpLine) {
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l, 4, 0.1, 0, 0.1, 0, Material.GLOWSTONE.createBlockData());
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l.add(0, 1, 0), 4, 0.1, 0, 0.1, 0, Material.GLOWSTONE.createBlockData());
						Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
							le.damage(4, p);
							le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0));
						});
					}
					if (c % 2 == 0) {
						w.playSound(start, Sound.BLOCK_GLASS_BREAK, 1, 1);
					}
				}
			}.runTaskLater(plugin, 10+count);
		}
	}
	public static void Pound(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(new Vector(0, 0.5, 0));
		new BukkitRunnable() {
			@Override
			public void run() {
				p.setVelocity(new Vector(0, -1, 0));
				w.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1, 1);
			}
		}.runTaskLater(plugin, 10);

		int range = 10, magnitude = 1;
		Location start = p.getLocation();
		Vector dir = p.getLocation().getDirection().setY(0).normalize().multiply(0.8);
		Vector norm = new Vector(-dir.getZ(), 0, dir.getX()).multiply(0.8);
		RayTrace eye = new RayTrace(start, dir, range, magnitude);
		for (int count = 0; count < range; count += magnitude) {
			Location center = eye.next().clone();
			Location groundLoc = Geometry.getGroundLocation(center, 6);
			List<Location> perpLine = Geometry.getLine(groundLoc, norm, 4);
			final int c = count;
			for (Location l : perpLine) {
				p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 1, 0.1, 0, 0.1, 0);
				Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
					le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
				});
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l : perpLine) {
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l, 4, 0.1, 0, 0.1, 0, Material.IRON_BLOCK.createBlockData());
						p.getWorld().spawnParticle(Particle.BLOCK_CRACK, l.add(0, 1, 0), 4, 0.1, 0, 0.1, 0, Material.IRON_BLOCK.createBlockData());
						Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
							le.damage(4, p);
							le.setVelocity(dir.setY(1));
						});
					}
					if (c % 2 == 0) {
						w.playSound(start, Sound.ENTITY_IRON_GOLEM_ATTACK, 1, 1);
					}
				}
			}.runTaskLater(plugin, 10+count);
		}
	}
	
	public static void Spines(Player p, Plugin plugin) {
		FireworkPlayer.fire(p.getEyeLocation(), Type.BALL, Color.OLIVE, false, false, false);
		p.setMetadata("spines", new FixedMetadataValue(plugin, true));
		Utils.rotateItems(p, Material.GREEN_DYE, "spine",200, p.getEyeHeight()/2, plugin);

		new BukkitRunnable() {
			public void run() {
				p.removeMetadata("spines", plugin);
				p.sendMessage("Your Spine Shield fades away.");
			}
		}.runTaskLater(plugin, 200);
	}
	public static void Starving(Player p, Plugin plugin) {
		FireworkPlayer.fire(p.getEyeLocation(), Type.BALL, Color.GREEN, false, false, false);
		p.setMetadata("starve", new FixedMetadataValue(plugin, true));
		p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0));
		BukkitTask task = new BukkitRunnable() {
			public void run() {
				p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation(), 5, 1, 1, 1, 0);
			}
		}.runTaskTimer(plugin, 0, 5);
		new BukkitRunnable() {
			@Override
			public void run() {
				task.cancel();
				p.removeMetadata("starve", plugin);
				p.sendMessage("Your Starving Shield fades away.");
			}
		}.runTaskLater(plugin, 200);
	}
	public static double StarvingTrigger(Player p, double eventDamage) {
		if (p.getFoodLevel() > eventDamage) {
			p.setFoodLevel((int) (p.getFoodLevel() - eventDamage));
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
			p.getWorld().spawnParticle(Particle.ITEM_CRACK, p.getEyeLocation(), 8, 0, 0, 0, 0, new ItemStack(Material.EMERALD));
			return 0;
		}
		return eventDamage;
	}
	public static void Kinetic(Player p, Plugin plugin) {
		FireworkPlayer.fire(p.getEyeLocation(), Type.BALL, Color.LIME, false, false, false);
		p.setMetadata("kinetic", new FixedMetadataValue(plugin, true));
		BukkitTask task = new BukkitRunnable() {
			public void run() {
				p.getWorld().spawnParticle(Particle.COMPOSTER, p.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
			}
		}.runTaskTimer(plugin, 0, 5);
		new BukkitRunnable() {
			@Override
			public void run() {
				task.cancel();
				p.removeMetadata("kinetic", plugin);
				p.sendMessage("Your Kinetic Shield releases its energy.");
				FireworkPlayer.fire(p.getEyeLocation(), Type.BALL, Color.LIME, false, false, false);
			}
		}.runTaskLater(plugin, 200);
	}
	public static void Mirror(Player p, Plugin plugin) {
		FireworkPlayer.fire(p.getEyeLocation(), Type.BALL, Color.GREEN, false, false, false);
		List<Item> mirrors = Utils.rotateItems(p, Material.GREEN_STAINED_GLASS_PANE, "mirror",160, 3*p.getEyeHeight()/4, plugin);
		List<Item> mirrors2 = Utils.rotateItems(p, Material.GREEN_STAINED_GLASS_PANE, "mirror",160, p.getEyeHeight()/4, plugin);
		BukkitTask task = new BukkitRunnable() {
			public void run() {
				for (Entity e:p.getNearbyEntities(6, 6, 6)) {
					if (!(e instanceof LivingEntity) && !mirrors.contains(e) && !mirrors2.contains(e)) {
						e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).multiply(0.3));
					}
				}
			}
		}.runTaskTimer(plugin, 0, 5);
		new BukkitRunnable() {
			@Override
			public void run() {
				task.cancel();
				p.sendMessage("Your Mirror Shield fades.");
			}
		}.runTaskLater(plugin, 160);
	}

	public static void Howl(Player p, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.ENTITY_WOLF_HOWL, 3, 1);
		FireworkPlayer.fire(p.getLocation(), Type.BALL_LARGE, Color.GRAY, false, false, false);
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			if (!e.equals(p) && e instanceof LivingEntity) {
				((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 0));
			}
		}
	}
	public static void Growl(Player p, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_DEATH, 3, 1);
		FireworkPlayer.fire(p.getLocation(), Type.BALL_LARGE, Color.ORANGE, false, false, false);
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			if (!e.equals(p) && e instanceof LivingEntity) {
				e.setFireTicks(80);
			}
		}
	}
	public static void Roar(Player p, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 5, 1);
		FireworkPlayer.fire(p.getLocation(), Type.BALL_LARGE, Color.RED, false, false, false);
		for (Entity e:p.getNearbyEntities(4, 4, 4)) {
			if (!e.equals(p) && e instanceof LivingEntity) {
				((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0));
				((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 80, 1));
			}
		}
	}
	public static void Call(Player p, Plugin plugin) {
		p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 5, 1);
		FireworkPlayer.fire(p.getLocation(), Type.BALL_LARGE, Color.BLACK, false, false, false);

		List<Entity> entities = p.getNearbyEntities(6, 6, 6);

		List<LivingEntity> livingEntities = new ArrayList<LivingEntity>();
		List<Location> locations = new ArrayList<Location>();
		for (Entity e: entities) {
			if (e instanceof LivingEntity && !(e.equals(p))) {
				livingEntities.add((LivingEntity) e);
				locations.add(e.getLocation());
			}
		}
		for (int i = 0; i < livingEntities.size(); i++) {
			LivingEntity le = livingEntities.get(i);
			le.teleport(locations.get((i + 1) % locations.size()));
			le.damage(2);
			le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
		}
	}
	
	public static boolean setTotem(Location loc, Material m, PotionEffect potionEffect, Plugin plugin) {
		FireworkPlayer.fire(loc, Type.BALL_LARGE, Color.FUCHSIA, false, false, false);
		Location loc2 = loc.clone();
		loc2.setY(loc.getY() + 1);
		Block b1 = loc.getBlock();
		Block b2 = loc2.getBlock();
		Material o1 = b1.getType();
		Material o2 = b2.getType();
		if (b1.isEmpty() && b2.isEmpty()) {
			b1.setType(m);
			b2.setType(m);

			BukkitTask task = new BukkitRunnable() {
				public void run() {
					Utils.applyNearby(loc, null, 6, 6, 6, (LivingEntity le) -> {
						loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc,16, 2, 2, 2, 0);
						le.addPotionEffect(potionEffect);
					});
				}
			}.runTaskTimer(plugin, 0, 20);

			new BukkitRunnable() {
				public void run() {
					task.cancel();
					b1.setType(o1);
					b2.setType(o2);
				}
			}.runTaskLater(plugin, 200);

			return true;
		}
		return false;
	}
	public static void Totem(Player p, Plugin plugin, int type) {
		Material material = Material.PINK_STAINED_GLASS;
		PotionEffect potionEffect = new PotionEffect(PotionEffectType.REGENERATION, 40, 1);

		if (type == 1) {
			material = Material.GRAY_STAINED_GLASS;
			potionEffect = new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0);
		}
		else if (type == 2) {
			material = Material.ORANGE_STAINED_GLASS;
			potionEffect = new PotionEffect(PotionEffectType.SATURATION, 20, 0);
		}
		if (type == 3) {
			material = Material.RED_STAINED_GLASS;
			potionEffect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0);
		}
		Item item = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(material));
		item.setVelocity(p.getLocation().getDirection().multiply(0.5));
		item.setPickupDelay(Integer.MAX_VALUE);

		PotionEffect finalPotionEffect = potionEffect;
		Material finalMaterial = material;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (item.isOnGround()) {
					if (!setTotem(item.getLocation(), finalMaterial, finalPotionEffect, plugin)) {
						p.sendMessage("Totem requires two blocks of vertical space.");
					}
					item.remove();
					this.cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
	}
}
