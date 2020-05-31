package com.gmail.kvkkuo.JotS.classes;

import com.gmail.kvkkuo.JotS.utils.Geometry;
import com.gmail.kvkkuo.JotS.utils.RayTrace;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.kvkkuo.JotS.utils.FireworkPlayer;
import com.gmail.kvkkuo.JotS.utils.Utils;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

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
					Witherknight.Noxburst(p, pl);
				}
				if (upgrade.equals(2)) {
					Witherknight.Arcwither(p, pl);
				}
				if (upgrade.equals(3)) {
					Witherknight.Nightfall(p, pl);
				}
				cooldown = 12;
			}
			if (spell.equals(1)) {
				// Shouldn't appear in cycle
			}
			if (spell.equals(2)) {
				if (upgrade.equals(0)) {
					Witherknight.Spear(p, pl);
				}
				if (upgrade.equals(1)) {
					Witherknight.Slash(p, pl);
				}
				if (upgrade.equals(2)) {
					Witherknight.Shred(p, pl);
				}
				if (upgrade.equals(3)) {
					Witherknight.Swarm(p, pl);
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

	public static void Duskwave(Player p, Plugin plugin) {
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
			List<Location> perpLine = Geometry.getLine(groundLoc, norm, count);
			for (int i = 0; i < 3; i++) {
				final int finalI = i;
				final int c = count;
				new BukkitRunnable() {
					@Override
					public void run() {
						if (finalI == 0) {
							for (Location l : perpLine) {
								p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 4, 0.1, 0, 0.1, 0);
								Utils.applyNearby(center, p, c, 1, c, (LivingEntity le) -> {
									le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
								});
							}
						} else {
							for (Location l : perpLine) {
								Utils.applyNearby(center, p, 1, 1, 1, (LivingEntity le) -> {
									le.damage(2, p);
									le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1));
								});
								p.getWorld().spawnParticle(Particle.SPELL_WITCH, l.clone().add(0, 1, 0), 3, 0, 0.5, 0, 0);
							}
						}
						if (c == 0) {
							w.playSound(start, Sound.ENTITY_WITHER_SHOOT, 1, 1);
						}
					}
				}.runTaskLater(plugin, 15 * i + count);
			}
		}
	}

	public static void Noxburst(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3));
		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);

		Location center = p.getLocation();
		int range = 5, count = 0, magnitude = 1;
		for (count = 1; count < range; count+=magnitude) {
			List<Location> circle = Geometry.getCirclePoints(center, Geometry.Plane.XZ, count, count * 10);
			Integer c = count;
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l : circle) {
						p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 4, 0.1, 0, 0.1, 0);
					}
				}
			}.runTaskLater(plugin, count);

			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l: circle) {
						p.getWorld().spawnParticle(Particle.SPELL_WITCH, l.clone().add(0, 1, 0), 3, 0, 0.5, 0, 0);
					}
				}
			}.runTaskLater(plugin, 20+count);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getEyeLocation(), 1, 0, 0, 0, 1);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
				Utils.applyNearby(center, p, range, 1, range, (LivingEntity le) -> {
					le.damage(2,p);
					le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1));
				});
			}
		}.runTaskLater(plugin, 20);
	}

	public static void Arcwither(Player p, Plugin plugin) {
		ArrayList<LivingEntity> affected = new ArrayList<>();
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
		affected.add(p);
		Archelper(p, affected, new RayTrace(p, 6, 1), plugin);
	}

	private static void Archelper(Player p, ArrayList<LivingEntity> affected, RayTrace ray, Plugin plugin) {
		World w = p.getWorld();
		for (int i = 1; i < 7; i++) {
			new BukkitRunnable() {
				public void run() {
					if (ray.hasNext()) {
						Location l = ray.next();
						w.spawnParticle(Particle.SPELL_WITCH, l, 2, 0, 0, 0, 0.05);
						Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {
							if (!affected.contains(le)) {
								w.playSound(l, Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
								w.spawnParticle(Particle.SPELL_WITCH, le.getEyeLocation(), 8, 0, 0.5, 0, 0.05);
								Utils.magicDamage(p, le, 2, plugin);
								le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1));
								le.setVelocity(new Vector(0, 0, 0));
								affected.add(le);
								LivingEntity le2 = Utils.getNearestEntity(l, affected, 5, 3, 5);
								if (le2 != null) {
									Vector diff = le2.getLocation().subtract(le.getLocation()).toVector();
									RayTrace v = new RayTrace(le.getEyeLocation(), diff, diff.length(), diff.length() / 7);
									new BukkitRunnable() {
										public void run() {
											Archelper(p, affected, v, plugin);
										}
									}.runTaskLater(plugin, 4);
								}
							}
						});
					}
				}
			}.runTaskLater(plugin, i);
		}
	}

	public static void Nightfall(Player p, Plugin plugin) {
		World w = p.getWorld();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3));
		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);

		Location center = Geometry.getGroundLocation(p.getLocation(), 6);
		int range = 8, count = 0, magnitude = 1;
		for (count = 1; count < range-1; count+=magnitude) {
			List<Location> circle = Geometry.getCirclePoints(center, Geometry.Plane.XZ, range-count, count * 10);
			Integer c = count;
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l : circle) {
						p.getWorld().spawnParticle(Particle.SMOKE_NORMAL, l, 4, 0.1, 0, 0.1, 0);
					}
				}
			}.runTaskLater(plugin, count);

			new BukkitRunnable() {
				@Override
				public void run() {
					for (Location l: circle) {
						p.getWorld().spawnParticle(Particle.SPELL_WITCH, l, 3, 0.1, 0.3, 0.1, 0);
					}
					p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
				}
			}.runTaskLater(plugin, 20+count*3);
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Utils.applyNearby(p.getLocation(), p, range, 1, range, (LivingEntity le) -> {
					le.damage(2, p);
					le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1));
					le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
					le.teleport(center);
				});
			}
		},	(20));
	}

	public static void Barrier(Player p, Plugin plugin) {
		p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, p.getEyeLocation(), 6, 0.3, 0.2, 0.3, 0);
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0));
	}

	public static void Drain(Player p, Plugin plugin) {
		p.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, p.getEyeLocation(), 4, 0.3, 0.2, 0.3, 0);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 0));
	}

	public static void Wisp(Player p, Plugin plugin) {
		p.getWorld().spawnParticle(Particle.CLOUD, p.getEyeLocation(), 6, 0.3, 0.2, 0.3, 0);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
	}

	public static void Rebirth(Player p, Plugin plugin) {
		p.getWorld().spawnParticle(Particle.HEART, p.getEyeLocation(), 6, 0.3, 0.2, 0.3, 0);
		p.setHealth(p.getHealth() + 1);
	}

	public static void Spear(Player p, Plugin plugin) {
		int range = 10;
		int space = 1;

		World w = p.getWorld();
		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 1);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3));
		p.setVelocity(p.getLocation().getDirection().setY(0).multiply(-0.5));
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
								w.spawnParticle(Particle.CRIT_MAGIC, l, 8, 0.1, 0.1, 0.1, 0);
								Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {
									le.damage(6, p);
									le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
									le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
								});
							}
						}
					}.runTaskLater(plugin, count);
				}

				for (int i = 0; i < 2; i++) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							w.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
							WitherSkull s = p.launchProjectile(WitherSkull.class);
							s.setVelocity(p.getLocation().getDirection());
							s.setShooter(p);
						}
					}, i*4);
				}
			}
		}.runTaskLater(plugin, 10);
	}

	public static void Slash(Player p, Plugin plugin) {
		World w = p.getWorld();

		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
		WitherSkull s1 = p.launchProjectile(WitherSkull.class);
		s1.setVelocity(p.getLocation().getDirection().multiply(2));
		s1.setShooter(p);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				w.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
				WitherSkull s2 = p.launchProjectile(WitherSkull.class);
				s2.setVelocity(p.getLocation().getDirection().multiply(2));
				s2.setShooter(p);
			}
		}, 10);
		new BukkitRunnable() {
			public void run() {
				double angle = p.getLocation().getYaw()*Math.PI/180;
				double startAngle = angle + Math.PI/2;
				double endAngle = angle - Math.PI/2;

				p.setVelocity(p.getLocation().getDirection().setY(0));
				Vector sweepOffset = new Vector(-Math.sin(angle), 0, Math.cos(angle)).multiply(3.3).setY(1);
				w.spawnParticle(Particle.SWEEP_ATTACK, p.getLocation().add(sweepOffset), 1, 0, 0, 0, 0);

				int points = 7;
				for (int i = 1; i < points+1; i++) {
					int finalI = i;
					new BukkitRunnable() {
						public void run() {
							double newAngle = startAngle + finalI * (endAngle - startAngle) / points;
							Vector vec = new Vector(-Math.sin(newAngle), 0, Math.cos(newAngle)).multiply(3);
							Location l = p.getEyeLocation().add(vec);
							w.spawnParticle(Particle.CRIT_MAGIC, l, 10, 0.2, 0.2, 0.2, 0);
							Utils.applyNearby(l, p, 1, 1, 1, (LivingEntity le) -> {
								le.damage(6, p);
							});
						}
					}.runTaskLater(plugin, i);
				}
				w.playSound(p.getLocation(), Sound.ENTITY_WITHER_HURT, 1, 1);
			}
		}.runTaskLater(plugin, 20);
	}

	public static void Shred(Player p, Plugin plugin) {
		World w = p.getWorld();
		double angle = p.getLocation().getYaw()*Math.PI/180;
		double startAngle = angle + Math.PI/2;
		double endAngle = angle - Math.PI/2;

		Vector sweepOffset = new Vector(-Math.sin(angle), 0.3, Math.cos(angle)).multiply(3.3);
		w.spawnParticle(Particle.SWEEP_ATTACK, p.getLocation().add(sweepOffset), 1, 0, 0, 0, 0);
		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_HURT, 1, 1);

		int points = 7;
		for (int i = 1; i < points+1; i++) {
			double newAngle = startAngle + i * (endAngle - startAngle) / points;
			Vector v1 = new Vector(-Math.sin(newAngle), 0, Math.cos(newAngle)).multiply(3);
			Vector v2 = new Vector(-Math.sin(newAngle), 0, Math.cos(newAngle)).multiply(2);
			new BukkitRunnable() {
				public void run() {
					Location l1 = p.getEyeLocation().add(v1);
					Location l2 = p.getEyeLocation().add(v2);
					w.spawnParticle(Particle.CRIT_MAGIC, l1, 10, 0.2, 0.2, 0.2, 0);
					w.spawnParticle(Particle.CRIT_MAGIC, l2, 10, 0.2, 0.2, 0.2, 0);
					Utils.applyNearby(l2, p, 1, 1, 1, (LivingEntity le) -> {
						le.damage(6, p);
						le.setVelocity(v2);
					});
				}
			}.runTaskLater(plugin, i);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					WitherSkull s = p.launchProjectile(WitherSkull.class);
					s.setVelocity(v2.setY(p.getLocation().getDirection().getY()));
					s.setShooter(p);
				}
			}, 10);
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				w.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
			}
		}, 10);
	}

	public static void Swarm(Player p, Plugin plugin) {
		World w = p.getWorld();
		double angle = p.getLocation().getYaw()*Math.PI/180;
		double startAngle = angle + Math.PI/2;
		double endAngle = angle - Math.PI/2;

		Vector XZdirection = new Vector(-Math.sin(angle), 0, Math.cos(angle));
		Vector sweepOffset = XZdirection.multiply(3.3).setY(1);
		w.spawnParticle(Particle.SWEEP_ATTACK, p.getLocation().add(sweepOffset), 1, 0, 0, 0, 0);
		w.playSound(p.getLocation(), Sound.ENTITY_WITHER_HURT, 1, 1);
		p.setVelocity(p.getLocation().getDirection().setY(0));

		int points = 7;
		for (int i = 1; i < points+1; i++) {
			double newAngle = startAngle + i * (endAngle - startAngle) / points;
			Vector v1 = new Vector(-Math.sin(newAngle), 0, Math.cos(newAngle)).multiply(3);
			Vector v2 = new Vector(-Math.sin(newAngle), 0, Math.cos(newAngle)).multiply(2);
			new BukkitRunnable() {
				public void run() {
					Location l1 = p.getEyeLocation().add(v1);
					Location l2 = p.getEyeLocation().add(v2);
					w.spawnParticle(Particle.CRIT_MAGIC, l1, 10, 0.2, 0.2, 0.2, 0);
					w.spawnParticle(Particle.CRIT_MAGIC, l2, 10, 0.2, 0.2, 0.2, 0);
					Utils.applyNearby(l2, p, 1, 1, 1, (LivingEntity le) -> {
						le.damage(6, p);
						le.setVelocity(v2);
					});
				}
			}.runTaskLater(plugin, i);
		}

		new BukkitRunnable() {
			public void run() {
				p.setVelocity(XZdirection.multiply(-0.5).setY(0.5));
				p.setMetadata("nofall", new FixedMetadataValue(plugin, true));
				for (int i = 0; i < 5; i++) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							w.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
							WitherSkull s = p.launchProjectile(WitherSkull.class);
							s.setVelocity(p.getLocation().getDirection());
							s.setShooter(p);
						}
					}, i*2);
				}
			}
		}.runTaskLater(plugin, 10);
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
					FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false, false, false);
					Location target = trap.getLocation();
					trap.remove();

					target.add(0, 12, 0);
					for (int i = 0; i < 5; i++) {
						int in = i;
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
							public void run() {
								FireworkPlayer.fire(target, Type.BURST, Color.GRAY, false, false, false);
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
					FireworkPlayer.fire(trap.getLocation(), Type.CREEPER, Color.SILVER, false, false, false);
					trap.remove();
				}
			}
		}.runTaskLater(pl, 600);
	}
}