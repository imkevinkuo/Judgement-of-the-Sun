package com.gmail.kvkkuo.JotS.classes;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Witherknight {
	public static String[] SKILLS = Utils.readSkillsFromCSV("witherknight.csv");
	static String[] TRAP_NAMES = new String[]{"Trap", "Curse", "Snare", "Shroud"};
	static PotionEffect[] TRAP_EFFECTS = new PotionEffect[]{
			new PotionEffect(PotionEffectType.POISON, 80, 2),
			new PotionEffect(PotionEffectType.SLOW_DIGGING, 80, 2),
			new PotionEffect(PotionEffectType.SLOW, 80, 2),
			new PotionEffect(PotionEffectType.BLINDNESS, 80, 2)
	};

	public static Integer cast(Player p, Integer spell, Integer cooldown, Integer upgrade, Plugin pl) {
		if (cooldown <= 0) {
			if (spell.equals(0)) {
				if (upgrade.equals(0)) {
					Witherknight.Duskwave(p, pl);
				}
				if (upgrade.equals(1)) {
					Witherknight.Coldquake(p, pl);
				}
				if (upgrade.equals(2)) {
					Witherknight.Arcnight(p, pl);
				}
				if (upgrade.equals(3)) {
					Witherknight.Soulgrip(p, pl);
				}
				cooldown = 12;
			}
			if (spell.equals(1)) {
//				Passive spell, do nothing
//				if (upgrade.equals(0)) {
//					Witherknight.Barrier(p, cooldown, pl);
//				}
//				if (upgrade.equals(1)) {
//					Witherknight.Drain(p, cooldown, pl);
//				}
//				if (upgrade.equals(2)) {
//					Witherknight.Wisp(p, cooldown, pl);
//				}
//				if (upgrade.equals(3)) {
//					Witherknight.Rebirth(p, cooldown, pl);
//				}
//				if (c) {cooldown = 12;}
			}
			if (spell.equals(2)) {
				if (upgrade.equals(0)) {
					Witherknight.Spear(p, pl);
				}
				if (upgrade.equals(1)) {
					Witherknight.Slash(p, pl);
				}
				if (upgrade.equals(2)) {
					Witherknight.Stab(p, pl);
				}
				if (upgrade.equals(3)) {
					Witherknight.Slice(p, pl);
				}
				cooldown = 20;
			}
			if (spell.equals(3)) {
				Witherknight.Trap(p, pl, upgrade);
				cooldown = 20;
			}
		}
		return cooldown;
	}

	public static void Duskwave(Player p, Plugin pl) {

	}

	public static void Coldquake(Player p, Plugin plugin) {

	}

	public static void Arcnight(Player p, Plugin pl) {
		ArrayList<LivingEntity> affected = new ArrayList<>();
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
		affected.add(p);
		Archelper(p, affected, new RayTrace(p, 6, 1), pl);
	}

	private static void Archelper(Player p, ArrayList<LivingEntity> affected, RayTrace ray, Plugin pl) {
		World w = p.getWorld();
		for (int i = 1; i < 7; i++) {
			new BukkitRunnable() {
				public void run() {
					if (ray.hasNext()) {
						Location l = ray.next();
						w.spawnParticle(Particle.FIREWORKS_SPARK, l, 2, 0, 0, 0, 0.05);
						Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {
							if (!affected.contains(le)) {
								w.playSound(l, Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
								w.spawnParticle(Particle.FIREWORKS_SPARK, le.getEyeLocation(), 8, 0, 0.5, 0, 0.05);
								Utils.magicDamage(p, le, 4, pl);
								le.setVelocity(new Vector(0, 0, 0));
								affected.add(le);
								LivingEntity le2 = Utils.getNearestEntity(l, affected, 5, 3, 5);
								if (le2 != null) {
									Vector diff = le2.getLocation().subtract(le.getLocation()).toVector();
									RayTrace v = new RayTrace(le.getEyeLocation(), diff, diff.length(), diff.length() / 7);
									new BukkitRunnable() {
										public void run() {
											Archelper(p, affected, v, pl);
										}
									}.runTaskLater(pl, 4);
								}
							}
						});
					}
				}
			}.runTaskLater(pl, i);
		}
	}

	public static void Soulgrip(Player p, Plugin plugin) {

	}

	// Triggers for these will need to be in a listener
	public static void Barrier(Player p, Plugin pl) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0));
	}

	public static void Drain(Player p, Plugin pl) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 0));
	}

	public static void Wisp(Player p, Plugin pl) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
	}

	public static void Rebirth(Player p, Plugin pl) {
		p.setHealth(p.getHealth() + 1);
	}

	public static void Spear(Player p, Plugin pl) {
		int range = 10;
		int space = 1;

		World w = p.getWorld();
		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(p.getLocation().getDirection().setY(0).multiply(-1));
		new BukkitRunnable() {
			public void run() {
				p.setVelocity(p.getLocation().getDirection().setY(0));
				w.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);

				RayTrace eye = new RayTrace(p, range, space);
				for (int count = 0; count < (int) range / space; count++) {
					new BukkitRunnable() {
						public void run() {
							Location l = eye.next();
							if (l.getBlock().getType().equals(Material.AIR)) {
								w.spawnParticle(Particle.CRIT_MAGIC, l, 4, 0, 0, 0, 0.3);
								Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {
									le.damage(6, p);
									le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
									le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
								});
							}
						}
					}.runTaskLater(pl, count);
				}
			}
		}.runTaskLater(pl, 10);
	}

	public static void Slash(Player p, Plugin pl) {
		WitherSkull s1 = p.launchProjectile(WitherSkull.class);
		s1.setShooter(p);
		p.setVelocity(p.getLocation().getDirection().setY(0.5));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			public void run() {
				WitherSkull s2 = p.launchProjectile(WitherSkull.class);
				s2.setShooter(p);
			}
		}, 10);
		new BukkitRunnable() {
			public void run() {
				p.setVelocity(p.getLocation().getDirection().setY(-0.5));
			}
		}.runTaskLater(pl, 20);
	}

	public static void Stab(Player p, Plugin pl) {
		int range = 10;
		int space = 1;

		World w = p.getWorld();
		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(p.getLocation().getDirection().setY(0).multiply(-1));
		new BukkitRunnable() {
			public void run() {
				p.setVelocity(p.getLocation().getDirection().setY(0));
				w.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);

				RayTrace eye = new RayTrace(p, range, space);
				for (int count = 0; count < (int) range / space; count++) {
					new BukkitRunnable() {
						public void run() {
							Location l = eye.next();
							if (l.getBlock().getType().equals(Material.AIR)) {
								w.spawnParticle(Particle.CRIT_MAGIC, l, 4, 0, 0, 0, 0.3);
								Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {
									le.damage(6, p);
									le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
									le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
								});
							}
						}
					}.runTaskLater(pl, count);
				}
			}
		}.runTaskLater(pl, 10);
	}

	public static void Slice(Player p, Plugin pl) {

	}

	public static void Trap(Player p, Plugin pl, int t) {
		Item trap = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.NETHER_QUARTZ_ORE));
		trap.setPickupDelay(Integer.MAX_VALUE);
		ArrayList<LivingEntity> excludePlayer = new ArrayList<>();
		excludePlayer.add(p);
		BukkitTask bt = new BukkitRunnable() {
			public void run() {
				LivingEntity nearest = Utils.getNearestEntity(trap.getLocation(), excludePlayer, 5, 5, 5);
				if (nearest != null) {
					Utils.applyNearby(trap.getLocation(), p, 5, 5, 5, (LivingEntity le) -> {
						le.damage(4, p);
						le.addPotionEffect(TRAP_EFFECTS[t]);
						if (le instanceof Player) {
							le.sendMessage(p.getName() + "'s nearby Shadow " + TRAP_NAMES[t] + " was triggered!");
						}
					});
					// Teleport player
					p.sendMessage("Your Shadow " + TRAP_NAMES[t] + " has been triggered!");
					trap.getWorld().playSound(trap.getLocation(), Sound.ENTITY_CREEPER_HURT, 5, 1);
					FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false);
					Location target = trap.getLocation();
					trap.remove();

					target.add(0, 12, 0);
					for (int i = 0; i < 5; i++) {
						int in = i;
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
							public void run() {
								FireworkPlayer.fire(target, Type.BURST, Color.GRAY, false);
								if (in == 4) {
									p.teleport(target);
								} else {
									target.subtract(0, 3, 0);
								}
							}
						}, (40 + i * 5));
					}

					this.cancel();
				}
			}
		}.runTaskTimer(pl, 1, 5);
		new BukkitRunnable() {
			public void run() {
				if (!bt.isCancelled()) {
					bt.cancel();
					FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false);
					trap.remove();
				}
			}
		}.runTaskLater(pl, 600);
	}
}